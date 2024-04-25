package org.fallt.repository;

import org.fallt.model.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для взаимодействия с таблицей, хранящей данные о тренировках
 */
public interface TrainingDao {
    /**
     * Сохранение тренировки в базе данных
     * @param training Объект класса Training
     */
    void save(Training training);

    /**
     * Обновление данных существующей тренировки
     * @param training Объект класса Training с обновленными данными
     */
    void update(Training training);

    /**
     * Удаление тренировки из базы данных по первичному ключу
     * @param id Первичный ключ
     */
    void delete(Long id);

    /**
     * Метод поиска тренировки по id пользователя, типу тренировки и дате
     * @param id Идентификатор пользователя
     * @param trainingType Тип тренировки
     * @param date Дата тренировки
     * @return Объект Optional с тренировкой, соответствующей переданным параметрами
     */
    Optional<Training> findTrainingById(Long id, String trainingType, LocalDate date);

    /**
     * Метод поиска всех тренировок пользователя
     * @param userId Идентификатор пользователя
     * @return Список тренировок
     */
    List<Training> findAllUserTrainings(Long userId);

    /**
     * Метод поиска тренировок пользователя за определенную дату
     * @param userId Идентификатор пользователя
     * @param date Дата тренировки
     * @return Список тренировок
     */
    List<Training> findUserTrainingsByDay(Long userId, LocalDate date);
}
