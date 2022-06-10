package server;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface UDPCommunicator {
    int BUFFER_CAPACITY = 512; //размер отводимого буфера

    void sendObject(Message objectToSend) throws IOException;
    Message receiveObject() throws IOException, ClassNotFoundException;

    //десериализация буфера
    static Object deserializeBuffer(ByteBuffer byteBuffer) throws IOException, ClassNotFoundException {
        return deserializeByteArray(byteBuffer.array());
    }

    //десериализация массива байтов
    static Object deserializeByteArray(byte[] array)  throws IOException, ClassNotFoundException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(array))) {
            return objectInputStream.readObject();
        }
    }

    //сериализация объекта в массив байтов
    static byte[] serializeToByteArray(Object object) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        }
    }

    //сериализация объекта в буфер
    static ByteBuffer serializeToBuffer(Object object) throws IOException {
        return ByteBuffer.wrap(serializeToByteArray(object));
    }

    //деление буфера на равные (по BUFFER_CAPACITY) кусочки
    static List<ByteBuffer> sliceByteBuffer(ByteBuffer bufferToSlice) {
        if (bufferToSlice.limit() <= BUFFER_CAPACITY)  //если помещается и так, то просто возвращаем лист с одним элементом
            return Collections.singletonList(bufferToSlice);
        else {
            List<ByteBuffer> bufferList = new ArrayList<>(); //создаем лист, куда будем помещать наши кусочки
            int bufferToSliceLimit = bufferToSlice.limit(); //размер данных в разделяемом буфере
            int number = (int) Math.ceil((double) bufferToSlice.limit() / (double) BUFFER_CAPACITY); //получаем размер кусочков
            for (int i = 0, position = 0; i < number; i++) {
                int size = (i != number - 1) ? BUFFER_CAPACITY : bufferToSliceLimit - (number - 1) * BUFFER_CAPACITY; //вычисляем размер кусочка (либо BUFFER_CAPACITY, либо что осталось)
                byte[] curr = new byte[size];
                System.arraycopy(bufferToSlice.array(), position, curr, 0, curr.length); //копируем из массива байтов необходимый кусочек
                position += size; //сдвигаем позицию чтения из массива байтов
                bufferList.add(ByteBuffer.wrap(curr)); //добавляем кусочек
            }
            return bufferList;
        }
    }

    static ByteBuffer mergeBuffers(List<ByteBuffer> byteBuffers) {
        if (byteBuffers == null || byteBuffers.size() == 0) {
            return ByteBuffer.allocate(0);
        } else if (byteBuffers.size() == 1) {
            return byteBuffers.get(0);
        } else {
            ByteBuffer fullContent = ByteBuffer.allocate(
                    byteBuffers.stream()
                            .mapToInt(Buffer::limit)
                            .sum()
            );
            byteBuffers.forEach(fullContent::put);
            fullContent.flip();
            return fullContent;
        }
    }
}
