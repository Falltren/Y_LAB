package org.fallt.repository.impl;

import lombok.AllArgsConstructor;
import org.fallt.exception.DBException;
import org.fallt.model.Training;
import org.fallt.model.TrainingType;
import org.fallt.repository.TrainingDao;
import org.fallt.util.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс предназначен для взаимодействия с таблицей тренировок по средствам SQL запросов
 */
@AllArgsConstructor
public class TrainingDaoImpl implements TrainingDao {

    private Connection connection;

    @Override
    public void save(Training training) {
        String sql = "INSERT INTO my_schema.trainings (training_type_id, date, duration, spent_calories, description, user_id)" +
                " VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, training.getType().getId());
            preparedStatement.setObject(2, training.getDate());
            preparedStatement.setInt(3, training.getDuration());
            preparedStatement.setInt(4, training.getSpentCalories());
            preparedStatement.setString(5, training.getDescription());
            preparedStatement.setLong(6, training.getUser().getId());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void update(Training training) {
        String sql = "UPDATE my_schema.trainings SET training_type_id = ?, date = ?, duration = ?, spent_calories = ?," +
                " description = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, training.getType().getId());
            preparedStatement.setObject(2, training.getDate());
            preparedStatement.setInt(3, training.getDuration());
            preparedStatement.setInt(4, training.getSpentCalories());
            preparedStatement.setString(5, training.getDescription());
            preparedStatement.setLong(6, training.getId());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM my_schema.trainings WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public Optional<Training> findTrainingById(Long id, String trainingType, LocalDate date) {
        String sql = "SELECT * FROM my_schema.trainings tr LEFT JOIN my_schema.training_type tt ON tr.training_type_id = tt.id" +
                " WHERE tr.user_id = ? AND tt.type = ? AND tr.date = ?";
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, trainingType);
            preparedStatement.setObject(3, date);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Training training = instantiateTraining(resultSet);
                return Optional.of(training);
            }
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        } finally {
            DBUtils.closeResultSet(resultSet);
        }
        return Optional.empty();
    }

    @Override
    public List<Training> findAllUserTrainings(Long userId) {
        List<Training> trainings = new ArrayList<>();
        String sql = "SELECT * FROM my_schema.trainings tr LEFT JOIN my_schema.training_type tt" +
                " ON tr.training_type_id = tt.id WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Training training = instantiateTraining(resultSet);
                trainings.add(training);
            }
            DBUtils.closeResultSet(resultSet);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
        return trainings;
    }

    @Override
    public List<Training> findUserTrainingsByDay(Long userId, LocalDate date) {
        List<Training> trainings = new ArrayList<>();
        String sql = "SELECT * FROM my_schema.trainings tr LEFT JOIN my_schema.training_type tt" +
                " ON tr.training_type_id = tt.id WHERE user_id = ? AND tr.date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setObject(2, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Training training = instantiateTraining(resultSet);
                trainings.add(training);
            }
            DBUtils.closeResultSet(resultSet);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
        return trainings;
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
        trainingType.setId(resultSet.getInt(8));
        trainingType.setType(resultSet.getString(9));
        return trainingType;
    }
}
