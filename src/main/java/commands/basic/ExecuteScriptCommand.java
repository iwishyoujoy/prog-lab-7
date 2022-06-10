package commands.basic;

import application.input.InputManager;
import application.input.ScriptInputStrategy;
import commands.AbstractCommand;
import commands.CommandParameters;

public class ExecuteScriptCommand extends AbstractCommand {
    private final InputManager inputManager;

    public ExecuteScriptCommand(InputManager inputManager) {
        super("execute_script", "execute script by typed path");
        this.inputManager = inputManager;
    }

    @Override
    public void execute(CommandParameters commandParameters) {
        inputManager.setInputStrategy(new ScriptInputStrategy(commandParameters.join()));
    }
}
