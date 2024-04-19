package org.fallt.repository.impl;

import lombok.AllArgsConstructor;
import org.fallt.exception.DbException;
import org.fallt.model.Role;
import org.fallt.model.Training;
import org.fallt.model.TrainingType;
import org.fallt.model.User;
import org.fallt.repository.UserDao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
public class UserDaoImpl implements UserDao {

    private Connection connection;

    @Override
    public void create(User user) {
        String sql = "INSERT INTO users (id, role, name, registration, password) VALUES (? ? ? ? ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Long id = getId();
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, user.getRole().name());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setObject(4, user.getRegistration());
            preparedStatement.setString(5, user.getPassword());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public Optional<User> getUserByName(String name) {
        String sql = "SELECT *  FROM users u JOIN trainings tr ON tr.user_id = u.id" +
                " JOIN training_type tt ON tr.training_type = tt.id WHERE name = ?";
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
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                User user = instantiateUser(resultSet);
                users.add(user);
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return users;
    }

    private Long getId() throws SQLException {
        String sql = "SELECT nextval('users_id_seq')";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        }
        throw new SQLException("Unable to retrieve value from sequence users_id_seq");
    }

    private User instantiateUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setRole(Role.valueOf(resultSet.getString("role")));
        user.setName(resultSet.getString("name"));
        user.setRegistration(resultSet.getObject("date", LocalDateTime.class));
        user.setPassword(resultSet.getString("password"));
        return user;
    }

    private Training instantiateTraining(ResultSet resultSet) throws SQLException {
        Training training = new Training();
        TrainingType trainingType = new TrainingType();
        trainingType.setId(resultSet.getInt("tt.id"));
        trainingType.setType(resultSet.getString("tt.type"));
        training.setId(resultSet.getLong("tr.id"));
        training.setType(trainingType);
        training.setDate(resultSet.getObject("tr.date", LocalDate.class));
        training.setDuration(resultSet.getInt("tr.duration"));
        training.setSpentCalories(resultSet.getInt("tr.spent_calories"));
        training.setDescription(resultSet.getString("tr.description"));
        return training;
    }
}
