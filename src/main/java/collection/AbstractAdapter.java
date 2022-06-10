package collection;

import collection.data.exceptions.InvalidFieldException;
import collection.exceptions.NoSuchValueException;
import database.DBRequest;
import database.DBStorable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

abstract public class AbstractAdapter implements DBStorable {

    protected final List<String> bannedSettersList;
    protected final Map<String, StringSetter> settersMap;
    protected final Map<String, StringGetter> gettersMap;
    protected final Map<String, String> formatMap;

    abstract protected String getDbName();
    protected final String db_name = getDbName();
    protected String user = "local";


    {
        formatMap = new HashMap<>();
        settersMap = new LinkedHashMap<>();
        gettersMap = new LinkedHashMap<>();
        bannedSettersList = new ArrayList<>();

        gettersMap.put("id", () -> String.valueOf(getId()));
        settersMap.put("id", id -> setId(Long.valueOf(id)));
        gettersMap.put("user", this::getUser);
        settersMap.put("user", this::setUser);


        bannedSettersList.add("id");
        bannedSettersList.add("user");
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public void setValue(String valueName, String value) {
        if(!settersMap.containsKey(valueName)) throw new NoSuchValueException(valueName);
        try {
            settersMap.get(valueName).accept(value);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new InvalidFieldException(valueName, value);
        }
    }

    @Override
    public String getValue(String valueName) {
        if(!gettersMap.containsKey(valueName)) throw new NoSuchValueException(valueName);
        try {
            return gettersMap.get(valueName).get();
        } catch (NullPointerException e) {
            return "null";
        }
    }

    @Override
    public String getFormat(String valueName) {
        return formatMap.getOrDefault(valueName, "");
    }

    @Override
    public Set<String> getGettersList() {
        return gettersMap.keySet();
    }

    @Override
    public Set<String> getSettersList() {
        List<String> keySet = new ArrayList<>(settersMap.keySet());
        keySet.removeAll(bannedSettersList);
        return new LinkedHashSet<>(keySet);
    }

    @Override
    public int compareTo(CollectionItem o) {
        return getId().compareTo(o.getId());
    }



    @Override
    public Element parse(Document document) {
        Element element = document.createElement("element");
        for(String valueName : gettersMap.keySet())
            element.appendChild(DOMParseable.createTextElement(document, valueName, getValue(valueName)));
        return element;
    }

    @Override
    public void parse(Element element) {
        NodeList nodeList = element.getChildNodes();
        Map<String, String> resultNull = new LinkedHashMap<>();
        for(String iter : settersMap.keySet())
            resultNull.put(iter, "null");

        for(int i=0;i<nodeList.getLength();i++) {
            Node node = nodeList.item(i);
            if(settersMap.containsKey(node.getNodeName())) {
                String name = node.getTextContent();
                resultNull.put(node.getNodeName(), node.getTextContent());
            }
        }
        for(String key : resultNull.keySet()) {
            setValue(key, resultNull.get(key));
        }
    }

    @Override
    public void parse(ResultSet set) throws SQLException {
        setId(set.getLong("id"));
        setUser(set.getString("user"));
    }

    @Override
    public DBRequest delete() {
        return new DBRequest("DELETE FROM "+db_name+" WHERE \"id\" = ?", getId());
    }

    protected interface StringGetter extends Supplier<String>, Serializable {

    }
    protected interface StringSetter extends Consumer<String>, Serializable {

    }

}
