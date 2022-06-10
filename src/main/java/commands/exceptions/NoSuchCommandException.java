package commands.exceptions;

public class NoSuchCommandException extends CommandException{
    public NoSuchCommandException(String name) {
        super(String.format("No such command with name \"%s\"", name));
    }
}
