package org.fallt.repository.impl;

import lombok.AllArgsConstructor;
import org.fallt.exception.DBException;
import org.fallt.model.TrainingType;
import org.fallt.repository.TrainingTypeDao;

import java.sql.*;
import java.util.Optional;

@AllArgsConstructor
public class TrainingTypeDaoImpl implements TrainingTypeDao {

    private Connection connection;

    @Override
    public TrainingType save(TrainingType trainingType) {
        String sql = "INSERT INTO my_schema.training_type (type) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, trainingType.getType());
            preparedStatement.executeUpdate();
            int id = getLastKey(connection);
            trainingType.setId(id);
            connection.commit();
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
        return trainingType;
    }

    @Override
    public Optional<TrainingType> findByType(String type) {
        String sql = "SELECT * FROM my_schema.training_type WHERE type = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, type);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(instantiateTrainingType(resultSet));
            }
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
        return Optional.empty();
    }

    private TrainingType instantiateTrainingType(ResultSet resultSet) throws SQLException {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(resultSet.getInt("id"));
        trainingType.setType(resultSet.getString("type"));
        return trainingType;
    }

    private Integer getLastKey(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT lastval()");
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        throw new DBException("Can`t get last key from sequence");
    }

}
