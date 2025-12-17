package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.MainWindow;

public class CryptoApp extends Application {
    @Override
    public void start(Stage stage) {
        MainWindow root = new MainWindow();
        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("CryptoApp");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}