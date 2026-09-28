package dein.koldo.tablesmariadb.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DataBaseConnection {

    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private DataBaseConnection() {
    }

    /**
     * Function that allows to create a new connection to the database through the use of
     * a JDBC connector.
     * 
     * @return A Connection to the database.
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {

        if (URL == null || USER == null || PASSWORD == null) {
            throw new SQLException(
                    "Las variables de entorno para acceder a la base de datos no estan configuradas."
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}