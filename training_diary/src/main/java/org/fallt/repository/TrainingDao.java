package org.fallt.repository;

import org.fallt.model.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingDao {

    void save(Training training);

    void update(Training training);

    void delete(Long id);

    Optional<Training> findTrainingById(Long id, String trainingType, LocalDate date);

    List<Training> findAllUserTrainings(Long userId);

    List<Training> findUserTrainingsByDay(Long userId, LocalDate date);
}
