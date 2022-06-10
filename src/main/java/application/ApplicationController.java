package application;

import application.exceptions.CriticalErrorException;
import application.exceptions.ScriptEndedException;
import application.input.ConsoleInputStrategy;
import application.input.InputManager;
import application.input.InputStrategyType;
import collection.CollectionItem;
import collection.CollectionManager;
import collection.data.exceptions.InvalidDataException;
import collection.exceptions.CollectionException;
import commands.Command;
import commands.CommandManager;
import commands.CommandParameters;
import commands.basic.*;
import commands.exceptions.CommandException;
import commands.exceptions.OpenFileException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import server.AuthorizationManager;
import server.exceptions.InvalidLoginException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApplicationController<T extends CollectionItem> implements SavableController {
    private String savePath;

    private final CollectionManager<T> collectionManager;
    private final CommandManager commandManager;
    private final InputManager inputManager;

    public ApplicationController(CollectionManager<T> collectionManager) {
        this.collectionManager = collectionManager;
        commandManager.add(new HelpCommand(commandManager));
        commandManager.add(new InfoCommand(collectionManager));
        commandManager.add(new ShowCommand(collectionManager));
        commandManager.add(new ClearCommand(collectionManager));
        commandManager.add(new RemoveLastCommand(collectionManager));
        commandManager.add(new ExitCommand(this));
        commandManager.add(new SortCommand(collectionManager));
        commandManager.add(new RemoveByIdCommand(collectionManager));
        commandManager.add(new AddCommand<>(collectionManager, inputManager));
        commandManager.add(new UpdateCommand<>(collectionManager, inputManager));
        commandManager.add(new RemoveLowerCommand<>(collectionManager, inputManager));
        commandManager.add(new ExecuteScriptCommand(inputManager));
//        commandManager.add(new SaveCommand(this, collectionManager));
    }



    private AuthorizationManager authorizationManager;

    public void setAuthorizationManager(AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }

    {
        inputManager = new InputManager(new ConsoleInputStrategy());
        commandManager = new CommandManager();
    }
    public void addCommand(Command command){
        commandManager.add(command);
    }

    public State state;

    public void run(){
        state = State.RUNNING;
        while(state == State.RUNNING) {
            try {
                if (authorizationManager != null && !authorizationManager.isDone()) {
                    registerLogin(authorizationManager);
                }
                ConsolePrinter.request("Enter command: ");
                String[] input = inputManager.getInput().split(" ");
                String commandName = (input.length != 0) ? input[0] : "";
                List<String> param = new ArrayList<>(Arrays.asList(input));
                param.remove(0);
                commandManager.executeCommand(commandName, new CommandParameters(param));
            } catch (InvalidLoginException e) {
                ConsolePrinter.println(e.getMessage());
                if(authorizationManager!=null) {
                    registerLogin(authorizationManager);
                }
            } catch (CommandException | CollectionException | InvalidDataException | ScriptEndedException e){
                if(inputManager.type()== InputStrategyType.SCRIPT) {
                    inputManager.setInputStrategy(new ConsoleInputStrategy());
                }
                ConsolePrinter.println(e.getMessage());
            } catch (CriticalErrorException e){
                ConsolePrinter.println(e.getMessage());
                state = State.CLOSING;
            }
        }

    }

    @Override
    public void close() {
        state = State.CLOSING;
    }

    public void openFile(String path) {
        setSavePath(path);
        try {
            File inputFile;
            inputFile = new File(path);
            if(!inputFile.canRead()) throw new OpenFileException("xml", "Can't read");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            collectionManager.parse(doc.getDocumentElement());

        } catch (ParserConfigurationException | IOException | InvalidDataException | CollectionException | SAXException e) {
            setSavePath("default.xml");
            throw new OpenFileException("xml", e.getMessage());
        }
    }

    void registerLogin(AuthorizationManager manager) {
        boolean success = false;
        do {
            String choice = "login";
            do {
                ConsolePrinter.print("Do you want to register/login?: ");
                choice = inputManager.getInput();
            } while (!(choice.equals("register") || choice.equals("login")));

            String login;
            do {
                ConsolePrinter.print("Enter login (from 1 to 255 chars): ");
                login = inputManager.getInput();
            } while (!(login.length() >= 1 && login.length() <= 255));
            String password;
            do {
                ConsolePrinter.print("Enter password (from 2 chars): ");
                password = inputManager.getInput();
            } while (!(password.length() >= 2 && password.length() <= 255));
            if(choice.equals("login")) success = manager.login(login, password);
            if(choice.equals("register")) success = manager.register(login, password);
            if(!success) ConsolePrinter.println("Try again!");
        } while(!success);
        ConsolePrinter.println("Successfully done!");
    }


    @Override
    public String getSavePath() {
        return savePath;
    }

    @Override
    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }
}
