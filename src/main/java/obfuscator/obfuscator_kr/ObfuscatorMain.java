package obfuscator.obfuscator_kr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ObfuscatorMain extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ObfuscatorMain.class.getResource("Obfuscator.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1300, 750);
        stage.setTitle("Obfuscator");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}