package commands.basic;

import application.ConsolePrinter;
import application.input.InputManager;
import collection.CollectionItem;
import collection.CollectionManager;
import collection.data.exceptions.InvalidDataException;
import commands.AbstractCommand;
import commands.CommandParameters;

import java.util.ArrayList;
import java.util.List;

public class RemoveLowerCommand<T extends CollectionItem> extends AbstractCommand {
    private final CollectionManager<T> collectionManager;
    private final InputManager inputManager;
    public RemoveLowerCommand(CollectionManager<T> collectionManager, InputManager inputManager) {
        super("remove_lower", "remove all elements that lower than typed element");
        this.collectionManager = collectionManager;
        this.inputManager = inputManager;
    }

    @Override
    public void execute(CommandParameters commandParameters) {
        T typedItem = collectionManager.generateNew();
        List<String> setList = new ArrayList<>(typedItem.getSettersList());
        for (int i = 0; i < setList.size(); ){
            try {
                String valueName = setList.get(i);
                ConsolePrinter.request(String.format("Enter %s: ", valueName));
                typedItem.setValue(valueName, inputManager.getInput());
                i++;
            } catch (InvalidDataException e){
                ConsolePrinter.println(e.getMessage());
            }
        }
        if (collectionManager.removeIf(iter -> typedItem.compareTo(iter)>0)){
            ConsolePrinter.println("All elements have been filtered!");
        }
        else{
            ConsolePrinter.println("No such elements!");
        }

    }
}
