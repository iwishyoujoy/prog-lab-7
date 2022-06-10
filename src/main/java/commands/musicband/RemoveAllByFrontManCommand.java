package commands.musicband;

import application.ConsolePrinter;
import collection.CollectionItem;
import collection.CollectionManager;
import collection.data.musicband.MusicBand;
import commands.AbstractCommand;
import commands.CommandParameters;

public class RemoveAllByFrontManCommand extends AbstractCommand {
    private final CollectionManager<? extends MusicBand> collectionManager;
    public RemoveAllByFrontManCommand(CollectionManager<? extends MusicBand> collectionManager) {
        super("remove_all_by_front_man", "remove all elements with equal to typed front man's name");
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(CommandParameters commandParameters) {
        if (collectionManager.removeIf(name -> name.getFrontMan().getName().equals(commandParameters.join()))){
            ConsolePrinter.println("All elements have been successfully deleted!");
        }
        else{
            ConsolePrinter.println("No such elements!");
        }
    }
}
