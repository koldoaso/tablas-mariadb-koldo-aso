package dein.koldo.tablesmariadb;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class TablasMariaDBApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TablasMariaDBApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Tablas Con MariaDB");
        stage.setMinWidth(Screen.getPrimary().getBounds().getWidth()/2);
        stage.setMinHeight(Screen.getPrimary().getBounds().getHeight()/2);
        stage.setScene(scene);
        stage.show();
    }
}
