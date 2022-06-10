package server;

import server.exceptions.CommunicatingException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

public class UDPChannelCommunicator implements UDPCommunicator, Runnable {

    private final DatagramChannel datagramChannel; //канал для приема


    private final Map<SocketAddress, ServerClient> session;
    private final BlockingQueue<Message> receivedMessages;

    {
        session = new HashMap<>();
        receivedMessages = new ArrayBlockingQueue<>(10);
    }

    public UDPChannelCommunicator(int port, boolean blocking) { //создаем канал на конкретном порту
        try {
            datagramChannel = DatagramChannel.open();
            datagramChannel.bind(new InetSocketAddress(port));
            datagramChannel.configureBlocking(blocking);
        } catch (IOException e) {
            throw new CommunicatingException(e.getMessage());
        }
    }

    public UDPChannelCommunicator(boolean blocking) { //создаем канал на любой свободном порту
        try {
            datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(blocking);
        } catch (IOException e) {
            throw new CommunicatingException(e.getMessage());
        }
    }


    //метод для отправки сообщения
    public void sendObject(Message objectToSend) throws IOException {
        SocketAddress address = objectToSend.getAddressee(); //получаем адрес для отправки

        ByteBuffer buffer = UDPCommunicator.serializeToBuffer(objectToSend); //сериализуем сообщение
        if (buffer.limit() <= BUFFER_CAPACITY)  //если данные помещаются в максимальный размер буфера, то просто отправляем
            datagramChannel.send(buffer, address);
        else { //иначе
            List<ByteBuffer> bufferList = UDPCommunicator.sliceByteBuffer(buffer); //делим его на кусочки
            Message startPartReceiving = new Message(Message.Type.PARTS)
                    .put("buffers", bufferList.size()); //создаем сообщение типа PARTS и помещаем туда количество кусочков
            datagramChannel.send(UDPCommunicator.serializeToBuffer(startPartReceiving), address); //отправляем извещение об отправке по кусочкам
            for (ByteBuffer iter : bufferList) {
                datagramChannel.send(iter, address);  //отправляем все кусочки
            }
        }
    }


    @Override
    public void run() {
        try {
            while (true) {
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY); //создаем буфер
                SocketAddress clientAddress = datagramChannel.receive(buffer); //получаем данные с канала
                if(clientAddress == null) continue;
                buffer.flip();
                if(!session.containsKey(clientAddress)) session.put(clientAddress, new ServerClient());
                ServerClient client = session.get(clientAddress);
                Message message = client.receiveBuffer(buffer);
                if(message==null) continue;
                if(message.getType()== Message.Type.PARTS) {
                    int buffers = (Integer) message.get("buffers");
                    client.startReceiving(buffers);
                } else {
                    message.setAddressee(clientAddress);
                    receivedMessages.add(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Message getReceivedObject() throws InterruptedException {
        return receivedMessages.take();
    }

    @Deprecated
    public Message receiveObject() throws IOException, ClassNotFoundException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY); //создаем буфер
        SocketAddress clientAddress = datagramChannel.receive(buffer); //получаем данные с канала
        if (clientAddress == null) return null; //если вернуло null (может быть только в случае non-blocking режима)
        Message sentMessage = (Message) UDPCommunicator.deserializeBuffer(buffer); //получаем сообщение
        if (sentMessage.getType() == Message.Type.PARTS) { //если сообщение типа PARTS
            int buffers = (Integer) sentMessage.get("buffers"); //получаем количество частей
            List<ByteBuffer> receivedBuffers = new ArrayList<>();  //создаем лист, куда будем скидывать полученные части
            for (int i = 0; i < buffers; ) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_CAPACITY);
                if (datagramChannel.receive(byteBuffer) == null) continue; //получаем кусочек
                byteBuffer.flip();
                receivedBuffers.add(byteBuffer); //скидываем в лист
                i++;
            }
            sentMessage = (Message) UDPCommunicator.deserializeBuffer(UDPCommunicator.mergeBuffers(receivedBuffers)); //объединяем полученные буферы
        }
        sentMessage.setAddressee(clientAddress);
        return sentMessage;
    }

    private static class ServerClient {
        private final List<ByteBuffer> receivedBuffers;
        private boolean receivingParts = false;
        private int partsToReceive = 0;
        private int receivedParts = 0;

        {
            receivedBuffers = new ArrayList<>();
        }

        public Message receiveBuffer(ByteBuffer byteBuffer) throws IOException, ClassNotFoundException {
            if(!receivingParts)
                return (Message) UDPCommunicator.deserializeBuffer(byteBuffer);
            else {
                receivedBuffers.add(byteBuffer);
                receivedParts++;
                if(partsToReceive == receivedParts) {
                    receivingParts = false;
                    Message message = (Message) UDPCommunicator.deserializeBuffer(UDPCommunicator.mergeBuffers(receivedBuffers));
                    receivedBuffers.clear();
                    return message;
                }
                return null;
            }
        }

        public void startReceiving(int partsToReceive) {
            receivingParts = true;
            this.partsToReceive = partsToReceive;
            receivedParts = 0;
        }

    }

}
