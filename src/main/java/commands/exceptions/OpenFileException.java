package commands.exceptions;

public class OpenFileException extends CommandException {
    public OpenFileException(String path, String cause) {
        super(String.format("Couldn't open file with path \"%s\". Cause: %s", path, cause));
    }
}

