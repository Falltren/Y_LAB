package org.fallt.repository;

import org.fallt.model.TrainingType;

import java.util.Optional;

/**
 * Интерфейс для взаимодействия с таблицей, хранящей данные о типе тренировок
 */
public interface TrainingTypeDao {
    /**
     * Сохранение нового типа тренировки
     * @param trainingType Тип тренировки
     * @return Сохраненный объект TrainingType
     */
    TrainingType save(TrainingType trainingType);

    /**
     * Метод поиска типа тренировки по названию
     * @param type Название типа тренировки
     * @return Объект Optional с найденным по указанным параметрам типом тренировок.
     */
    Optional<TrainingType> findByType(String type);
}
