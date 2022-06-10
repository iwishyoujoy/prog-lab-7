package commands.basic;


import application.ApplicationController;
import application.ConsolePrinter;
import application.SavableController;
import collection.CollectionManager;
import commands.AbstractCommand;
import commands.CommandParameters;
import commands.exceptions.CommandException;
import commands.exceptions.OpenFileException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class SaveCommand extends AbstractCommand {
    private final SavableController controller;
    private final CollectionManager<?> collectionManager;
    public SaveCommand(SavableController controller, CollectionManager<?> collectionManager) {
        super("save", "save collection to .xml file");
        this.controller = controller;
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(CommandParameters params)  {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            document.appendChild(collectionManager.parse(document));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);


            File file = new File(controller.getSavePath());
            if(!file.canWrite()) throw new OpenFileException(file.getAbsolutePath(), "Can't write");
            file.createNewFile();

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

            StringWriter result = new StringWriter();
            StreamResult streamResult = new StreamResult(result);

            transformer.transform(domSource, streamResult);

            bufferedWriter.write(result.toString());

            bufferedWriter.close();

        } catch (ParserConfigurationException | TransformerException | IOException e ) {
            throw new OpenFileException(controller.getSavePath(), e.getMessage());
        }
    }
}