package commands.basic;

import application.ConsolePrinter;
import collection.CollectionManager;
import commands.AbstractCommand;
import commands.CommandParameters;

public class ClearCommand extends AbstractCommand {
    private final CollectionManager<?> collectionManager;

    public ClearCommand(CollectionManager<?> collectionManager) {
        super("clear", "clear the collection");
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(CommandParameters commandParameters) {
        collectionManager.clear();
        ConsolePrinter.println("The collection is empty now!");
    }
}
