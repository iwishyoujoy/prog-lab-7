package collection;

import collection.exceptions.EmptyCollectionException;
import collection.exceptions.NoSuchElemException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

abstract public class AbstractCollectionManager<T extends CollectionItem> implements CollectionManager<T>  {
    abstract protected Collection<T> getCollection();
    private LocalDateTime initTime;

    {
        initTime = LocalDateTime.now();
    }

    protected Long generateId() {
        final List<Long> idList = new ArrayList<>();
        for (T iter: getCollection()) {
            idList.add(iter.getId());
        }
        try {
            return Collections.max(idList) + 1;
        } catch (NoSuchElementException e) {
            return 1L;
        }
    }

    @Override
    public int size() {
        return getCollection().size();
    }

    @Override
    public void clear() {
        getCollection().clear();
    }

    @Override
    public boolean isEmpty() {
        return getCollection().isEmpty();
    }

    @Override
    public boolean removeIf(SerializablePredicate<? super T> filter) {
        return getCollection().removeIf(filter);
    }

    @Override
    public T getById(Long id) {
        for(T iter : getCollection()) {
            if(iter.getId().equals(id)) return iter;
        }
        throw new NoSuchElemException();
    }


    abstract public Long getIdByIndex(int index);

    @Override
    public boolean removeById(Long id) {
        return getCollection().removeIf(item -> item.getId().equals(id));
    }

    @Override
    public Map<String, String> getInfo() {
        Map<String, String> infoTable = new LinkedHashMap<>();
        infoTable.put("size", String.valueOf(size()));
        infoTable.put("initializationTime", initTime.toString());
        return infoTable;
    }

    @Override
    public List<T> toList() {
        return new ArrayList<>(getCollection());
    }

    @Override
    public Element parse(Document document) {
        Element root = document.createElement("collection");
        root.appendChild(DOMParseable.createTextElement(document, "initializationTime", initTime.toString()));
        root.appendChild(DOMParseable.createTextElement(document, "size", String.valueOf(size())));

        Element elements = document.createElement("elements");
        for(T iter : getCollection()) {
            elements.appendChild(iter.parse(document));
        }
        root.appendChild(elements);
        return root;
    }

    @Override
    public void parse(Element element) {
        initTime = LocalDateTime.parse(element.getElementsByTagName("initializationTime").item(0).getTextContent());

        NodeList nodeList = element.getElementsByTagName("elements").item(0).getChildNodes();
        for(int i=0;i< nodeList.getLength();i++) {
            T t = generateNew();
            Node node = nodeList.item(i);
            if(node.getNodeType()==Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                t.parse(eElement);
                add(t);
            }
        }
    }
}
