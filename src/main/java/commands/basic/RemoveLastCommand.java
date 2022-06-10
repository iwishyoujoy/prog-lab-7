package commands.basic;

import application.ConsolePrinter;
import collection.CollectionManager;
import commands.AbstractCommand;
import commands.CommandParameters;

public class RemoveLastCommand extends AbstractCommand {
    private final CollectionManager<?> collectionManager;

    public RemoveLastCommand(CollectionManager<?> collectionManager) {
        super("remove_last", "remove the last element from the collection");
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(CommandParameters commandParameters) {
        if(collectionManager.remove(collectionManager.size()-1))
             ConsolePrinter.println("The last element from the collection has been removed!");
        else ConsolePrinter.println("Couldn't remove the last element from the collection!");
    }
}
