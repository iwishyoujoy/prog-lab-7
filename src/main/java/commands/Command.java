package commands;

public interface Command {
    void execute(CommandParameters commandParameters);
    String getName();
    String getDescription();
}
