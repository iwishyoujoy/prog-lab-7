package collection;

import com.sun.org.apache.xalan.internal.xsltc.DOM;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface CollectionManager<T extends CollectionItem> extends DOMParseable {
    void sort();
    void clear();
    int size();
    boolean isEmpty();
    Class<T> getItemClass();

    boolean add(T element);
    T generateNew();
    T getById(Long id);

    boolean removeIf(SerializablePredicate<? super T> filter);
    boolean removeById(Long id);

    boolean remove(int index);

    Map<String, String> getInfo();
    List<T> toList();

    interface SerializablePredicate<T> extends Predicate<T>, Serializable {

    }
}
