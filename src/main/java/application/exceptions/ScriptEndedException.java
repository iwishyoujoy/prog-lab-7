package application.exceptions;

public class ScriptEndedException extends RuntimeException {
    public ScriptEndedException() {
        super("End of the script detected");
    }
}
