package server;

import server.exceptions.CommunicatingException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class UDPSocketCommunicator implements UDPCommunicator {
    private final DatagramSocket datagramSocket;

    public UDPSocketCommunicator() { //создать сокет на свободном порте
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new CommunicatingException(e.getMessage());
        }
    }


    @Override
    public void sendObject(Message objectToSend) throws IOException {
        SocketAddress address = objectToSend.getAddressee();

        byte[] buffer = UDPCommunicator.serializeToByteArray(objectToSend);
        if(buffer.length <= BUFFER_CAPACITY) //если помещается, то отправляем так
            datagramSocket.send(new DatagramPacket(buffer, buffer.length, address));
        else { //иначе
            List<ByteBuffer> bufferList = UDPCommunicator.sliceByteBuffer(ByteBuffer.wrap(buffer)); //делим его на кусочки
            Message startPartReceiving = new Message(Message.Type.PARTS)
                    .put("buffers", bufferList.size()); //создаем сообщение-извещение об отправке по кускам
            byte[] startPartReceivingData = UDPCommunicator.serializeToByteArray(startPartReceiving);
            datagramSocket.send(new DatagramPacket(startPartReceivingData, startPartReceivingData.length, address)); //отправляем извещение
            for (ByteBuffer iter : bufferList) {
                byte[] part = iter.array();
                datagramSocket.send(new DatagramPacket(part, part.length, address));  //отправляем все кусочки
            }
        }
    }

    @Override
    public Message receiveObject() throws IOException, ClassNotFoundException {
        DatagramPacket packet = receivePacket(); //получаем пакет
        Message sentMessage = (Message) UDPCommunicator.deserializeByteArray(packet.getData()); //десереализуем в сообщение
        if (sentMessage.getType() == Message.Type.PARTS) { //если сообщение типа PARTS
            int buffers = (Integer) sentMessage.get("buffers"); //получаем количество частей
            List<ByteBuffer> receivedBuffers = new ArrayList<>();  //создаем лист, куда будем скидывать полученные части
            for (int i = 0; i < buffers; ) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(receivePacket().getData());
                byteBuffer.position(0); //устанавливаем позицию ноль
                receivedBuffers.add(byteBuffer); //скидываем в лист
                i++;
            }
            sentMessage = (Message) UDPCommunicator.deserializeBuffer(UDPCommunicator.mergeBuffers(receivedBuffers)); //объединяем полученные буферы
        }
        sentMessage.setAddressee(packet.getSocketAddress()); //устанавливаем отправителя
        return sentMessage;
    }

    //устанавливаем время, которое будет ждать сокет чтобы получить пакет
    public void setTimeOut(int millis) {
        try {
            datagramSocket.setSoTimeout(millis);
        } catch (SocketException e) {
            throw new CommunicatingException(e.getMessage());
        }
    }

    //получение пакета
    private DatagramPacket receivePacket() throws IOException {
        byte[] buffer = new byte[BUFFER_CAPACITY];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        datagramSocket.receive(packet);
        return packet;
    }
}
