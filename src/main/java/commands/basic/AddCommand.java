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

public class AddCommand<T extends CollectionItem> extends AbstractCommand {
    private final CollectionManager<T> collectionManager;
    private final InputManager inputManager;
    public AddCommand(CollectionManager<T> collectionManager, InputManager inputManager) {
        super("add", "add new element to the collection");
        this.collectionManager = collectionManager;
        this.inputManager = inputManager;
    }

    @Override
    public void execute(CommandParameters commandParameters) {
        T newItem = collectionManager.generateNew();
        List<String> setList = new ArrayList<>(newItem.getSettersList());
        for (int i = 0; i < setList.size(); ){
            try {
                String valueName = setList.get(i);
                ConsolePrinter.request(String.format("Enter %s%s: ", valueName, newItem.getFormat(valueName)));
                newItem.setValue(valueName, inputManager.getInput());
                i++;
            } catch (InvalidDataException e){
                ConsolePrinter.println(e.getMessage());
            }
        }
        collectionManager.add(newItem);
        ConsolePrinter.println("The element has been added!");
    }
}
