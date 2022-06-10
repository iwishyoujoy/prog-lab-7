package application;

public interface Controller {
    void run();
    void close();

    enum State {
        RUNNING,
        CLOSING
    }
}
