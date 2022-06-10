package application.exceptions;

public class CriticalErrorException extends RuntimeException{
    public CriticalErrorException() {
        super("Application has been closed!");
    }

    public CriticalErrorException(String cause) {
        super(String.format("Application has been closed! Cause: %s", cause));
    }
}
