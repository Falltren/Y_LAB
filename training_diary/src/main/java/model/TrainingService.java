package model;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TrainingService {

    private UserService userService;

    private Set<TrainingType> types = new HashSet<>();

    public TrainingService(UserService userService) {
        this.userService = userService;
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
    }

    public List<Training> watchTrainings(User user) {
        return user.getTrainings().stream()
                .sorted(Comparator.comparing(Training::getDate).reversed().thenComparing(t -> t.getType().getType()))
                .toList();
    }

    public void editTraining(User user, Training training) {

    }

    public void deleteTraining(User user, String trainingType, LocalDate localDate) {
        Optional<Training> training = userService.getUserByName(user.getName()).getTrainings().stream()
                .filter(t -> t.getDate().equals(localDate) && t.getType().getType().equals(trainingType))
                .findFirst();
        training.ifPresent(value -> user.getTrainings().remove(value));
    }

    public List<Training> watchTrainings(User user, String inputDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate trainingDate = LocalDate.parse(inputDate, dateTimeFormatter);
        List<Training> trainings = watchTrainings(user);
        return trainings.stream()
                .filter(t -> t.getDate().isEqual(trainingDate))
                .toList();
    }

    private boolean checkSameTrainingFromDay(User user, Training training, LocalDate date) {
        return user.getTrainings().stream()
                .filter(t -> t.getDate().equals(date))
                .anyMatch(t -> t.getType().equals(training.getType()));

    }


}
