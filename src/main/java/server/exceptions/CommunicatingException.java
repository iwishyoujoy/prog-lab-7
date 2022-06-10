package server.exceptions;

import commands.exceptions.CommandException;

public class CommunicatingException extends CommandException {
    public CommunicatingException(String message) {
        super(String.format("Communicating exception! Cause: %s", message));
    }

}
