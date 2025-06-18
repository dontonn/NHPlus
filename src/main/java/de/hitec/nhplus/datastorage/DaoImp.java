package de.hitec.nhplus.datastorage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Abstract class to use for the DAO Classes.
 * Implements and declares the needed methods.
 * */
public abstract class DaoImp<T> implements Dao<T> {
    protected Connection connection;

    public DaoImp(Connection connection) {
        this.connection = connection;
    }
    /**
     * This Methode gets and executes the Create SQL Statement.
     * */
    @Override
    public void create(T t) throws SQLException {
        getCreateStatement(t).executeUpdate();
    }
    /**
     * This Methode gets and executes the Read SQL Statement and Returns its results.
     * Used to read a single records from a table.
     * @param key The ID of the Record to get.
     * */
    @Override
    public T read(long key) throws SQLException {
        T object = null;
        ResultSet result = getReadByIDStatement(key).executeQuery();
        if (result.next()) {
            object = getInstanceFromResultSet(result);
        }
        return object;
    }
    /**
     * This Methode gets and executes the read all SQL Statement and Returns its results.
     * Used to read all records from a table.
     * */
    @Override
    public List<T> readAll() throws SQLException {
        return getListFromResultSet(getReadAllStatement().executeQuery());
    }
    /**
    * This Methode gets and executes the Update SQL Statement.
     * Used to Update a single record in a table.
     * @param t The Objekt to Update.
     *  */
    @Override
    public void update(T t) throws SQLException {
        getUpdateStatement(t).executeUpdate();
    }
    /**
     * This Methode gets and executes the delete SQL Statement.
     * Used to delete a single record in a table.
     * @param key The ID of the Record to delete.
     * */
    @Override
    public void deleteById(long key) throws SQLException {
        getDeleteStatement(key).executeUpdate();
    }

    protected abstract T getInstanceFromResultSet(ResultSet set) throws SQLException;

    protected abstract ArrayList<T> getListFromResultSet(ResultSet set) throws SQLException;

    protected abstract PreparedStatement getCreateStatement(T t);

    protected abstract PreparedStatement getReadByIDStatement(long key);

    protected abstract PreparedStatement getReadAllStatement();

    protected abstract PreparedStatement getUpdateStatement(T t);

    protected abstract PreparedStatement getDeleteStatement(long key);
}
