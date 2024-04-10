package model;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TrainingService {

    private UserService userService;

    private Set<TrainingType> types = new HashSet<>();

    public TrainingService(UserService userService) {
        this.userService = userService;
    }

    public void addNewTrainingType(TrainingType type) {
        types.add(type);
    }

    public void addNewTraining(User user, TrainingType type, LocalDate date, int duration, int spentCalories, String description) {
        Training training = new Training(type, date, duration, spentCalories, description);
        User existedUser = userService.getUserByName(user.getName());
        existedUser.getTrainings().add(training);
    }

    public List<Training> watchTrainings(User user) {
        return user.getTrainings().stream()
                .sorted(Comparator.comparing(Training::getDate))
                .toList();
    }

    public void editTraining(User user, Training training) {

    }

    public void deleteTraining(User user, Training training) {
        user.getTrainings().remove(training);
    }


}
