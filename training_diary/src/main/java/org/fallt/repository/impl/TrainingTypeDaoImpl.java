package org.fallt.repository.impl;

import lombok.AllArgsConstructor;
import org.fallt.exception.DBException;
import org.fallt.model.TrainingType;
import org.fallt.repository.TrainingTypeDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class TrainingTypeDaoImpl implements TrainingTypeDao {

    private Connection connection;

    @Override
    public TrainingType save(TrainingType trainingType) {
        String sql = "INSERT INTO my_schema.training_type (id, type) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Integer id = getId();
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, trainingType.getType());
            preparedStatement.executeUpdate();
            connection.commit();
            trainingType.setId(id);
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

    private Integer getId() throws SQLException {
        String sql = "SELECT nextval('my_schema.training_type_id_seq')";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        throw new SQLException("Unable to retrieve value from sequence training_type_id_seq");
    }
}
