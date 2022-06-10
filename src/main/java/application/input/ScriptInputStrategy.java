package application.input;

import application.exceptions.ScriptEndedException;
import commands.exceptions.InvalidParamException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ScriptInputStrategy implements InputStrategy{

    private final File file;
    private Scanner scanner;


    public ScriptInputStrategy(String path) {
        file = new File(path);
        if(!file.canRead()) throw new InvalidParamException();
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new InvalidParamException();
        }
    }

    @Override
    public InputStrategyType type() {
        return InputStrategyType.SCRIPT;
    }

    @Override
    public String getInput() {
        if(scanner.hasNext()) {
            return scanner.nextLine().trim();
        } else {
            throw new ScriptEndedException();
        }
    }
}
