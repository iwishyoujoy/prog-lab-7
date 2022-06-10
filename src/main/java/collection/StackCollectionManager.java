package collection;

import collection.exceptions.EmptyCollectionException;
import collection.exceptions.NoSuchElemException;
import collection.exceptions.UniqueIdException;

import java.util.*;

abstract public class StackCollectionManager<T extends CollectionItem> extends AbstractCollectionManager<T> {
    private final Stack<T> collection;

    {
        collection = new Stack<>();
    }

    @Override
    protected Stack<T> getCollection() {
        return collection;
    }



    @Override
    public boolean add(T element) {
        for (T iter: collection) {
            if(element.getId().equals(iter.getId())){
                throw new UniqueIdException();
            }
        }
        collection.push(element);
        return true;
    }

    @Override
    public Long getIdByIndex(int index) {
        if(collection.size()<=index) throw new NoSuchElemException();
        return collection.get(index).getId();
    }

    @Override
    public boolean remove(int index) {
        if(index>=size()|| index<0) return false;
        collection.remove(index);
        return true;
    }

    @Override
    public void sort() {
        Collections.sort(collection);
    }

    @Override
    public Map<String, String> getInfo() {
        Map<String, String> infoTable = super.getInfo();
        infoTable.put("type", "Stack");
        return infoTable;
    }
}
