package commands.basic;

import application.ConsolePrinter;
import collection.CollectionManager;
import commands.AbstractCommand;
import commands.CommandParameters;

import java.util.Map;

public class InfoCommand extends AbstractCommand {
    private final CollectionManager<?> collectionManager;

    public InfoCommand(CollectionManager<?> collectionManager) {
        super("info", "print info about current collection");
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(CommandParameters commandParameters) {
        Map<String, String> infoTable = collectionManager.getInfo();
        for (String key: infoTable.keySet()){
            ConsolePrinter.println(key + " " + infoTable.get(key));
        }
    }
}
