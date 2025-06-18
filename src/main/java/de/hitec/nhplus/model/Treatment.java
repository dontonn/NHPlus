package de.hitec.nhplus.model;

import de.hitec.nhplus.utils.DateConverter;

import java.time.LocalDate;
import java.time.LocalTime;

public class Treatment {
    private long tid;
    private final long pid;
    private final long cid;
    private LocalDate date;
    private LocalTime begin;
    private LocalTime end;
    private String description;
    private String remarks;

    /**
     * Constructor to initiate an object of class <code>Treatment</code> with the given parameter. Use this constructor
     * to initiate objects, which are not persisted yet, because it will not have a treatment id (tid).
     *
     * @param pid Id of the treated patient.
     * @param cid Id of the careGiver
     * @param date Date of the Treatment.
     * @param begin Time of the start of the treatment in format "hh:MM"
     * @param end Time of the end of the treatment in format "hh:MM".
     * @param description Description of the treatment.
     * @param remarks Remarks to the treatment.
     */
    public Treatment(long pid, long cid, LocalDate date, LocalTime begin,
                     LocalTime end, String description, String remarks) {
        this.pid = pid;
        this.cid = cid;
        this.date = date;
        this.begin = begin;
        this.end = end;
        this.description = description;
        this.remarks = remarks;
    }

    /**
     * Constructor to initiate an object of class <code>Treatment</code> with the given parameter. Use this constructor
     * to initiate objects, which are already persisted and have a treatment id (tid).
     *
     * @param tid Id of the treatment.
     * @param pid Id of the treated patient.
     * @param date Date of the Treatment.
     * @param begin Time of the start of the treatment in format "hh:MM"
     * @param end Time of the end of the treatment in format "hh:MM".
     * @param description Description of the treatment.
     * @param remarks Remarks to the treatment.
     */
    public Treatment(long tid, long pid, long cid, LocalDate date, LocalTime begin,
                     LocalTime end, String description, String remarks) {
        this.tid = tid;
        this.pid = pid;
        this.cid = cid;
        this.date = date;
        this.begin = begin;
        this.end = end;
        this.description = description;
        this.remarks = remarks;
    }

    /**
     * Returns the treatment ID.
     *
     * @return the unique treatment identifier
     */
    public long getTid() {
        return tid;
    }

    /**
     * Returns the patient ID associated with this treatment.
     *
     * @return the patient identifier
     */
    public long getPid() {
        return this.pid;
    }

    /**
     * Returns the caregiver ID associated with this treatment.
     *
     * @return the caregiver identifier
     */
    public long getCid() {
        return this.cid;
    }

    /**
     * Returns the treatment date as a string in ISO-8601 format.
     *
     * @return the date of the treatment
     */
    public String getDate() {
        return date.toString();
    }

    /**
     * Returns the treatment start time as a string in ISO-8601 format.
     *
     * @return the start time of the treatment
     */
    public String getBegin() {
        return begin.toString();
    }

    /**
     * Returns the treatment end time as a string in ISO-8601 format.
     *
     * @return the end time of the treatment
     */
    public String getEnd() {
        return end.toString();
    }

    /**
     * Sets the treatment date by converting the given string to a LocalDate.
     *
     * @param date the date string to parse
     */
    public void setDate(String date) {
        this.date = DateConverter.convertStringToLocalDate(date);
    }

    /**
     * Sets the treatment start time by converting the given string to a LocalTime.
     *
     * @param begin the start time string to parse
     */
    public void setBegin(String begin) {
        this.begin = DateConverter.convertStringToLocalTime(begin);;
    }

    /**
     * Sets the treatment end time by converting the given string to a LocalTime.
     *
     * @param end the end time string to parse
     */
    public void setEnd(String end) {
        this.end = DateConverter.convertStringToLocalTime(end);;
    }

    /**
     * Returns the description of the treatment.
     *
     * @return the treatment description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the treatment.
     *
     * @param description the description text to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns any remarks associated with the treatment.
     *
     * @return the treatment remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets remarks for the treatment.
     *
     * @param remarks the remarks text to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Returns a formatted string representation of the treatment,
     * including IDs, date, times, description, and remarks.
     *
     * @return the string representation of this treatment
     */
    @Override
    public String toString() {
        return "\nBehandlung" + "\nTID: " + this.tid +
                "\nPID: " + this.pid +
                "\nDate: " + this.date +
                "\nBegin: " + this.begin +
                "\nEnd: " + this.end +
                "\nDescription: " + this.description +
                "\nRemarks: " + this.remarks + "\n";
    }
}
