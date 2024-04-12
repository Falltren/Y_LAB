package org.fallt.in;

import org.fallt.model.Role;
import org.fallt.model.Training;
import org.fallt.model.User;
import org.fallt.repository.UserBase;
import org.fallt.security.Authentication;
import org.fallt.security.Registration;
import org.fallt.service.TrainingService;
import org.fallt.service.UserService;
import org.fallt.util.Message;
import org.fallt.util.Report;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UserDisplay {

    private UserBase userBase;
    private Scanner scanner;
    private Registration registration;
    private Authentication authentication;
    private UserService userService;
    private TrainingService trainingService;

    private boolean isStop;

    private static final String DATE_PATTERN = "dd/MM/yyyy";

    public UserDisplay() {
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
        if (registerUser == null) {
            return;
        }
        if (registerUser.getRole().equals(Role.ADMIN)) {
            getAdminMenu();
        } else {
            getUserMenu(registerUser);
        }
    }

    private void getUserMenu(User user) {
        while (true) {
            System.out.println(Message.USER_MENU);
            String selection = scanner.nextLine();
            switch (selection) {
                case "1" -> inputTrainingData(user);
                case "2" -> deleteTraining(user);
                case "3" -> editTraining(user);
                case "4" -> printAllTrainings(user.getTrainings());
                case "0" -> {
                    return;
                }
                default -> System.out.println(Message.INCORRECT_MENU_NUMBER);
            }
        }
    }

    private void getAdminMenu() {
        while (true) {
            System.out.println(Message.ADMIN_MENU);
            String selection = scanner.nextLine();
            switch (selection) {
                case "1" -> printAllUserInfo();
                case "0" -> {
                    return;
                }
                default -> System.out.println(Message.INCORRECT_MENU_NUMBER);
            }
        }
    }

    private void editTraining(User registerUser) {
        System.out.println("Введите дату тренировки, которую необходимо отредактировать в формате дд/мм/гггг");
        String date = scanner.nextLine();
        if (!checkInputDate(date)) {
            System.out.println("Введена некорректная дата");
            return;
        }
        List<Training> trainings = trainingService.watchTrainings(registerUser, date);
        if (trainings.isEmpty()) {
            System.out.println("За указанную дату тренировки отсутствуют");
            return;
        }
        printTrainings(trainings);
        System.out.println("Введите тип тренировки, которую необходимо отредактировать");
        String trainingType = scanner.nextLine();
        Map<String, String> editableData = chooseEditFieldInTraining();
        trainingService.editTraining(registerUser, trainingType, getDateFromString(date), editableData);
    }

    private void inputTrainingData(User user) {
        System.out.println("Введите тип тренировки");
        String type = scanner.nextLine();
        System.out.println("Введите дату тренировки в формате дд/мм/гггг");
        String date = scanner.nextLine();
        if (!checkInputDate(date)) {
            System.out.println("Введена некорректная дата");
            return;
        }
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

    private void deleteTraining(User registerUser) {
        System.out.println("Введите дату тренировки, которую необходимо удалить в формате дд/мм/гггг");
        String date = scanner.nextLine();
        if (!checkInputDate(date)) {
            System.out.println("Введена некорректная дата");
            return;
        }
        List<Training> trainings = trainingService.watchTrainings(registerUser, date);
        if (trainings.isEmpty()) {
            System.out.println("За указанную дату тренировки отсутствуют");
            return;
        }
        printTrainings(trainings);
        System.out.println("Введите тип тренировки, которую необходимо удалить");
        String trainingType = scanner.nextLine();
        trainingService.deleteTraining(registerUser, trainingType, getDateFromString(date));
    }

    private boolean checkInputDate(String input) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN);
        try {
            dateFormat.parse(input);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    private LocalDate getDateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        return LocalDate.parse(date, formatter);
    }

    private void printTrainings(List<Training> trainings) {
        for (Training training : trainings) {
            System.out.println("Тренировка: " + "\n"
                    + "тип тренировки: " + training.getType() + "\n"
                    + "продолжительность: " + training.getDuration() + " мин" + "\n"
                    + "количество затраченных калорий " + training.getSpentCalories() + " кал" + "\n"
                    + "дополнительная информация: " + training.getDescription() + "\n");
        }
    }

    private void printAllTrainings(List<Training> trainings) {
        Map<LocalDate, List<Training>> report = Report.getUserReport(trainings);
        for (Map.Entry<LocalDate, List<Training>> entry : report.entrySet()) {
            System.out.println(entry.getKey().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
            printTrainings(entry.getValue());
        }
    }

    private void printAllUserInfo() {
        List<User> users = userService.getAllUsers().stream()
                .filter(u -> !u.getRole().equals(Role.ADMIN))
                .toList();
        var report = Report.getFullReport(users);
        for (Map.Entry<String, Map<LocalDate, List<Training>>> entry : report.entrySet()) {
            System.out.println(entry.getKey());
            for (Map.Entry<LocalDate, List<Training>> userInfo : entry.getValue().entrySet()) {
                System.out.println(userInfo.getKey().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
                printTrainings(userInfo.getValue());
            }
        }
    }

    private Map<String, String> chooseEditFieldInTraining() {
        Map<String, String> map = new HashMap<>();
        System.out.println(Message.EDIT_MENU);
        String input = scanner.nextLine();
        for (char c : input.toCharArray()) {
            System.out.println("Введите новое значение для параметра - " + c);
            String cInput = scanner.nextLine();
            if (String.valueOf(c).equals("2")) {
                checkInputDate(cInput);
            }
            map.put(String.valueOf(c), cInput);
        }
        return map;
    }

}
