package in;

import model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Menu {

    private UserBase userBase;
    private Scanner scanner;
    private Registration registration;
    private Authentication authentication;
    private UserService userService;
    private TrainingService trainingService;
    private boolean isStop;

    public Menu() {
        this.userBase = new UserBase();
        this.scanner = new Scanner(System.in);
        this.registration = new Registration(userBase);
        this.userService = new UserService(userBase);
        this.authentication = new Authentication(userService);
        this.trainingService = new TrainingService(userService);
        this.isStop = false;
    }

    public void start() {
        while (!isStop) {
            System.out.println(Message.MAIN_MENU);
            String selection = scanner.nextLine();
            switch (selection) {
                case "1" -> register();
                case "2" -> authentication();
                case "0" -> isStop = true;
                default -> System.out.println(Message.INCORRECT_MENU_NUMBER);
            }
        }
    }

    private void register() {
        registration.register();
    }

    private void authentication() {
        User registerUser = authentication.login();
        while (true) {
            if (registerUser != null) {
                System.out.println(Message.USER_MENU);
                String selection = scanner.nextLine();
                switch (selection) {
                    case "1" -> inputTrainingData(registerUser);
                    case "2" -> deleteTraining(registerUser);
                    case "3" -> printAllTrainings(registerUser.getTrainings());
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println(Message.INCORRECT_MENU_NUMBER);
                }
            }
        }
    }

    private void inputTrainingData(User user) {
        System.out.println("Введите тип тренировки");
        String type = scanner.nextLine();
        System.out.println("Введите дату тренировки в формате дд/мм/гггг");
        String date = scanner.nextLine();
        checkInputDate(date);
        LocalDate trainingDate = getDateFromString(date);
        System.out.println("Введите продолжительность тренировки в минутах");
        String duration = scanner.nextLine();
        System.out.println("Введите количество потраченных калорий");
        String spentCalories = scanner.nextLine();
        System.out.println("Введите дополнительную информацию о тренировке (при необходимости)");
        String description = scanner.nextLine();
        trainingService.addNewTraining(user, type, trainingDate, Integer.parseInt(duration),
                Integer.parseInt(spentCalories), description);
    }

    private void deleteTraining(User user) {
        System.out.println("Введите дату тренировки, которую необходимо удалить в формате дд/мм/гггг");
        String date = scanner.nextLine();
        if (!checkInputDate(date)) {
            System.out.println("Введена некорректная дата");
            return;
        }
        List<Training> trainings = trainingService.watchTrainings(user, date);
        if (trainings.isEmpty()) {
            System.out.println("За указанную дату тренировки отсутствуют");
            return;
        }
        printTrainingsFromDay(trainings);
        System.out.println("Введите тип тренировки, которую необходимо удалить");
        String trainingType = scanner.nextLine();
        trainingService.deleteTraining(user, trainingType, getDateFromString(date));
    }

    private boolean checkInputDate(String input) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            dateFormat.parse(input);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    private LocalDate getDateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(date, formatter);
    }

    private void printTrainingsFromDay(List<Training> trainings) {
        for (Training training : trainings) {
            System.out.println("Тренировка: " + "\n"
                    + "вид тренировки: " + training.getType() + "\n"
                    + "продолжительность: " + training.getDuration() + " мин" + "\n"
                    + "количество затраченных калорий " + training.getSpentCalories() + " кал" + "\n"
                    + "дополнительная информация: " + training.getDescription() + "\n");
        }
    }

    private void printAllTrainings(List<Training> trainings) {
        Map<LocalDate, List<Training>> report = TrainingReport.getFullReportByDate(trainings);
        for (Map.Entry<LocalDate, List<Training>> entry : report.entrySet()) {
            System.out.println(entry.getKey().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            printTrainingsFromDay(entry.getValue());
        }
    }

}
