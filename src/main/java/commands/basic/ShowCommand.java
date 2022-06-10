package commands.basic;

import application.ConsolePrinter;
import collection.CollectionItem;
import collection.CollectionManager;
import collection.exceptions.CollectionException;
import commands.AbstractCommand;
import commands.CommandParameters;

import java.util.List;

public class ShowCommand extends AbstractCommand {
    private final CollectionManager<? extends CollectionItem> collectionManager;

    public ShowCommand(CollectionManager<? extends CollectionItem> collectionManager) {
        super("show", "print all elements of current collection");
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(CommandParameters commandParameters) {
        if(!collectionManager.isEmpty()) {
            List<? extends CollectionItem> collectionItemList = collectionManager.toList();
            for (CollectionItem iter :
                    collectionItemList) {
                for (String str :
                        iter.getGettersList()) {
                    ConsolePrinter.println(str + " - " + iter.getValue(str));
                }
                ConsolePrinter.println("");
            }
        } else {
            ConsolePrinter.println("Collection is empty!");
        }
    }
}
