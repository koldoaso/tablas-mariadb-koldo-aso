package dein.koldo.tablesmariadb.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a person stored in the application and in the database.
 *
 * <p>Each person contains a unique identifier, first name, last name
 * and birthdate.</p>
 *
 * <p>The object is immutable after creation.</p>
 *
 * @author Koldo
 */
public class PersonaModel {

    /**
     * Unique identifier of the person.
     */
    private final int id;

    /**
     * First name of the person.
     */
    private final String firstName;

    /**
     * Last name of the person.
     */
    private final String lastName;

    /**
     * Birthdate of the person.
     */
    private final LocalDate birthDate;

    /**
     * Creates a new person.
     *
     * @param id unique identifier of the person
     * @param firstName first name of the person
     * @param lastName last name of the person
     * @param birthDate birth date of the person
     *
     * @throws IllegalArgumentException if the identifier is lower than
     *                                  or equal to zero, or if the first
     *                                  or last name is empty
     * @throws NullPointerException if the birthdate is {@code null}
     */
    public PersonaModel(
            int id,
            String firstName,
            String lastName,
            LocalDate birthDate
    ) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Id must be greater than zero."
            );
        }

        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException(
                    "First name cannot be empty."
            );
        }

        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException(
                    "Last name cannot be empty."
            );
        }

        Objects.requireNonNull(
                birthDate,
                "Birth date cannot be null."
        );

        this.id = id;
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.birthDate = birthDate;
    }

    /**
     * Returns the unique identifier of the person.
     *
     * @return person identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the first name of the person.
     *
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name of the person.
     *
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the birth date of the person.
     *
     * @return birth date
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }
}