package application;

public interface SavableController extends Controller {
    void setSavePath(String savePath);
    String getSavePath();
}
