package collection.exceptions;

import collection.CollectionItem;

public class NoSuchElemException extends CollectionException {
    public NoSuchElemException() {
        super("No such element!");
    }
}
