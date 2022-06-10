package application.input;

import application.ConsolePrinter;

public class InputManager {
    private InputStrategy inputStrategy;

    public InputManager(InputStrategy inputStrategy) {
        setInputStrategy(inputStrategy);
    }

    public void setInputStrategy(InputStrategy inputStrategy) {
        this.inputStrategy = inputStrategy;
        ConsolePrinter.type = type();
    }

    public InputStrategyType type() {
        return inputStrategy.type();
    };

    public String getInput(){
        return inputStrategy.getInput();
    }
}
