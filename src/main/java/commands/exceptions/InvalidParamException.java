package commands.exceptions;

public class InvalidParamException extends CommandException{
    public InvalidParamException() {
        super("Invalid parameter!");
    }
}
