import application.ApplicationController;
import collection.AbstractCollectionManager;
import collection.CollectionManager;
import collection.StackCollectionManager;
import collection.data.musicband.MusicBand;
import collection.data.musicband.Person;
import commands.musicband.MinByFrontManCommand;
import commands.musicband.PrintFieldAscendingEstablishmentDateCommand;
import commands.musicband.RemoveAllByFrontManCommand;
import server.collection.UDPServerCollectionManager;
import server.collection.UPDClientCollectionManager;

import java.time.LocalDateTime;


public class Client {
    public static void main(String... args) {


        UPDClientCollectionManager<MusicBand> updClientCollectionManager = new UPDClientCollectionManager<MusicBand>("localhost", 8080) {
            //это нужно, чтобы преобразовывать получаемые объекты в тип MusicBand
            @Override
            public Class<MusicBand> getItemClass() {
                return MusicBand.class;
            }
        };

        ApplicationController<MusicBand> applicationController = new ApplicationController<MusicBand>(updClientCollectionManager);

        applicationController.setAuthorizationManager(updClientCollectionManager);


        applicationController.addCommand(new RemoveAllByFrontManCommand(updClientCollectionManager));
        applicationController.addCommand(new MinByFrontManCommand(updClientCollectionManager));
        applicationController.addCommand(new PrintFieldAscendingEstablishmentDateCommand(updClientCollectionManager));



        applicationController.run();

    }
}
