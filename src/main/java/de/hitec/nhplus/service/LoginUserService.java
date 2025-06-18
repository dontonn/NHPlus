package de.hitec.nhplus.service;

import de.hitec.nhplus.datastorage.ConnectionBuilder;
import de.hitec.nhplus.model.LoginUser;
import de.hitec.nhplus.utils.DateConverter;
import de.hitec.nhplus.utils.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class provides services related to the Caregiver model.
 * It contains a method for authenticating a caregiver based on their username and password.
 */
public class LoginUserService {

    /**
     * This method authenticates a caregiver based on their username and password.
     * It first checks if the username and password are not null.
     * Then it establishes a connection to the database and prepares a SQL statement to select the caregiver with the given username who is not locked.
     * It executes the SQL statement and retrieves the result.
     * It retrieves the hashed password from the result and checks if it matches the given password.
     * If the result contains a next row and the password matches, it creates a new Caregiver object with the data from the result and returns it.
     * If the result does not contain a next row or the password does not match, it returns null.
     * If a SQL exception occurs, it prints the stack trace and returns null.
     *
     * @param username The username of the caregiver.
     * @param password The password of the caregiver.
     * @return The authenticated caregiver, or null if the authentication failed.
     */
    public LoginUser authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        LoginUser loginUser = null;
        try {
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM caregiver WHERE username = ? AND locked is not true");
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String password_hash = resultSet.getString("password_hash");
                boolean password_match = PasswordUtil.checkPassword(password, password_hash);

                if (password_match) {
                    loginUser = new LoginUser(
                            resultSet.getLong("pid"),
                            resultSet.getString("username"),
                            resultSet.getString("firstname"),
                            resultSet.getString("surname"),
                            DateConverter.convertStringToLocalDate(resultSet.getString("dateOfBirth")),
                            resultSet.getString("telephoneNumber"),
                            password_hash,
                            resultSet.getBoolean("isAdmin")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loginUser;
    }
}