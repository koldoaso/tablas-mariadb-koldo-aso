package dein.koldo.tablesmariadb.util;

import java.io.IOException;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Utility class responsible for configuring the logging system
 * used by the application.
 *
 * <p>The logger writes the application activity to the
 * {@code application.log} file.</p>
 *
 * @author Koldo
 */
public final class LoggerConfig {
    private static boolean configured;
    /**
     * Logger used to report errors occurring during logger
     * configuration.
     */
    private static final Logger LOGGER =
            Logger.getLogger(LoggerConfig.class.getName());

    /**
     * Private constructor that prevents instances of this utility
     * class from being created.
     */
    private LoggerConfig() {
    }

    /**
     * Configures the application's file logger.
     *
     * <p>The log file is opened in append mode so previous
     * application activity is not deleted every time the program
     * starts.</p>
     */
    public static void configure() {

        if (configured) {
            return;
        }

        try {

            FileHandler fileHandler =
                    new FileHandler(
                            "application.log",
                            true
                    );

            fileHandler.setFormatter(
                    new SimpleFormatter()
            );

            Logger.getLogger("")
                    .addHandler(fileHandler);

            configured = true;

            LOGGER.info(
                    "Logger configurado correctamente."
            );

        } catch (IOException exception) {

            LOGGER.log(
                    Level.SEVERE,
                    "No se ha podido crear el archivo de log.",
                    exception
            );
        }
    }
}