package org.fallt.service;

import org.fallt.audit.Audit;
import org.fallt.audit.AuditWriter;
import org.fallt.model.Training;
import org.fallt.model.TrainingType;
import org.fallt.model.User;
import org.fallt.repository.TrainingDao;
import org.fallt.repository.TrainingTypeDao;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сервис для работы с тренировками
 */
public class TrainingService {

    private final TrainingDao trainingDao;

    private final TrainingTypeDao trainingTypeDao;

    private final AuditWriter auditWriter;

    public TrainingService(TrainingDao trainingDao, TrainingTypeDao trainingTypeDao) {
        this.trainingDao = trainingDao;
        this.trainingTypeDao = trainingTypeDao;
        auditWriter = new AuditWriter();
    }

    /**
     * Добавляет новый тип тренировки
     *
     * @param type Новый тип тренировки
     */
    public TrainingType addNewTrainingType(TrainingType type) {
        return trainingTypeDao.save(type);
    }

    /**
     * Добавляет новую тренировку пользователя. Если тренировка данного вида уже добавлялась в указанную дату,
     * новая тренировка добавлена не будет
     *
     * @param user          Пользователь
     * @param type          Тип тренировки
     * @param date          Дата тренировки
     * @param duration      Продолжительность тренировки в минутах
     * @param spentCalories Количество потраченных калорий
     * @param description   Описание тренировки
     */
    public void addNewTraining(User user, String type, LocalDate date, int duration, int spentCalories, String description) {
        TrainingType trainingType;
        Optional<TrainingType> typeOptional = trainingTypeDao.findByType(type);
        if (typeOptional.isEmpty()) {
            TrainingType newTrainingType = new TrainingType();
            newTrainingType.setType(type);
            trainingType = addNewTrainingType(newTrainingType);
        } else {
            trainingType = typeOptional.get();
        }
        Training training = createdTraining(trainingType, date, duration, spentCalories, description, user);
        if (checkSameTrainingFromDay(user, training, date)) {
            System.out.println(MessageFormat.format("Вы уже добавляли данный тип тренировок: {0} в указанную дату", trainingType));
            return;
        }
        trainingDao.save(training);
        auditWriter.write(new Audit(user.getName(), "added new training"));
    }

    /**
     * Просмотр данных о тренировках пользователя
     *
     * @param user Пользователь
     * @return Список тренировок
     */
    public List<Training> getTrainings(User user) {
        auditWriter.write(new Audit(user.getName(), "watch all trainings"));
        return trainingDao.findAllUserTrainings(user.getId());
    }

    /**
     * Просмотр данных о тренировках пользователя за определенный день
     *
     * @param user      Пользователь
     * @param inputDate Дата в виде строки, проверка валидности передаваемой даты должны осуществляться до передачи в данный метод
     * @return Список тренировок
     */
    public List<Training> getTrainings(User user, String inputDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate trainingDate = LocalDate.parse(inputDate, dateTimeFormatter);
        auditWriter.write(new Audit(user.getName(), "watch training for day " + inputDate));
        return trainingDao.findUserTrainingsByDay(user.getId(), trainingDate);
    }

    /**
     * Редактирование тренировки пользователя
     *
     * @param user         Пользователь
     * @param trainingType Тип тренировки
     * @param date         Дата тренировки
     * @param newValue     Map с новыми значениями параметров тренировки
     */
    public void editTraining(User user, String trainingType, LocalDate date, Map<String, String> newValue) {
        Optional<Training> optionalTraining = trainingDao.findTrainingById(user.getId(), trainingType, date);
        if (optionalTraining.isPresent()) {
            Training training = optionalTraining.get();
            changeTrainingValue(training, newValue);
            trainingDao.update(training);
            auditWriter.write(new Audit(user.getName(), "user edit training"));
        }
    }

    /**
     * Удаление тренировки
     *
     * @param user         Пользователь
     * @param trainingType Тип тренировки
     * @param date         Дата тренировки
     */
    public void deleteTraining(User user, String trainingType, LocalDate date) {
        Optional<Training> optionalTraining = trainingDao.findTrainingById(user.getId(), trainingType, date);
        if (optionalTraining.isPresent()) {
            trainingDao.delete(optionalTraining.get().getId());
            auditWriter.write(new Audit(user.getName(), "user delete training"));
        }
    }

    /**
     * Вспомогательный метод, используется для проверки наличия тренировки идентичного типа за указанный день
     *
     * @param user     Пользователь
     * @param training Тренировка
     * @param date     Дата тренировки
     * @return Наличие или отсутствие тренировки такого же типа у пользователя за указанный день
     */
    private boolean checkSameTrainingFromDay(User user, Training training, LocalDate date) {
        Optional<Training> optionalTraining = trainingDao.findTrainingById(user.getId(), training.getType().getType(), date);
        return optionalTraining.isPresent();
    }

    /**
     * Вспомогательный метод сопоставления новых значений и полей экземпляра тренировки
     *
     * @param training Тренировка
     * @param newValue Map с новыми значения полей экземпляра тренировки
     */
    private void changeTrainingValue(Training training, Map<String, String> newValue) {
        for (Map.Entry<String, String> entry : newValue.entrySet()) {
            switch (entry.getKey()) {
                case "1" -> {
                    TrainingType trainingType;
                    Optional<TrainingType> optionalType = trainingTypeDao.findByType(entry.getValue());
                    if (optionalType.isEmpty()) {
                        TrainingType newTrainingType = new TrainingType();
                        newTrainingType.setType(entry.getValue());
                        trainingType = addNewTrainingType(newTrainingType);
                    } else {
                        trainingType = optionalType.get();
                    }
                    training.setType(trainingType);
                }
                case "2" ->
                        training.setDate(LocalDate.parse(entry.getValue(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                case "3" -> training.setDuration(Integer.parseInt(entry.getValue()));
                case "4" -> training.setSpentCalories(Integer.parseInt(entry.getValue()));
                case "5" -> training.setDescription(entry.getValue());
                default -> System.out.println("Некорректный параметр");
            }
        }
    }

    private Training createdTraining(TrainingType type, LocalDate date, int duration, int spentCalories, String description, User user) {
        Training training = new Training();
        training.setType(type);
        training.setDate(date);
        training.setDuration(duration);
        training.setSpentCalories(spentCalories);
        training.setDescription(description);
        training.setUser(user);
        return training;
    }
}
