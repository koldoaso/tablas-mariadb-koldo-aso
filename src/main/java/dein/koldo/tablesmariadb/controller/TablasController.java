package dein.koldo.tablesmariadb.controller;

import dein.koldo.tablesmariadb.dao.PersonaDao;
import dein.koldo.tablesmariadb.model.PersonaModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 * Controller for the application's main table view.
 *
 * <p>This controller manages user interaction with the people table.
 * It allows people to be created, logically deleted and restored.</p>
 *
 * <p>Database access is delegated to {@link PersonaDao}, keeping the
 * controller separate from the persistence layer.</p>
 *
 * @author Koldo
 */
public class TablasController {

    /**
     * Logger used to record controller activity and errors.
     */
    private static final Logger LOGGER =
            Logger.getLogger(
                    TablasController.class.getName()
            );

    /**
     * DAO used to communicate with the person table in MariaDB.
     */
    private final PersonaDao personaDao =
            new PersonaDao();

    /**
     * Observable collection containing the people currently displayed
     * in the JavaFX table.
     */
    private final ObservableList<PersonaModel> personas =
            FXCollections.observableArrayList();

    /**
     * Button used to add a new person.
     */
    @FXML
    private Button add;

    /**
     * Button used to delete selected people.
     */
    @FXML
    private Button delete;

    /**
     * Button used to restore deleted people.
     */
    @FXML
    private Button restore;

    /**
     * Input containing the person's first name.
     */
    @FXML
    private TextField firstName;

    /**
     * Input containing the person's last name.
     */
    @FXML
    private TextField lastName;

    /**
     * Input containing the person's birth date.
     */
    @FXML
    private DatePicker birthDate;

    /**
     * Table containing all active people.
     */
    @FXML
    private TableView<PersonaModel> table;

    /**
     * Table column containing person identifiers.
     */
    @FXML
    private TableColumn<PersonaModel, Integer> tableUserId;

    /**
     * Table column containing first names.
     */
    @FXML
    private TableColumn<PersonaModel, String> tableFirstName;

    /**
     * Table column containing last names.
     */
    @FXML
    private TableColumn<PersonaModel, String> tableLastName;

    /**
     * Table column containing birth dates.
     */
    @FXML
    private TableColumn<PersonaModel, LocalDate> tableBirthDate;

    /**
     * Initializes the controller after the FXML elements have been
     * injected by JavaFX.
     *
     * <p>This method configures the table, tooltips, selection mode
     * and loads the people stored in MariaDB.</p>
     */
    @FXML
    public void initialize() {

        configureTooltips();

        configureTable();

        loadPersonas();

        LOGGER.info(
                "TablasController inicializado."
        );
    }

    /**
     * Configures the tooltips displayed by the application buttons.
     */
    private void configureTooltips() {

        add.setTooltip(
                new Tooltip(
                        "Adds a new person."
                )
        );

        delete.setTooltip(
                new Tooltip(
                        "Deletes the selected rows."
                )
        );

        restore.setTooltip(
                new Tooltip(
                        "Restores previously deleted rows."
                )
        );
    }

    /**
     * Configures the table columns and selection mode.
     */
    private void configureTable() {

        tableUserId.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(
                        data.getValue().getId()
                )
        );

        tableFirstName.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(
                        data.getValue().getFirstName()
                )
        );

        tableLastName.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(
                        data.getValue().getLastName()
                )
        );

        tableBirthDate.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(
                        data.getValue().getBirthDate()
                )
        );

        table.setItems(personas);

        table
                .getSelectionModel()
                .setSelectionMode(
                        SelectionMode.MULTIPLE
                );
    }

    /**
     * Retrieves all active people from MariaDB and displays them
     * inside the table.
     */
    private void loadPersonas() {

        try {

            personas.setAll(
                    personaDao.findAll()
            );

            LOGGER.info(
                    "Personas cargadas: "
                            + personas.size()
            );

        } catch (SQLException exception) {

            LOGGER.log(
                    Level.SEVERE,
                    "Error cargando personas.",
                    exception
            );

            showDatabaseError();
        }
    }

    /**
     * Creates a new person using the values entered in the form.
     *
     * <p>If any field is empty the operation is rejected and a
     * warning is displayed.</p>
     */
    @FXML
    private void addRow() {

        String name =
                firstName.getText().trim();

        String surname =
                lastName.getText().trim();

        LocalDate date =
                birthDate.getValue();

        if (
                name.isEmpty()
                        || surname.isEmpty()
                        || date == null
        ) {

            showWarning(
                    "Todos los campos deben estar rellenados."
            );

            LOGGER.warning(
                    "Intento de crear una persona con datos incompletos."
            );

            return;
        }

        try {

            PersonaModel persona =
                    personaDao.insert(
                            name,
                            surname,
                            date
                    );

            personas.add(persona);

            LOGGER.info(
                    "Persona creada. ID: "
                            + persona.getId()
            );

            clearForm();

        } catch (SQLException exception) {

            LOGGER.log(
                    Level.SEVERE,
                    "Error creando persona.",
                    exception
            );

            showDatabaseError();
        }
    }

    /**
     * Marks every selected person as deleted in the database.
     */
    @FXML
    private void deleteRows() {

        List<PersonaModel> selectedRows =
                new ArrayList<>(
                        table
                                .getSelectionModel()
                                .getSelectedItems()
                );

        if (selectedRows.isEmpty()) {

            LOGGER.warning(
                    "Se ha intentado eliminar sin seleccionar filas."
            );

            return;
        }

        try {

            personaDao.delete(
                    selectedRows
            );

            personas.removeAll(
                    selectedRows
            );

            LOGGER.info(
                    "Personas eliminadas: "
                            + selectedRows.size()
            );

        } catch (SQLException exception) {

            LOGGER.log(
                    Level.SEVERE,
                    "Error eliminando personas.",
                    exception
            );

            showDatabaseError();
        }
    }

    /**
     * Restores every logically deleted person from the database.
     */
    @FXML
    private void restoreRows() {

        try {

            personaDao.restoreAll();

            loadPersonas();

            LOGGER.info(
                    "Personas eliminadas restauradas."
            );

        } catch (SQLException exception) {

            LOGGER.log(
                    Level.SEVERE,
                    "Error restaurando personas.",
                    exception
            );

            showDatabaseError();
        }
    }

    /**
     * Clears every input field in the person creation form.
     */
    private void clearForm() {

        firstName.clear();

        lastName.clear();

        birthDate.setValue(null);
    }

    /**
     * Displays a warning dialog containing the specified message.
     *
     * @param message warning message shown to the user
     */
    private void showWarning(String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alert.setTitle(
                "Datos incorrectos"
        );

        alert.setHeaderText(null);

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }

    /**
     * Displays a generic database error without exposing internal
     * database information to the user.
     */
    private void showDatabaseError() {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                "Error de base de datos"
        );

        alert.setHeaderText(
                "No se ha podido acceder a MariaDB."
        );

        alert.setContentText(
                "Comprueba la conexión con la base de datos."
        );

        alert.showAndWait();
    }
}