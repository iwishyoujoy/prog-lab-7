package server;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//сервер и клиент обмениваются Message'ами
//в Message можно поместить Object'ы по ключу с помощью метода put()
//а взять с помощью get()
public class Message implements Serializable {
    private final Map<String, Object> data;
    private SocketAddress addressee; //UDPCommunicator'у нужно знать куда отправлять, поэтому перед отправкой, нужно выставлять адрес
    private final Type type;

    {
        data = new HashMap<>();
    }

    public Message(Type type) {
        this.type = type;
    }


    public void setAddressee(SocketAddress addressee) {
        this.addressee = addressee;
    }

    public SocketAddress getAddressee() {
        return addressee;
    }

    public Message put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public Object get(String key) {
        return data.get(key);
    }

    public Type getType() {
        return type;
    }


    // поле type нужно нам для предварительного распределения и обработки сообщения
    // (например, типы PARTS перехватываются еще на этапе получения сообщения)
    // а тип COLLECTION свидетельствует о взаимодействии с коллекцией
    // тип ERROR приходит на сторону клиенту, если возникло исключение в коллекции (пример - EmptyCollectionException), где(в клиенте) пробрасывается throw'ом
    public enum Type {
        COLLECTION,
        LOGIN,
        REGISTER,
        SUCCESS,
        ERROR,
        PARTS
    }



    @Override
    public String toString() {
        List<String> dataList = data.keySet().stream().collect(
                ArrayList::new,
                (list, key) -> list.add(String.format("%s=%s", key, AuthorizationManager.shorterString(data.get(key).toString(), 50))),
                ArrayList::addAll
        );
        String dataString = String.join(";", dataList);
        return String.format("Type = %s; Data = {%s}", type, dataString);
    }
}
