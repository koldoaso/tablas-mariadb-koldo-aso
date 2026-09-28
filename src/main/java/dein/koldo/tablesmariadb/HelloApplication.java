package dein.koldo.tablesmariadb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Tablas Con MariaDB");
        stage.setMinWidth(Screen.getPrimary().getBounds().getWidth()/2);
        stage.setMinHeight(Screen.getPrimary().getBounds().getHeight()/2);
        stage.setScene(scene);
        stage.show();
    }
}
