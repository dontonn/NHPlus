package de.hitec.nhplus.model;

import javafx.beans.property.SimpleStringProperty;
/**
 * Abstract class to use for Model to represents persons.
 * Declares and Implements needed methods.
 * */
public abstract class Person {
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty surname;
    /**
     * Constructor sets the name of the person.
     * */
    public Person(String firstName, String surname) {
        this.firstName = new SimpleStringProperty(firstName);
        this.surname = new SimpleStringProperty(surname);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getSurname() {
        return surname.get();
    }

    public SimpleStringProperty surnameProperty() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname.set(surname);
    }
}
