package server.collection;

import collection.CollectionItem;
import collection.CollectionManager;
import database.DBStorable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import server.AuthorizationManager;
import server.Message;
import server.UDPSocketCommunicator;
import server.exceptions.CommunicatingException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

//этот класс работает как CollectionManager
//но все методы переопределены так, что они отправляют Message типа Collection на сервак
//и потом получает Message'и типа SUCCESS или ERROR
//работает в блокирующем режиме (но с отведенным временем на получение данных)

abstract public class UPDClientCollectionManager<T extends DBStorable>
        implements CollectionManager<T>, AuthorizationManager {
    private final UDPSocketCommunicator udpSocketCommunicator;
    private final SocketAddress serverAddress;

    public UPDClientCollectionManager(String hostName, int port) {
        udpSocketCommunicator = new UDPSocketCommunicator(); //создаем коммуникатор
        udpSocketCommunicator.setTimeOut(5000); //устанавливаем время на получение данных
        serverAddress = new InetSocketAddress(hostName, port); //создаем адрес сервера
    }


    public boolean isDone = false;

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public boolean login(String login, String password) {
        setLogin(login);
        setPassword(AuthorizationManager.hashPassword(password, defaultSalt));
        Message respond = sendMessage(createBaseCommand(Message.Type.LOGIN));
        Boolean success = (Boolean) respond.get("accepted");
        isDone = success;
        return success;
    }

    @Override
    public boolean register(String login, String password) {
        setLogin(login);
        setPassword(AuthorizationManager.hashPassword(password, defaultSalt));
        Message respond = sendMessage(createBaseCommand(Message.Type.REGISTER));
        Boolean success = (Boolean) respond.get("accepted");
        isDone = success;
        return success;
    }

    private String login = "login";
    private String password = "password";

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private Message createBaseCommand(Message.Type type) {
        return new Message(type)
                .put("login", login)
                .put("password", password);
    }

    //удобный метод для создания MESSAGE типа COLLECTION с заданным именем
    private Message createCommand(String name) {
        return createBaseCommand(Message.Type.COLLECTION)
                .put("commandName", name);
    }

    //отправка и получение ответа
    private Message sendMessage(Message command) {
        try {
            command.setAddressee(serverAddress); //устанавливаем адрес сервака
            udpSocketCommunicator.sendObject(command); //отправляем сообщение
            Message respond = udpSocketCommunicator.receiveObject(); //ждем ответа (блокается), либо прокидывает исключение если ждали большое отведенного времени
            if (respond.getType() == Message.Type.ERROR) { //если тип ответа ERROR то пробрасываем его (как если бы мы обращались к локальной коллекции)
                throw (RuntimeException) respond.get("error");
            }
            return respond;
        } catch (IOException | ClassNotFoundException e) {
            throw new CommunicatingException(e.getMessage()); //если вышло время или бред какой-нибудь то оборачиваем в ComminicatingException(наследник CommandException), поэтому просто напечатается
        }
    }

    @Override
    public void sort() {
        sendMessage(createCommand("sort"));
    }

    @Override
    public void clear() {
        sendMessage(createCommand("clear"));
    }

    @Override
    public int size() {
        return (Integer) sendMessage(createCommand("size")).get("size");
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean add(T element) {
        return (Boolean) sendMessage(createCommand("add")
                .put("element", element)
        ).get("added");
    }

    @Override
    public T generateNew() {
        T item = getItemClass().cast(sendMessage(createCommand("generateNew")).get("item"));
        item.setUser(login);
        return item;
    }

    @Override
    public T getById(Long id) {
        return getItemClass().cast(sendMessage(createCommand("getById")
                .put("id", id)
        ).get("item"));
    }

    //SerializablePredicate находится в интерфейсе CollectionManager, по умолчанию функциональные интерфейсы не сериализуются
    @Override
    public boolean removeIf(SerializablePredicate<? super T> filter) {
        return (Boolean) sendMessage(createCommand("removeIf").put("filter", filter)).get("removed");
    }

    @Override
    public boolean removeById(Long id) {
        return (Boolean) sendMessage(createCommand("removeById")
                .put("id", id)).get("removed");
    }

    @Override
    public boolean remove(int index) {
        return (Boolean) sendMessage(createCommand("remove")
                .put("index", index)).get("removed");
    }

    @Override
    public Map<String, String> getInfo() {
        return (Map<String, String>) sendMessage(createCommand("getInfo")).get("info");
    }

    @Override
    public List<T> toList() {
        return (List<T>) sendMessage(createCommand("toList")).get("list");
    }

    @Override
    public Element parse(Document document) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void parse(Element element) {
        throw new UnsupportedOperationException();
    }
}
