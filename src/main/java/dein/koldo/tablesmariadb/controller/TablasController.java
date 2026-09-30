package dein.koldo.tablesmariadb.controller;

import dein.koldo.tablesmariadb.dao.PersonaDao;
import dein.koldo.tablesmariadb.model.PersonaModel;

import java.net.URL;

import java.sql.SQLException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
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
 * <p>User-visible texts are obtained from a {@link ResourceBundle},
 * allowing the application to automatically adapt to the locale
 * configured in the operating system.</p>
 *
 * @author Koldo
 */
public class TablasController implements Initializable {

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
     * Translation resources currently loaded by JavaFX.
     */
    private ResourceBundle resources;

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
     * Initializes the controller after all FXML components have
     * been injected.
     *
     * <p>The {@link ResourceBundle} provided by the FXMLLoader contains
     * the translations corresponding to the current system locale.</p>
     *
     * @param location location of the FXML document
     * @param resources translation resources loaded for the current locale
     */
    @Override
    public void initialize(
            URL location,
            ResourceBundle resources
    ) {

        this.resources = resources;

        configureTooltips();

        configureTable();

        loadPersonas();

        LOGGER.info(
                "TablasController inicializado."
        );

        LOGGER.info(
                "Locale detectada: "
                        + Locale.getDefault()
        );
    }

    /**
     * Configures translated tooltips for the application buttons.
     */
    private void configureTooltips() {

        add.setTooltip(
                new Tooltip(
                        text("tooltip.add")
                )
        );

        delete.setTooltip(
                new Tooltip(
                        text("tooltip.delete")
                )
        );

        restore.setTooltip(
                new Tooltip(
                        text("tooltip.restore")
                )
        );
    }

    /**
     * Configures the table columns, localized date formatting
     * and selection mode.
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

        configureBirthDateFormat();

        table.setItems(
                personas
        );

        table
                .getSelectionModel()
                .setSelectionMode(
                        SelectionMode.MULTIPLE
                );
    }

    /**
     * Configures the birth date column to display dates according
     * to the current system locale.
     *
     * <p>For example, Spanish and English locales may display the
     * same date using different textual formats.</p>
     */
    private void configureBirthDateFormat() {

        DateTimeFormatter dateFormatter =
                DateTimeFormatter
                        .ofLocalizedDate(
                                FormatStyle.MEDIUM
                        )
                        .withLocale(
                                Locale.getDefault()
                        );

        tableBirthDate.setCellFactory(column ->
                new TableCell<>() {

                    /**
                     * Updates the text displayed by a birth date cell.
                     *
                     * @param date date associated with the current row
                     * @param empty indicates whether the cell is empty
                     */
                    @Override
                    protected void updateItem(
                            LocalDate date,
                            boolean empty
                    ) {

                        super.updateItem(
                                date,
                                empty
                        );

                        if (empty || date == null) {

                            setText(null);

                        } else {

                            setText(
                                    dateFormatter.format(
                                            date
                                    )
                            );
                        }
                    }
                }
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
     * localized warning is displayed.</p>
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
                    text(
                            "warning.requiredFields"
                    )
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

            personas.add(
                    persona
            );

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
     * Displays a localized warning dialog.
     *
     * @param message warning message shown to the user
     */
    private void showWarning(
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alert.setTitle(
                text(
                        "warning.invalidData.title"
                )
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }

    /**
     * Displays a localized generic database error without exposing
     * internal database information to the user.
     */
    private void showDatabaseError() {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                text(
                        "error.database.title"
                )
        );

        alert.setHeaderText(
                text(
                        "error.database.header"
                )
        );

        alert.setContentText(
                text(
                        "error.database.content"
                )
        );

        alert.showAndWait();
    }

    /**
     * Retrieves the translated text associated with the specified
     * resource bundle key.
     *
     * @param key translation key
     * @return translated text for the currently selected locale
     */
    private String text(
            String key
    ) {

        return resources.getString(
                key
        );
    }
}