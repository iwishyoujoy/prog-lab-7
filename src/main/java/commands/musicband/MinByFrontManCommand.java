package commands.musicband;

import application.ConsolePrinter;
import collection.CollectionItem;
import collection.CollectionManager;
import collection.data.musicband.MusicBand;
import commands.AbstractCommand;
import commands.CommandParameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MinByFrontManCommand extends AbstractCommand {
    private final CollectionManager<? extends MusicBand> collectionManager;
    public MinByFrontManCommand(CollectionManager<? extends MusicBand> collectionManager) {
        super("min_by_front_man", "print any element from the collection with the minimal front man's name");
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(CommandParameters commandParameters) {
        List<? extends MusicBand> musicBandList = collectionManager.toList();
        musicBandList.sort(Comparator.comparing(iter -> iter.getFrontMan().getName()));
        if (!musicBandList.isEmpty()){
            MusicBand musicBand = musicBandList.get(0);
            for (String str :
                    musicBand.getGettersList()) {
                ConsolePrinter.println(str + " - " + musicBand.getValue(str));
            }
            ConsolePrinter.println("");
        } else {
            ConsolePrinter.println("Collection is empty!");
        }
    }
}
