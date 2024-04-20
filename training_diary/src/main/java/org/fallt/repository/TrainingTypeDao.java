package org.fallt.repository;

import org.fallt.model.TrainingType;

import java.util.Optional;

public interface TrainingTypeDao {

    TrainingType save(TrainingType trainingType);

    Optional<TrainingType> findByType(String type);
}
