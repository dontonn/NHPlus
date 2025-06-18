package de.hitec.nhplus.datastorage;

/**
 * Creates the DAO Objekts to use.
 * */

public class DaoFactory {

    private static DaoFactory instance;

    private DaoFactory() {
    }
    /**
     * Implements the Singelton Pattern to get the DaoFactory Objekt.
     * @return Returns the DaoFactory instance
     * */
    public static DaoFactory getDaoFactory() {
        if (DaoFactory.instance == null) {
            DaoFactory.instance = new DaoFactory();
        }
        return DaoFactory.instance;
    }
    public LoginUserDao createCaregiverDAO() {
        return new LoginUserDao(ConnectionBuilder.getConnection());
    }

    /**
     * Creates a new TreatmentDao and returns it.
     * @return Returns the TreatmentDao instance
     * */
    public TreatmentDao createTreatmentDao() {
        return new TreatmentDao(ConnectionBuilder.getConnection());
    }
    /**
     * Creates a new PatientDao and returns it.
     * @return Returns the PatientDao instance
     *  */
    public PatientDao createPatientDAO() {
        return new PatientDao(ConnectionBuilder.getConnection());
    }
    /**
     * Creates a new CareGiverDao and returns it.
     * @return Returns the CareGiver instance
     * */
    public CareGiverDao createCareGiverDAO() {
        return new CareGiverDao(ConnectionBuilder.getConnection());
    }

}
