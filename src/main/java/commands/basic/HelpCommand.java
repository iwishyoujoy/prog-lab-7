package commands.basic;

import application.ConsolePrinter;
import commands.AbstractCommand;
import commands.Command;
import commands.CommandManager;
import commands.CommandParameters;

public class HelpCommand extends AbstractCommand {
    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        super("help", "print info about available commands");
        this.commandManager = commandManager;
    }

    @Override
    public void execute(CommandParameters commandParameters) {
        for (Command command: commandManager.getCommands()){
            ConsolePrinter.println(command.getName() + " - " + command.getDescription());
        }
    }


}
