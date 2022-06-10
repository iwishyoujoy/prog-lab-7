package collection.exceptions;

public class EmptyCollectionException extends CollectionException{
    public EmptyCollectionException() {
        super("The collection is empty!");
    }
}
