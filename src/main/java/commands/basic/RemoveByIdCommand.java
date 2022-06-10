package commands.basic;

import application.ConsolePrinter;
import collection.CollectionItem;
import collection.CollectionManager;
import commands.AbstractCommand;
import commands.CommandParameters;
import commands.exceptions.InvalidParamException;

public class RemoveByIdCommand extends AbstractCommand {
    private final CollectionManager<? extends CollectionItem> collectionManager;
    public RemoveByIdCommand(CollectionManager<? extends CollectionItem> collectionManager) {
        super("remove_by_id", "remove an element from the collection by id");
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(CommandParameters commandParameters) {
        try {
            String id = commandParameters.getParam(0);
            if(collectionManager.removeById(Long.valueOf(id)))
                ConsolePrinter.println(String.format("The element with id \"%s\" has been removed!", id));
            else ConsolePrinter.println("Couldn't remove the element by id!");
        } catch (NumberFormatException e){
            throw new InvalidParamException();
        }
    }
}
