package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.User;

import java.sql.*;
import java.util.ArrayList;

/**
 * DAO for User operations
 */
public class UserDao extends DaoImp<User> {

    public UserDao(Connection connection) {
        super(connection);
    }

    @Override
    protected PreparedStatement getCreateStatement(User user) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "INSERT INTO users (username, password_hash, first_name, last_name, is_admin, is_active) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setString(3, user.getFirstName());
            preparedStatement.setString(4, user.getLastName());
            preparedStatement.setInt(5, user.isAdmin() ? 1 : 0);
            preparedStatement.setInt(6, user.isActive() ? 1 : 0);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getReadByIDStatement(long uid) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM users WHERE uid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, uid);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected User getInstanceFromResultSet(ResultSet result) throws SQLException {
        return new User(
                result.getLong("uid"),
                result.getString("username"),
                result.getString("password_hash"),
                result.getString("first_name"),
                result.getString("last_name"),
                result.getInt("is_admin") == 1,
                result.getInt("is_active") == 1,
                result.getString("created_at"),
                result.getString("last_login")
        );
    }

    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement statement = null;
        try {
            final String SQL = "SELECT * FROM users ORDER BY username";
            statement = this.connection.prepareStatement(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return statement;
    }

    @Override
    protected ArrayList<User> getListFromResultSet(ResultSet result) throws SQLException {
        ArrayList<User> list = new ArrayList<>();
        while (result.next()) {
            User user = new User(
                    result.getLong("uid"),
                    result.getString("username"),
                    result.getString("password_hash"),
                    result.getString("first_name"),
                    result.getString("last_name"),
                    result.getInt("is_admin") == 1,
                    result.getInt("is_active") == 1,
                    result.getString("created_at"),
                    result.getString("last_login")
            );
            list.add(user);
        }
        return list;
    }

    @Override
    protected PreparedStatement getUpdateStatement(User user) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "UPDATE users SET " +
                    "username = ?, " +
                    "password_hash = ?, " +
                    "first_name = ?, " +
                    "last_name = ?, " +
                    "is_admin = ?, " +
                    "is_active = ? " +
                    "WHERE uid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setString(3, user.getFirstName());
            preparedStatement.setString(4, user.getLastName());
            preparedStatement.setInt(5, user.isAdmin() ? 1 : 0);
            preparedStatement.setInt(6, user.isActive() ? 1 : 0);
            preparedStatement.setLong(7, user.getUid());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getDeleteStatement(long uid) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "DELETE FROM users WHERE uid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, uid);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Finds a user by username
     */
    public User findByUsername(String username) throws SQLException {
        PreparedStatement preparedStatement = null;
        User user = null;
        try {
            final String SQL = "SELECT * FROM users WHERE username = ? AND is_active = 1";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, username);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                user = getInstanceFromResultSet(result);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw exception;
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
        return user;
    }

    /**
     * Updates the last login timestamp for a user
     */
    public void updateLastLogin(long uid, String timestamp) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "UPDATE users SET last_login = ? WHERE uid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, timestamp);
            preparedStatement.setLong(2, uid);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw exception;
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    /**
     * Checks if a username already exists
     */
    public boolean usernameExists(String username) throws SQLException {
        PreparedStatement preparedStatement = null;
        boolean exists = false;
        try {
            final String SQL = "SELECT COUNT(*) FROM users WHERE username = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, username);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                exists = result.getInt(1) > 0;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw exception;
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
        return exists;
    }

    /**
     * Gets all active users
     */
    public ArrayList<User> readAllActive() throws SQLException {
        PreparedStatement statement = null;
        ArrayList<User> users = new ArrayList<>();
        try {
            final String SQL = "SELECT * FROM users WHERE is_active = 1 ORDER BY username";
            statement = this.connection.prepareStatement(SQL);
            ResultSet result = statement.executeQuery();
            users = getListFromResultSet(result);
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw exception;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return users;
    }
}