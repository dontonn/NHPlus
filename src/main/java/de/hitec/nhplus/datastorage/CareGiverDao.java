package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.CareGiver;

import java.sql.*;
import java.util.ArrayList;

/**
 * Implements the Interface <code>DaoImp</code>. Overrides methods to generate specific <code>PreparedStatements</code>,
 * to execute the specific SQL Statements.
 */
public class CareGiverDao extends DaoImp<CareGiver> {

    /**
     * The constructor initiates an object of <code>CareGiverDao</code> and passes the connection to its super class.
     *
     * @param connection Object of <code>Connection</code> to execute the SQL-statements.
     */
    public CareGiverDao(Connection connection) {
        super(connection);
    }


    /**
     * Generates a <code>PreparedStatement</code> to persist the given object of <code>careGiver</code>.
     *
     * @param careGiver Object of <code>careGiver</code> to persist.
     * @return <code>PreparedStatement</code> to insert the given careGiver.
     */
    @Override
    protected PreparedStatement getCreateStatement(CareGiver careGiver) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "INSERT INTO care_giver (firstname, surname, telephone_number) " +
                    "VALUES (?, ?, ?)";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, careGiver.getFirstName());
            preparedStatement.setString(2, careGiver.getSurname());
            preparedStatement.setString(3, careGiver.getTelephoneNumber());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Generates a <code>PreparedStatement</code> to query a careGiver by a given careGiver id (pid).
     *
     * @param pid careGiver id to query.
     * @return <code>PreparedStatement</code> to query the careGiver.
     */
    @Override
    protected PreparedStatement getReadByIDStatement(long pid) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM care_giver WHERE cid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, pid);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Maps a <code>ResultSet</code> of one careGiver to an object of <code>careGiver</code>.
     *
     * @param result ResultSet with a single row. Columns will be mapped to an object of class <code>careGiver</code>.
     * @return Object of class <code>careGiver</code> with the data from the resultSet.
     */
    @Override
    protected CareGiver getInstanceFromResultSet(ResultSet result) throws SQLException {
        return new CareGiver(
                result.getInt(1),
                result.getString(2),
                result.getString(3),
                result.getString(4));
    }

    /**
     * Generates a <code>PreparedStatement</code> to query all CareGiver.
     *
     * @return <code>PreparedStatement</code> to query all CareGiver.
     */
    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement statement = null;
        try {
            final String SQL = "SELECT * FROM care_giver";
            statement = this.connection.prepareStatement(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return statement;
    }

    /**
     * Maps a <code>ResultSet</code> of all CareGivers to an <code>ArrayList</code> of <code>careGiver</code> objects.
     *
     * @param result ResultSet with all rows. The Columns will be mapped to objects of class <code>careGiver</code>.
     * @return <code>ArrayList</code> with objects of class <code>careGiver</code> of all rows in the
     * <code>ResultSet</code>.
     */
    @Override
    protected ArrayList<CareGiver> getListFromResultSet(ResultSet result) throws SQLException {
        ArrayList<CareGiver> list = new ArrayList<>();
        while (result.next()) {
            CareGiver careGiver = new CareGiver(result.getInt(1), result.getString(2),
                    result.getString(3), result.getString(4));
            list.add(careGiver);
        }
        return list;
    }

    /**
     * Generates a <code>PreparedStatement</code> to update the given careGiver, identified
     * by the id of the careGiver (pid).
     *
     * @param careGiver careGiver object to update.
     * @return <code>PreparedStatement</code> to update the given careGiver.
     */
    @Override
    protected PreparedStatement getUpdateStatement(CareGiver careGiver) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL =
                    "UPDATE care_giver SET " +
                            "firstname = ?, " +
                            "surname = ?, " +
                            "telephone_number = ? " +
                            "WHERE cid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, careGiver.getFirstName());
            preparedStatement.setString(2, careGiver.getSurname());
            preparedStatement.setString(3, careGiver.getTelephoneNumber());
            preparedStatement.setLong(4, careGiver.getCid());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Generates a <code>PreparedStatement</code> to delete a careGiver with the given id.
     *
     * @param pid Id of the careGiver to delete.
     * @return <code>PreparedStatement</code> to delete careGiver with the given id.
     */
    @Override
    protected PreparedStatement getDeleteStatement(long pid) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "DELETE FROM care_giver WHERE cid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, pid);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }
}
