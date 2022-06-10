package application;

import application.input.InputStrategyType;
import application.input.ScriptInputStrategy;

public class ConsolePrinter {
    public static InputStrategyType type;

    static public void print(String message){
        System.out.print(message);
    }
    static public void println(String message){
        System.out.println(message);
    }
    static public void request(String message) {
        if(type==InputStrategyType.CONSOLE) System.out.print(message);
    }
}
