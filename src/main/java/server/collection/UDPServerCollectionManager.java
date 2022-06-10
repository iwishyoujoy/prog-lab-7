package server.collection;

import application.ConsolePrinter;
import application.Controller;
import collection.AbstractCollectionManager;
import collection.CollectionManager;
import collection.exceptions.CollectionException;
import collection.exceptions.NoSuchElemException;
import database.DBStorable;
import database.Database;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import server.AuthorizationManager;
import server.Message;
import server.UDPChannelCommunicator;
import server.exceptions.CommunicatingException;
import server.exceptions.InvalidLoginException;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


//этот класс действует как декорация для другого CollectionManager
//если мы вызываем методы напрямую, то они просто вызываются из wrappedManager (коллекции которую мы задекорировали)
//но кроме этого этот класс может запускать обработку запросов в другом потоке
//сервер запускается методом run() и закрывается методом close()

public class UDPServerCollectionManager<T extends DBStorable> implements CollectionManager<T>, Controller {
    private final AbstractCollectionManager<T> wrappedManager;
    private final UDPChannelCommunicator udpChannelCommunicator;
    private final Thread serverThread;
    private volatile State state; //volatile, что бы не кешировался и сервер распознал, когда изменилось состояние
    private final Database database;

    public String db_name = "collection";

    {
        database = Database.getInstance();
    }

    private class MessageSender implements Runnable {
        private final Message messageToSend;

        public MessageSender(Message messageToSend) {
            this.messageToSend = messageToSend;
        }

        @Override
        public void run() {
            try {
                udpChannelCommunicator.sendObject(messageToSend);
                ConsolePrinter.println(String.format("Sent message to %s: %s",
                        messageToSend.getAddressee().toString(), messageToSend)); //печатаем что отправляем
            } catch (IOException e) {
                ConsolePrinter.println(String.format("Failed to send message to %s: %s",
                        messageToSend.getAddressee().toString(), messageToSend)); //печатаем что отправляем
            }
        }
    }

    private class MessageProcessor implements Runnable {
        private final Message receivedMessage;
        private final Consumer<Message> messageSender;
        public MessageProcessor(Message receivedMessage, Consumer<Message> messageSender) {
            this.receivedMessage = receivedMessage;
            this.messageSender = messageSender;
        }

        @Override
        public void run() {
            Message messageToSend = processMessage(receivedMessage); //обрабатываем сообщение (см. метод processMessage()) и получаем ответ
            messageToSend.setAddressee(receivedMessage.getAddressee()); //устанавливаем ответу адрес отправителя
            messageSender.accept(messageToSend);
        }
    }

    public UDPServerCollectionManager(AbstractCollectionManager<T> wrappedManager, int port) {
        this.wrappedManager = wrappedManager;
        this.udpChannelCommunicator = new UDPChannelCommunicator(port, false); //создаем неблокирующий прием на порте port

        //логика сервера
        Runnable server = () -> {
            class MessageSenderExecutor {
                private final Executor executor;

                public MessageSenderExecutor(Executor executor) {
                    this.executor = executor;
                }

                public void executeSendMessage(Message message) {
                    executor.execute(new MessageSender(message));
                }
            }
            MessageSenderExecutor messageSenderExecutor = new MessageSenderExecutor(Executors.newFixedThreadPool(4));
            state = State.RUNNING;
            Thread readMessageThread = new Thread(udpChannelCommunicator);
            readMessageThread.setDaemon(true);
            readMessageThread.start();
            ConsolePrinter.println("Server started");
            while (state == State.RUNNING) {
                try {
                    Message receivedMessage = udpChannelCommunicator.getReceivedObject(); //пытаемся получить данные
                    ConsolePrinter.println(String.format("Received message from %s: %s",
                            receivedMessage.getAddressee().toString(), receivedMessage)); //печатаем что получили
                    Thread processMessageThread = new Thread(new MessageProcessor(receivedMessage, (messageSenderExecutor::executeSendMessage)));
                    processMessageThread.setDaemon(true);
                    processMessageThread.start();
                } catch (CommunicatingException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ConsolePrinter.println("Server has been stopped");
        };
        serverThread = new Thread(server);
        serverThread.setDaemon(true); //делаем фоновым потоком

        //типо менеджер команд коллекции
        //ключи - название команды
        //значения - функциональные интерфейсы Function<Message, Message>
        //то есть на вход подается Message и вернутся должен тоже Message
        //любое пойманное исключение поймается в методе processMessage() и вместо ответа типа SUCCESS отправится тип ERROR с пойманный исключение внутри
        collectionCommandManager.put("sort", (msg) -> {
            sort();
            return new Message(Message.Type.SUCCESS);
        });
        collectionCommandManager.put("clear", (msg) -> {
            String login = (String) msg.get("login");
            clear(login);
            return new Message(Message.Type.SUCCESS);
        });
        collectionCommandManager.put("size", (msg) ->
                new Message(Message.Type.SUCCESS)
                        .put("size", size())
        );
        collectionCommandManager.put("add", (msg) -> {
            T element = getItemClass().cast(msg.get("element"));
            return new Message(Message.Type.SUCCESS)
                    .put("added", add(element));
        });
        collectionCommandManager.put("generateNew", (msg) ->
                new Message(Message.Type.SUCCESS)
                        .put("item", generateNew())
        );
        collectionCommandManager.put("getById", (msg) -> {
            Long id = (Long) msg.get("id");
            return new Message(Message.Type.SUCCESS)
                    .put("item", getById(id));
        });
        collectionCommandManager.put("removeIf", (msg) -> {
            String user = (String) msg.get("login");
            SerializablePredicate<? super T> filter = (SerializablePredicate<? super T>) msg.get("filter");
            return new Message(Message.Type.SUCCESS)
                    .put("removed", removeIf(user, filter));
        });
        collectionCommandManager.put("removeById", (msg) -> {
            String user = (String) msg.get("login");
            Long id = (Long) msg.get("id");
            return new Message(Message.Type.SUCCESS)
                    .put("removed", removeById(user, id));
        });
        collectionCommandManager.put("remove", (msg) -> {
            String user = (String) msg.get("login");
            Integer index = (Integer) msg.get("index");
            return new Message(Message.Type.SUCCESS)
                    .put("removed", remove(user, index));
        });
        collectionCommandManager.put("getInfo", (msg) ->
                new Message(Message.Type.SUCCESS)
                        .put("info", getInfo())
        );
        collectionCommandManager.put("toList", (msg) ->
                new Message(Message.Type.SUCCESS)
                        .put("list", toList())
        );
    }

    private final Map<String, Function<Message, Message>> collectionCommandManager;

    {
        collectionCommandManager = new HashMap<>();
    }

    @Override
    public void close() {
        state = State.CLOSING;
    }

    @Override
    public void run() {
        parse();
        serverThread.start();
    }

    public void parse() {
        try {
            ResultSet resultSet = database.executeQuery(DBStorable.selectAll(db_name));
            while (resultSet.next()) {
                T item = generateNew();
                item.parse(resultSet);
                wrappedManager.add(item);
            }
        } catch (SQLException e) {
            throw new CommunicatingException(e.getMessage());
        }
    }

    synchronized public boolean login(String login, String password) {
        try {
            Database database = Database.getInstance();
            ResultSet foundUsers = database.executeQuery("SELECT * FROM users WHERE \"user\" = ?", login);
            if (!foundUsers.next()) return false;
            String dbHashedPassword = foundUsers.getString("password");
            String salt = foundUsers.getString("salt");
            String hashedPassword = AuthorizationManager.hashPassword(password, salt);
            hashedPassword = AuthorizationManager.shorterString(hashedPassword, 255);
            boolean success = dbHashedPassword.equals(hashedPassword);
            if(success)
                ConsolePrinter.println("Successfully login user " + login);
            return success;
        } catch (SQLException e) {
            throw new CommunicatingException(e.getMessage());
        }
    }

    synchronized public boolean register(String login, String password) {
        try {
            Database database = Database.getInstance();
            ResultSet foundUsers = database.executeQuery("SELECT * FROM users WHERE \"user\" = ?", login);
            if (foundUsers.next()) return false;
            String salt = String.valueOf((int) (Math.random()*1000));
            String hashedPassword = AuthorizationManager.hashPassword(password, salt);
            hashedPassword = AuthorizationManager.shorterString(hashedPassword, 255);
            database.executeUpdate("INSERT INTO users VALUES(?,?,?)", login, hashedPassword, salt);
            ConsolePrinter.println("Successfully registered user " + login);
            return true;
        } catch (SQLException e) {
            throw new CommunicatingException(e.getMessage());
        }
    }

    //обработка полученного сообщения
    synchronized private Message processMessage(Message message) {
        try {
            String login = (String) message.get("login");
            String password = (String) message.get("password");
            if (message.getType() == Message.Type.LOGIN) {
                return new Message(Message.Type.SUCCESS)
                        .put("accepted", login(login, password));
            }
            if (message.getType() == Message.Type.REGISTER) {
                return new Message(Message.Type.SUCCESS)
                        .put("accepted", register(login, password));
            }
            if(!login(login, password)) throw new InvalidLoginException();
            if (message.getType() != Message.Type.COLLECTION)
                throw new CommunicatingException("Not collection type message"); //проверяем на тип COLLECTION
            String commandName = (String) message.get("commandName"); //получаем название команды
            if (!collectionCommandManager.containsKey(commandName))
                throw new CommunicatingException(String.format("Not such command %s on server", commandName)); //проверяем можем ли обработать
            return collectionCommandManager.get(commandName).apply(message); //возвращаем ответ
        } catch (CollectionException | CommunicatingException e) {
            return new Message(Message.Type.ERROR)
                    .put("error", e); //если ловим ошибку, то возвращаем ответ типа ERROR
        }
    }

    @Override
    public Class<T> getItemClass() {
        return wrappedManager.getItemClass();
    }

    @Override
    public void sort() {
        wrappedManager.sort();
    }

    synchronized public void clear(String user) {
        try {
            ResultSet resultSet = database.executeQuery(DBStorable.selectByUser(db_name, user));
            List<Long> idList = new ArrayList<>();
            while(resultSet.next()) idList.add(resultSet.getLong("id"));
            idList.forEach(wrappedManager::removeById);
            database.executeUpdate(DBStorable.deleteUserOwned(db_name, user));
        } catch (SQLException e) {
            throw new CommunicatingException(e.getMessage());
        }
    }

    @Override
    public void clear() {
        try {
            database.executeUpdate(DBStorable.deleteAll(db_name));
            wrappedManager.clear();
        } catch (SQLException e) {
            throw new CommunicatingException(e.getMessage());
        }
    }

    @Override
    public int size() {
        return wrappedManager.size();
    }

    @Override
    public boolean isEmpty() {
        return wrappedManager.isEmpty();
    }

    @Override
    synchronized public boolean add(T element) {
        try {
            database.executeUpdate(element.insert());
            ResultSet resultSet = database.executeQuery("SELECT max(\"id\") FROM \"musicBands\"");
            resultSet.next();
            element.setId(resultSet.getLong("max"));
            return wrappedManager.add(element);
        } catch (SQLException e) {
            throw new CommunicatingException(e.getMessage());
        }
    }

    @Override
    public T generateNew() {
        return wrappedManager.generateNew();
    }

    synchronized public boolean removeById(String user, Long id) {
        try {
            if(!wrappedManager.getById(id).getUser().equals(user)) throw new NoSuchElemException();
            database.executeUpdate(wrappedManager.getById(id).delete());
            return wrappedManager.removeById(id);
        } catch (SQLException e) {
            throw new CommunicatingException(e.getMessage());
        }
    }

    @Override
    synchronized public boolean removeById(Long id) {
        try {
            database.executeUpdate(wrappedManager.getById(id).delete());
            return wrappedManager.removeById(id);
        } catch (SQLException e) {
            throw new CommunicatingException(e.getMessage());
        }
    }

    @Override
    public T getById(Long id) {
        return wrappedManager.getById(id);
    }

    synchronized public boolean removeIf(String user, SerializablePredicate<? super T> filter) {
        List<T> filtered = wrappedManager.toList()
                .stream()
                .filter(iter -> iter.getUser().equals(user))
                .filter(filter)
                .collect(Collectors.toList());
        if(filtered.isEmpty()) return false;
        filtered.forEach(iter -> removeById(iter.getId()));
        return true;
    }

    @Override
    synchronized public boolean removeIf(SerializablePredicate<? super T> filter) {
        List<T> filtered = wrappedManager.toList()
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
        if(filtered.isEmpty()) return false;
        filtered.forEach(iter -> removeById(iter.getId()));
        return true;
    }


    synchronized public boolean remove(String user, int index) {
        try {
            Long id = wrappedManager.getIdByIndex(index);
            T item = wrappedManager.getById(id);
            if(!item.getUser().equals(user)) throw new NoSuchElemException();
            database.executeUpdate(item.delete());
            return wrappedManager.remove(index);
        } catch (SQLException e) {
            throw new CommunicatingException(e.getMessage());
        }
    }

    @Override
    synchronized public boolean remove(int index) {
        try {
            Long id = wrappedManager.getIdByIndex(index);
            database.executeUpdate(wrappedManager.getById(id).delete());
            return wrappedManager.remove(index);
        } catch (SQLException e) {
            throw new CommunicatingException(e.getMessage());
        }
    }


    @Override
    public Map<String, String> getInfo() {
        return wrappedManager.getInfo();
    }

    @Override
    public List<T> toList() {
        return wrappedManager.toList();
    }

    @Override
    public Element parse(Document document) {
        return wrappedManager.parse(document);
    }

    @Override
    public void parse(Element element) {
        wrappedManager.parse(element);
    }
}
