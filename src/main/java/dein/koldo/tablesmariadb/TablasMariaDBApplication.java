package dein.koldo.tablesmariadb;

import java.io.IOException;

import dein.koldo.tablesmariadb.util.LoggerConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Main class of the JavaFX application.
 *
 * <p>This class initializes logging, loads the main FXML view
 * and displays the primary application window.</p>
 *
 * @author Koldo
 */
public class TablasMariaDBApplication extends Application {

    /**
     * Initializes and displays the primary JavaFX window.
     *
     * @param stage primary application stage
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage)
            throws IOException {

        LoggerConfig.configure();

        FXMLLoader fxmlLoader =
                new FXMLLoader(
                        TablasMariaDBApplication.class.getResource(
                                "hello-view.fxml"
                        )
                );

        Scene scene =
                new Scene(
                        fxmlLoader.load()
                );

        stage.setTitle(
                "Tablas Con MariaDB"
        );

        stage.setMinWidth(
                Screen.getPrimary().getBounds().getWidth() / 2
        );

        stage.setMinHeight(
                Screen.getPrimary().getBounds().getHeight() / 2
        );

        stage.setScene(scene);

        stage.show();
    }

    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
