package commands.basic;

import application.ConsolePrinter;
import application.input.InputManager;
import collection.AbstractAdapter;
import collection.CollectionItem;
import collection.CollectionManager;
import collection.data.exceptions.InvalidDataException;
import commands.AbstractCommand;
import commands.CommandParameters;
import commands.exceptions.InvalidParamException;

import java.util.ArrayList;
import java.util.List;

public class UpdateCommand<T extends CollectionItem> extends AbstractCommand {
    private final CollectionManager<T> collectionManager;
    private final InputManager inputManager;
    public UpdateCommand(CollectionManager<T> collectionManager, InputManager inputManager) {
        super("update", "update info about an element");
        this.collectionManager = collectionManager;
        this.inputManager = inputManager;
    }

    @Override
    public void execute(CommandParameters commandParameters) {
        try {
            T updateItem = collectionManager.getById(Long.valueOf(commandParameters.getParam(0)));
            List<String> setList = new ArrayList<>(updateItem.getSettersList());
            for (int i = 0; i < setList.size(); ) {
                try {
                    String valueName = setList.get(i);
                    ConsolePrinter.request(String.format("Enter %s%s: ", valueName, updateItem.getFormat(valueName)));
                    String respond = inputManager.getInput();
                    if(respond.equals("-")) continue;
                    updateItem.setValue(valueName, respond);
                    i++;
                } catch (InvalidDataException e) {
                    ConsolePrinter.println(e.getMessage());
                }
            }
            ConsolePrinter.println("The element has been updated!");
        } catch (NumberFormatException e){
            throw new InvalidParamException();
        }
    }
}
