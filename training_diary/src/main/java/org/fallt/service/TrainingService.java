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

public class TrainingService {

    private UserService userService;

    private Set<TrainingType> types = new HashSet<>();

    private AuditWriter auditWriter;

    public TrainingService(UserService userService) {
        this.userService = userService;
        auditWriter = new AuditWriter();
    }

    public void addNewTrainingType(TrainingType type) {
        types.add(type);
    }

    public void addNewTraining(User user, String type, LocalDate date, int duration, int spentCalories, String description) {
        TrainingType trainingType = new TrainingType(type.toLowerCase());
        addNewTrainingType(trainingType);
        Training training = new Training(trainingType, date, duration, spentCalories, description);
        User existedUser = userService.getUserByName(user.getName());
        if (checkSameTrainingFromDay(user, training, date)) {
            System.out.println(MessageFormat.format("Вы уже добавляли данный тип тренировок: {0} в указанную дату", trainingType));
            return;
        }
        existedUser.getTrainings().add(training);
        auditWriter.write(new Audit(user.getName(), "added new training"));
    }

    public List<Training> watchTrainings(User user) {
        auditWriter.write(new Audit(user.getName(), "watch all trainings"));
        return user.getTrainings().stream()
                .sorted(Comparator.comparing(Training::getDate).thenComparing(t -> t.getType().getType()))
                .toList();
    }

    public List<Training> watchTrainings(User user, String inputDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate trainingDate = LocalDate.parse(inputDate, dateTimeFormatter);
        List<Training> trainings = watchTrainings(user);
        auditWriter.write(new Audit(user.getName(), "watch training for day " + inputDate));
        return trainings.stream()
                .filter(t -> t.getDate().isEqual(trainingDate))
                .toList();
    }

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

    public void deleteTraining(User user, String trainingType, LocalDate date) {
        Optional<Training> training = userService.getUserByName(user.getName()).getTrainings().stream()
                .filter(t -> t.getDate().equals(date) && t.getType().getType().equals(trainingType))
                .findFirst();
        training.ifPresent(value -> user.getTrainings().remove(value));
        auditWriter.write(new Audit(user.getName(), "user delete training"));
    }

    private boolean checkSameTrainingFromDay(User user, Training training, LocalDate date) {
        return user.getTrainings().stream()
                .filter(t -> t.getDate().equals(date))
                .anyMatch(t -> t.getType().equals(training.getType()));
    }

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
