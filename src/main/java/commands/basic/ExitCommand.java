package commands.basic;

import application.Controller;
import application.exceptions.CriticalErrorException;
import commands.AbstractCommand;
import commands.CommandParameters;

public class ExitCommand extends AbstractCommand {
    private final Controller controller;
    public ExitCommand(Controller controller) {
        super("exit", "close the application without saving");
        this.controller = controller;
    }

    @Override
    public void execute(CommandParameters commandParameters) {
        controller.close();
    }
}
