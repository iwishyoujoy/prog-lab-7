package commands.musicband;

import application.ConsolePrinter;
import collection.CollectionItem;
import collection.CollectionManager;
import collection.data.musicband.MusicBand;
import commands.AbstractCommand;
import commands.CommandParameters;

import java.util.Comparator;
import java.util.List;

public class PrintFieldAscendingEstablishmentDateCommand extends AbstractCommand {
    private final CollectionManager<? extends MusicBand> collectionManager;
    public PrintFieldAscendingEstablishmentDateCommand(CollectionManager<? extends MusicBand> collectionManager) {
        super("print_field_ascending_establishment_date", "print sorted list of ascending establishment dates");
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(CommandParameters commandParameters) {
        List<? extends MusicBand> musicBandList = collectionManager.toList();
        musicBandList.sort(Comparator.comparing(MusicBand::getEstablishmentDate));
        if (!musicBandList.isEmpty()){
            for (MusicBand musicBand:
                 musicBandList) {
                ConsolePrinter.println(musicBand.getEstablishmentDate().toString());

            }
        }
        else{
            ConsolePrinter.println("No such elements!");
        }
    }
}
