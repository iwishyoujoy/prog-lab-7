package collection.exceptions;

public class UniqueIdException extends CollectionException{
    public UniqueIdException() {
        super("ID already exists!");
    }
}
