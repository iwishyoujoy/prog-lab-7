package collection.data.exceptions;

public class InvalidFieldException extends InvalidDataException {
    public InvalidFieldException(String field, String value) {
        super(String.format("Can't set value \"%s\" for field \"%s\"", value, field));
    }
    //public InvalidFieldException(String field, String value, String cause) {
      //  super(String.format("Can't set value \"%s\" for field \"%s\". Cause: %s", value, field, cause));
    //}
}
