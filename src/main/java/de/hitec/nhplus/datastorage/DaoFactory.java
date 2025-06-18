package de.hitec.nhplus.datastorage;

/**
 * Creates the DAO Objects to use.
 * */
public class DaoFactory {

    private static DaoFactory instance;

    private DaoFactory() {
    }

    /**
     * Implements the Singleton Pattern to get the DaoFactory Object.
     * @return Returns the DaoFactory instance
     * */
    public static DaoFactory getDaoFactory() {
        if (DaoFactory.instance == null) {
            DaoFactory.instance = new DaoFactory();
        }
        return DaoFactory.instance;
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

    /**
     * Creates a new UserDao and returns it.
     * @return Returns the UserDao instance
     * */
    public UserDao createUserDAO() {
        return new UserDao(ConnectionBuilder.getConnection());
    }
}