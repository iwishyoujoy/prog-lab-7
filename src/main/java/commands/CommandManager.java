package commands;

import commands.exceptions.NoSuchCommandException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commandMap;

    {
        commandMap = new LinkedHashMap<>();
    }

    public void add(Command command) {
        commandMap.put(command.getName(), command);
    }

    public boolean commandExists(String commandName){
        return commandMap.containsKey(commandName);
    }

    public void executeCommand(String commandName, CommandParameters commandParameters){
        if (!commandExists(commandName)) throw new NoSuchCommandException(commandName);
        commandMap.get(commandName).execute(commandParameters);
    }

    public List<Command> getCommands(){
        return new ArrayList<>(commandMap.values());
    }

}
