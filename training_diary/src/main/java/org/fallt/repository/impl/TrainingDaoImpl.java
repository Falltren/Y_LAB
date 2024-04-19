package org.fallt.repository.impl;

import lombok.AllArgsConstructor;
import org.fallt.exception.DbException;
import org.fallt.model.Training;
import org.fallt.model.TrainingType;
import org.fallt.repository.TrainingDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class TrainingDaoImpl implements TrainingDao {

    private Connection connection;

    @Override
    public void save(Training training) {
        String sql = "INSERT INTO trainings (id, training_type_id, date, duration, spent_calories, description, user_id)" +
                " VALUES (? ? ? ? ? ? ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Long id = getId();
            preparedStatement.setLong(1, id);
            preparedStatement.setInt(2, training.getType().getId());
            preparedStatement.setObject(3, training.getDate());
            preparedStatement.setInt(4, training.getDuration());
            preparedStatement.setInt(5, training.getSpentCalories());
            preparedStatement.setString(6, training.getDescription());
            preparedStatement.setLong(7, training.getUser().getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void update(Training training) {
        String sql = "UPDATE trainings SET training_type_id = ?, date = ?, duration = ?, spent_calories = ?," +
                " description = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, training.getType().getId());
            preparedStatement.setObject(2, training.getDate());
            preparedStatement.setInt(3, training.getDuration());
            preparedStatement.setInt(4, training.getSpentCalories());
            preparedStatement.setString(5, training.getDescription());
            preparedStatement.setLong(6, training.getUser().getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM trainings WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public Optional<Training> findTrainingById(String trainingType, LocalDate date) {
        String sql = "SELECT * FROM trainings tr INNER JOIN training_type tt ON tr.training_type_id = tt.id" +
                " WHERE tt.type = ? AND tr.date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, trainingType);
            preparedStatement.setObject(2, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Training training = instantiateTraining(resultSet);
                return Optional.of(training);
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Training> findAllTrainings(Long userId) {
        List<Training> trainings = new ArrayList<>();
        String sql = "SELECT * FROM trainings tr INNER JOIN training_type tt ON tr.training_type_id = tt.id WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            Training training = instantiateTraining(resultSet);
            trainings.add(training);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return trainings;
    }

    private Long getId() throws SQLException {
        String sql = "SELECT nextval('trainings_id_seq')";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        }
        throw new SQLException("Unable to retrieve value from sequence users_id_seq");
    }

    private Training instantiateTraining(ResultSet resultSet) throws SQLException {
        Training training = new Training();
        training.setId(resultSet.getLong("id"));
        training.setType(instantiateTrainingType(resultSet));
        training.setDate(resultSet.getObject("date", LocalDate.class));
        training.setDuration(resultSet.getInt("duration"));
        training.setSpentCalories(resultSet.getInt("spent_calories"));
        training.setDescription(resultSet.getString("description"));
        return training;
    }

    private TrainingType instantiateTrainingType(ResultSet resultSet) throws SQLException {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(resultSet.getInt("tt.id"));
        trainingType.setType(resultSet.getString("tt.type"));
        return trainingType;
    }
}
