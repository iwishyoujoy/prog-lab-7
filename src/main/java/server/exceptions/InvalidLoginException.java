package server.exceptions;

public class InvalidLoginException extends CommunicatingException {
    public InvalidLoginException() {
        super("Wrong login or password!");
    }
}
