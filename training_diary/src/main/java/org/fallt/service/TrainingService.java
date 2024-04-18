package org.fallt.service;

import org.fallt.audit.Audit;
import org.fallt.audit.AuditWriter;
import org.fallt.model.Training;
import org.fallt.model.TrainingType;
import org.fallt.model.User;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Сервис для работы с тренировками
 */
public class TrainingService {

    private UserService userService;

    private Set<TrainingType> types = new HashSet<>();

    private AuditWriter auditWriter;

    public TrainingService(UserService userService) {
        this.userService = userService;
        auditWriter = new AuditWriter();
    }

    /**
     * Добавляет новый тип тренировки
     * @param type Новый тип тренировки
     */
    public void addNewTrainingType(TrainingType type) {
        types.add(type);
    }

    /**
     * Добавляет новую тренировку пользователя. Если тренировка данного вида уже добавлялась в указанную дату,
     * новая тренировка добавлена не будет
     * @param user Пользователь
     * @param type Тип тренировки
     * @param date Дата тренировки
     * @param duration Продолжительность тренировки в минутах
     * @param spentCalories Количество потраченных калорий
     * @param description Описание тренировки
     */
    public void addNewTraining(User user, String type, LocalDate date, int duration, int spentCalories, String description) {
        TrainingType trainingType = new TrainingType(type.toLowerCase());
        addNewTrainingType(trainingType);
        Training training = new Training(1L, trainingType, date, duration, spentCalories, description);
        User existedUser = userService.getUserByName(user.getName());
        if (checkSameTrainingFromDay(user, training, date)) {
            System.out.println(MessageFormat.format("Вы уже добавляли данный тип тренировок: {0} в указанную дату", trainingType));
            return;
        }
        existedUser.getTrainings().add(training);
        auditWriter.write(new Audit(user.getName(), "added new training"));
    }

    /**
     * Просмотр данных о тренировках пользователя
     * @param user Пользователь
     * @return Список тренировок
     */
    public List<Training> getTrainings(User user) {
        auditWriter.write(new Audit(user.getName(), "watch all trainings"));
        return user.getTrainings().stream()
                .sorted(Comparator.comparing(Training::getDate).thenComparing(t -> t.getType().getType()))
                .toList();
    }

    /**
     * Просмотр данных о тренировках пользователя за определенный день
     * @param user Пользователь
     * @param inputDate Дата в виде строки, проверка валидности передаваемой даты должны осуществляться до передачи в данный метод
     * @return Список тренировок
     */
    public List<Training> getTrainings(User user, String inputDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate trainingDate = LocalDate.parse(inputDate, dateTimeFormatter);
        List<Training> trainings = getTrainings(user);
        auditWriter.write(new Audit(user.getName(), "watch training for day " + inputDate));
        return trainings.stream()
                .filter(t -> t.getDate().isEqual(trainingDate))
                .toList();
    }

    /**
     * Редактирование тренировки пользователя
     * @param user Пользователь
     * @param trainingType Тип тренировки
     * @param date Дата тренировки
     * @param newValue Map с новыми значениями параметров тренировки
     */
    public void editTraining(User user, String trainingType, LocalDate date, Map<String, String> newValue) {
        Optional<Training> optionalTraining = userService.getUserByName(user.getName()).getTrainings().stream()
                .filter(t -> t.getDate().equals(date) && t.getType().getType().equals(trainingType))
                .findFirst();
        if (optionalTraining.isPresent()) {
            Training training = optionalTraining.get();
            changeTrainingValue(training, newValue);
        }
        auditWriter.write(new Audit(user.getName(), "user edit training"));
    }

    /**
     * Удаление тренировки
     * @param user Пользователь
     * @param trainingType Тип тренировки
     * @param date Дата тренировки
     */
    public void deleteTraining(User user, String trainingType, LocalDate date) {
        Optional<Training> training = userService.getUserByName(user.getName()).getTrainings().stream()
                .filter(t -> t.getDate().equals(date) && t.getType().getType().equals(trainingType))
                .findFirst();
        training.ifPresent(value -> user.getTrainings().remove(value));
        auditWriter.write(new Audit(user.getName(), "user delete training"));
    }

    /**
     * Вспомогательный метод, используется для проверки наличия тренировки идентичного типа за указанный день
     * @param user Пользователь
     * @param training Тренировка
     * @param date Дата тренировки
     * @return Наличие или отсутствие тренировки такого же типа у пользователя за указанный день
     */
    private boolean checkSameTrainingFromDay(User user, Training training, LocalDate date) {
        return user.getTrainings().stream()
                .filter(t -> t.getDate().equals(date))
                .anyMatch(t -> t.getType().equals(training.getType()));
    }

    /**
     * Вспомогательный метод сопоставления новых значений и полей экземпляра тренировки
     * @param training Тренировка
     * @param newValue Map с новыми значения полей экземпляра тренировки
     */
    private void changeTrainingValue(Training training, Map<String, String> newValue) {
        for (Map.Entry<String, String> entry : newValue.entrySet()) {
            if (entry.getKey().equals("1")) {
                training.setType(new TrainingType(entry.getValue()));
            } else if (entry.getKey().equals("2")) {
                training.setDate(LocalDate.parse(entry.getValue(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else if (entry.getKey().equals("3")) {
                training.setDuration(Integer.parseInt(entry.getValue()));
            } else if (entry.getKey().equals("4")) {
                training.setSpentCalories(Integer.parseInt(entry.getValue()));
            } else if (entry.getKey().equals("5")) {
                training.setDescription(entry.getValue());
            }
        }
    }
}
