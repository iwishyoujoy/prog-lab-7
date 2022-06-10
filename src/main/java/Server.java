import application.ApplicationController;
import collection.AbstractCollectionManager;
import collection.CollectionManager;
import collection.StackCollectionManager;
import collection.data.musicband.MusicBand;
import collection.data.musicband.Person;
import commands.basic.SaveCommand;
import commands.musicband.MinByFrontManCommand;
import commands.musicband.PrintFieldAscendingEstablishmentDateCommand;
import commands.musicband.RemoveAllByFrontManCommand;
import server.collection.UDPServerCollectionManager;

import java.time.LocalDateTime;


public class Server {
    public static void main(String... args) {
        AbstractCollectionManager<MusicBand> musicBandCollection = new StackCollectionManager<MusicBand>() {
            @Override
            public MusicBand generateNew() {
                return new MusicBand(generateId());
            }

            //это нужно, чтобы преобразовывать получаемые объекты в тип MusicBand
            @Override
            public Class<MusicBand> getItemClass() {
                return MusicBand.class;
            }
        };


        UDPServerCollectionManager<MusicBand> udpServerCollectionManager = new UDPServerCollectionManager<>(musicBandCollection, 8080);
        udpServerCollectionManager.db_name = "\"musicBands\"";

                ApplicationController<MusicBand> applicationController = new ApplicationController<MusicBand>(udpServerCollectionManager) {
            @Override
            public void close() {
                udpServerCollectionManager.close(); //при закрытии приложения дополнительно стопаем сервер
                super.close();
            }
        };
        applicationController.addCommand(new RemoveAllByFrontManCommand(musicBandCollection));
        applicationController.addCommand(new MinByFrontManCommand(musicBandCollection));
        applicationController.addCommand(new PrintFieldAscendingEstablishmentDateCommand(musicBandCollection));
//        applicationController.addCommand(new SaveCommand(applicationController, musicBandCollection));

//        applicationController.openFile((args.length!=0) ? String.join(" ", args).trim() : "default.xml");
        udpServerCollectionManager.run();
        applicationController.run();

    }
}
