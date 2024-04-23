package org.fallt.repository.impl;

import lombok.AllArgsConstructor;
import org.fallt.exception.DBException;
import org.fallt.model.Role;
import org.fallt.model.Training;
import org.fallt.model.TrainingType;
import org.fallt.model.User;
import org.fallt.repository.UserDao;
import org.fallt.util.DBUtils;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс предназначен для взаимодействия с таблицей пользователей базы данных по средствам SQL запросов
 */
@AllArgsConstructor
public class UserDaoImpl implements UserDao {

    private Connection connection;

    @Override
    public void create(User user) {
        String sql = "INSERT INTO my_schema.users (role, name, registration, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getRole().name());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setObject(3, user.getRegistration());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public Optional<User> getUserByName(String name) {
        String sql = "SELECT * FROM my_schema.users u LEFT JOIN my_schema.trainings tr ON tr.user_id = u.id" +
                " LEFT JOIN my_schema.training_type tt ON tr.training_type_id = tt.id WHERE name = ?";
        Set<Training> trainings = new HashSet<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            User user = null;
            while (resultSet.next()) {
                user = instantiateUser(resultSet);
                Training training = instantiateTraining(resultSet);
                training.setUser(user);
                trainings.add(training);
                user.setTrainings(trainings);
            }
            DBUtils.closeResultSet(resultSet);
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM my_schema.users";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                User user = instantiateUser(resultSet);
                users.add(user);
            }
            DBUtils.closeResultSet(resultSet);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
        return users;
    }

    private User instantiateUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setRole(Role.valueOf(resultSet.getString("role")));
        user.setName(resultSet.getString("name"));
        user.setRegistration(resultSet.getObject("registration", LocalDateTime.class));
        user.setPassword(resultSet.getString("password"));
        return user;
    }

    private Training instantiateTraining(ResultSet resultSet) throws SQLException {
        Training training = new Training();
        TrainingType trainingType = new TrainingType();
        trainingType.setId(resultSet.getInt(13));
        trainingType.setType(resultSet.getString(14));
        training.setId(resultSet.getLong(6));
        training.setType(trainingType);
        training.setDate(resultSet.getObject(8, LocalDate.class));
        training.setDuration(resultSet.getInt(9));
        training.setSpentCalories(resultSet.getInt(10));
        training.setDescription(resultSet.getString(11));
        return training;
    }
}
