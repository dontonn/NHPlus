package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.utils.DateConverter;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * This class provides data access operations for the Caregiver model.
 * It extends the DaoImp class and overrides its methods to provide specific implementations for the Caregiver model.
 */
public class CaregiverDao extends DaoImp<Caregiver>{

    /**
     * This constructor creates a new CaregiverDao with the given database connection.
     *
     * @param connection The database connection.
     */
    public CaregiverDao(Connection connection) {
        super(connection);
    }

    /**
     * This method returns a PreparedStatement for creating a new caregiver in the database.
     *
     * @param caregiver The caregiver to be created.
     * @return The PreparedStatement for creating the caregiver.
     */
    @Override
    protected PreparedStatement getCreateStatement(Caregiver caregiver) {
        // SQL statement for creating a new caregiver
        final String SQL = "INSERT INTO caregiver (username, firstname, surname, dateOfBirth, telephoneNumber, password_hash, isAdmin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, caregiver.getUsername());
            preparedStatement.setString(2, caregiver.getFirstName());
            preparedStatement.setString(3, caregiver.getSurname());
            preparedStatement.setString(4, caregiver.getDateOfBirth());
            preparedStatement.setString(5, caregiver.getTelephoneNumber());
            preparedStatement.setString(6, caregiver.getPassword_hash());
            preparedStatement.setBoolean(7, caregiver.isAdmin());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * This method returns a PreparedStatement for reading a caregiver from the database by their ID.
     *
     * @param key The ID of the caregiver.
     * @return The PreparedStatement for reading the caregiver.
     */
    @Override
    protected PreparedStatement getReadByIDStatement(long key) {
        PreparedStatement preparedStatement = null;
        try {
            // SQL statement for reading a caregiver by their ID
            final String SQL = "SELECT * FROM caregiver WHERE pid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, key);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return preparedStatement;
    }

    /**
     * This method returns a Caregiver object created from the data in the given ResultSet.
     *
     * @param set The ResultSet containing the caregiver data.
     * @return The Caregiver object.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    protected Caregiver getInstanceFromResultSet(ResultSet set) throws SQLException {
        return new Caregiver(
                set.getLong("pid"),
                set.getString("username"),
                set.getString("firstname"),
                set.getString("surname"),
                DateConverter.convertStringToLocalDate(set.getString("dateOfBirth")),
                set.getString("telephoneNumber"),
                set.getString("password_hash"),
                set.getBoolean("isAdmin")
        );
    }

    /**
     * This method returns a PreparedStatement for reading all caregivers from the database who are not locked.
     *
     * @return The PreparedStatement for reading all caregivers.
     */
    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement preparedStatement = null;
        try {
            // SQL statement for reading all caregivers who are not locked
            final String SQL = "SELECT * FROM caregiver WHERE locked is not true";
            preparedStatement = this.connection.prepareStatement(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * This method returns a list of Caregiver objects created from the data in the given ResultSet.
     *
     * @param set The ResultSet containing the caregiver data.
     * @return The list of Caregiver objects.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    protected ArrayList<Caregiver> getListFromResultSet(ResultSet set) throws SQLException {
        ArrayList<Caregiver> caregivers = new ArrayList<>();
        while (set.next()) {
            caregivers.add(getInstanceFromResultSet(set));
        }
        return caregivers;
    }

    /**
     * This method returns a PreparedStatement for updating a caregiver in the database.
     *
     * @param caregiver The caregiver to be updated.
     * @return The PreparedStatement for updating the caregiver.
     */
    @Override
    protected PreparedStatement getUpdateStatement(Caregiver caregiver) {
        // SQL statement for updating a caregiver
        final String SQL = "UPDATE caregiver SET " +
                "username = ?, " +
                "firstname = ?, " +
                "surname = ?, " +
                "dateOfBirth = ?, " +
                "telephoneNumber = ?, " +
                "password_hash = ?, " +
                "isAdmin = ? " +
                "WHERE pid = ?";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, caregiver.getUsername());
            preparedStatement.setString(2, caregiver.getFirstName());
            preparedStatement.setString(3, caregiver.getSurname());
            preparedStatement.setString(4, caregiver.getDateOfBirth());
            preparedStatement.setString(5, caregiver.getTelephoneNumber());
            preparedStatement.setString(6, caregiver.getPassword_hash());
            preparedStatement.setBoolean(7, caregiver.isAdmin());
            preparedStatement.setLong(8, caregiver.getPid());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * This method returns a PreparedStatement for deleting a caregiver from the database by their ID.
     *
     * @param key The ID of the caregiver.
     * @return The PreparedStatement for deleting the caregiver.
     */
    @Override
    protected PreparedStatement getDeleteStatement(long key) {
        PreparedStatement preparedStatement = null;
        try {
            // SQL statement for deleting a caregiver by their ID
            final String SQL = "DELETE FROM caregiver WHERE pid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, key);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * This method locks a caregiver in the database by their ID.
     * The caregiver is locked for 10 years from the current date.
     *
     * @param pid The ID of the caregiver.
     * @throws SQLException If a database access error occurs.
     */
    public void lockCaregiver(long pid) throws SQLException {
        LocalDate localDate = LocalDate.now().plusYears(10);
        String sql = "UPDATE caregiver SET locked = 1, lockedDate = ? WHERE pid = ?";
        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(localDate));
            pstmt.setLong(2, pid);
            pstmt.executeUpdate();
        }
    }

    /**
     * This method deletes all expired locks on caregivers in the database.
     * A lock is considered expired if its lockedDate is before the current date.
     *
     * @throws SQLException If a database access error occurs.
     */
    public void deleteExpiredCaregiverLocks() throws SQLException {
        String sql = "DELETE FROM caregiver WHERE locked = 1 AND lockedDate < ?";
        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            pstmt.executeUpdate();
        }
    }

    /**
     * NEW: Checks if a username already exists in the database (for uniqueness validation)
     * @param username The username to check
     * @return true if username exists, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean usernameExists(String username) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            final String SQL = "SELECT COUNT(*) FROM caregiver WHERE username = ? AND locked is not true";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, username);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

            return false;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }
}