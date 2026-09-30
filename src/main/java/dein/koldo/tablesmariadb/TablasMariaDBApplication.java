package dein.koldo.tablesmariadb;

import dein.koldo.tablesmariadb.util.LoggerConfig;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Main class of the JavaFX application.
 *
 * <p>The application automatically detects the locale configured
 * in the operating system and loads the corresponding translations.</p>
 *
 * @author Koldo
 */
public class TablasMariaDBApplication extends Application {

    /**
     * Path used to locate the application's translation bundles.
     */
    private static final String BUNDLE_PATH = "i18n";

    /**
     * Initializes and displays the primary application window.
     *
     * @param stage primary JavaFX stage
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {

        LoggerConfig.configure();

        Locale locale = Locale.getDefault();

        ResourceBundle resources =
                ResourceBundle.getBundle(
                        BUNDLE_PATH,
                        locale
                );

        FXMLLoader fxmlLoader =
                new FXMLLoader(
                        TablasMariaDBApplication.class.getResource(
                                "tablas-view.fxml"
                        ),
                        resources
                );

        Scene scene =
                new Scene(
                        fxmlLoader.load()
                );

        stage.setTitle(
                resources.getString("app.title")
        );

        stage.setMinWidth(
                Screen.getPrimary()
                        .getBounds()
                        .getWidth() / 2
        );

        stage.setMinHeight(
                Screen.getPrimary()
                        .getBounds()
                        .getHeight() / 2
        );

        stage.setScene(scene);

        stage.show();
    }

    /**
     * Main entry point of the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}