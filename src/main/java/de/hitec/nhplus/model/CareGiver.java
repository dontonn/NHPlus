package de.hitec.nhplus.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * CareGivers treat Patiens in
 */
public class CareGiver extends Person {
    private SimpleLongProperty cid;
    private final SimpleStringProperty telephoneNumber;
    private final List<Treatment> allTreatments = new ArrayList<>();

    /**
     * Constructor to initiate an object of class <code>CareGiver</code> with the given parameter. Use this constructor
     * to initiate objects, which are not persisted yet, because it will not have a CareGiver id (cid).
     *
     * @param firstName   First name of the CareGiver.
     * @param surname     Last name of the CareGiver.
     * @param telephoneNumber  Telephone Number of the CareGiver.
     */
    public CareGiver(String firstName, String surname, String telephoneNumber) {
        super(firstName, surname);
        this.telephoneNumber = new SimpleStringProperty(telephoneNumber);
    }

    /**
     * Constructor to initiate an object of class <code>CareGiver</code> with the given parameter. Use this constructor
     * to initiate objects, which are already persisted and have a CareGiver id (cid).
     *
     * @param cid         CareGiver id.
     * @param firstName   First name of the CareGiver.
     * @param surname     Last name of the CareGiver.
     * @param telephoneNumber  Telephone Number of the CareGiver.
     */
    public CareGiver(long cid, String firstName, String surname, String telephoneNumber) {
        super(firstName, surname);
        this.cid = new SimpleLongProperty(cid);
        this.telephoneNumber = new SimpleStringProperty(telephoneNumber);
    }
    public long getCid() {
        return cid.get();
    }

    public SimpleLongProperty cidProperty() {
        return cid;
    }

    public String getTelephoneNumber() {
        return telephoneNumber.get();
    }

    public SimpleStringProperty telephoneNumberProperty() {
        return telephoneNumber;
    }


    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber.set(telephoneNumber);
    }

    /**
     * Adds a treatment to the list of treatments, if the list does not already contain the treatment.
     *
     * @param treatment Treatment to add.
     * @return False, if the treatment was already part of the list, else true.
     */
    public boolean add(Treatment treatment) {
        if (this.allTreatments.contains(treatment)) {
            return false;
        }
        this.allTreatments.add(treatment);
        return true;
    }

    public String toString() {
        return "CareGiver" + "\nMNID: " + this.cid +
                "\nFirstname: " + this.getFirstName() +
                "\nSurname: " + this.getSurname() +
                "\nTelephoneNumber: " + this.telephoneNumber +
                "\n";
    }
}
