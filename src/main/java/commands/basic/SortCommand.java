package commands.basic;

import application.ConsolePrinter;
import collection.CollectionManager;
import commands.AbstractCommand;
import commands.CommandParameters;

import java.util.ConcurrentModificationException;

public class SortCommand extends AbstractCommand {
    private final CollectionManager<?> collectionManager;
    public SortCommand(CollectionManager<?> collectionManager) {
        super("sort", "sort elements of collection in natural order");
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(CommandParameters commandParameters) {
        if (!collectionManager.isEmpty()) {
            collectionManager.sort();
            ConsolePrinter.println("Collection has been sorted!");
        } else {
            ConsolePrinter.println("Collection is empty!");
        }
    }
}
