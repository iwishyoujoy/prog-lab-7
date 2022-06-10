package commands;

import commands.exceptions.InvalidParamException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandParameters {
    private final List<String> paramList;


    public CommandParameters(List<String> paramList) {
        this.paramList = paramList;
    }


    public String getParam(int index){
        if (paramList.size()<= index) throw new InvalidParamException();
        return paramList.get(index);
    }

    public String join(){
        String value = "";
        for (String iter:
             paramList) {
            value = value + iter + " ";
        }
        return value.trim();
    }
}
