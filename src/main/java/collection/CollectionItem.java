package collection;

import database.DBStorable;

import java.io.Serializable;
import java.util.Set;

public interface CollectionItem extends Comparable<CollectionItem>, DOMParseable, Serializable {
    Long getId();
    void setId(Long id);


    void setValue(String valueName, String value);
    String getValue(String valueName);
    String getFormat(String valueName);

    Set<String> getGettersList();
    Set<String> getSettersList();
}
