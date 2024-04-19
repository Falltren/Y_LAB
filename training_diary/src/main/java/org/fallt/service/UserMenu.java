package org.fallt.service;

import lombok.RequiredArgsConstructor;
import org.fallt.in.UserInput;
import org.fallt.model.Role;
import org.fallt.model.Training;
import org.fallt.model.User;
import org.fallt.out.ReportPrinter;
import org.fallt.security.Authentication;
import org.fallt.security.Registration;
import org.fallt.util.DateHandler;
import org.fallt.util.Message;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class UserMenu {

    private boolean isStop;

    private final UserInput userInput;

    private final UserService userService;

    private final Registration registration;

    private final Authentication authentication;

    private final TrainingService trainingService;

    private final ReportPrinter reportPrinter;

    public void start() {
        while (!isStop) {
            System.out.println(Message.MAIN_MENU);
            String selection = userInput.getUserInput();
            switch (selection) {
                case "1" -> registerMenu();
                case "2" -> authenticationMenu();
                case "0" -> isStop = true;
                default -> System.out.println(Message.INCORRECT_MENU_NUMBER);
            }
        }
    }

    private void registerMenu() {
        String name = userInput.inputName();
        if (userService.getUserByName(name) != null) {
            System.out.println(Message.USER_EXIST);
            return;
        }
        String password = userInput.inputPassword();
        String confirmPassword = userInput.getUserInput("Повторите ввод пароля");
        registration.register(name, password, confirmPassword);
    }

    private void authenticationMenu() {
        String name = userInput.inputName();
        String password = userInput.inputPassword();
        User registerUser = authentication.login(name, password);
        if (registerUser == null) {
            return;
        }
        if (registerUser.getRole().equals(Role.ROLE_ADMIN)) {
            getAdminMenu();
        } else {
            getUserMenu(registerUser);
        }
    }

    private void getAdminMenu() {
        while (true) {
            System.out.println(Message.ADMIN_MENU);
            String selection = userInput.getUserInput();
            switch (selection) {
                case "1" -> reportPrinter.printAllUserInfo();
                case "0" -> {
                    return;
                }
                default -> System.out.println(Message.INCORRECT_MENU_NUMBER);
            }
        }
    }

    private void getUserMenu(User user) {
        while (true) {
            System.out.println(Message.USER_MENU);
            String selection = userInput.getUserInput();
            switch (selection) {
                case "1" -> inputTrainingMenu(user);
                case "2" -> deleteTrainingMenu(user);
                case "3" -> editTrainingMenu(user);
                case "4" -> reportPrinter.printAllTrainings(user.getTrainings());
                case "5" -> getCaloriesReportMenu(user);
                case "0" -> {
                    return;
                }
                default -> System.out.println(Message.INCORRECT_MENU_NUMBER);
            }
        }
    }

    private void inputTrainingMenu(User user) {
        if (!authentication.checkAuthenticate(user)) {
            System.out.println(Message.UNAUTHENTICATED_USER);
            return;
        }
        String type = userInput.inputTrainingType();
        String date = userInput.inputDate();
        if (!DateHandler.checkInputDate(date)) {
            System.out.println(Message.INCORRECT_DATE);
            return;
        }
        LocalDate trainingDate = DateHandler.getDateFromString(date);
        String duration = userInput.getUserInput("Введите продолжительность тренировки в минутах");
        String spentCalories = userInput.getUserInput("Введите количество потраченных калорий");
        String description = userInput.getUserInput("Введите дополнительную информацию о тренировке (при необходимости)");
        trainingService.addNewTraining(user, type, trainingDate, Integer.parseInt(duration),
                Integer.parseInt(spentCalories), description);
    }

    private void deleteTrainingMenu(User user) {
        if (!authentication.checkAuthenticate(user)) {
            System.out.println(Message.UNAUTHENTICATED_USER);
            return;
        }
        String date = userInput.getUserInput("Введите дату тренировки, которую необходимо удалить в формате дд/мм/гггг");
        if (!DateHandler.checkInputDate(date)) {
            System.out.println(Message.INCORRECT_DATE);
            return;
        }
        List<Training> trainings = trainingService.getTrainings(user, date);
        if (trainings.isEmpty()) {
            System.out.println("За указанную дату тренировки отсутствуют");
            return;
        }
        reportPrinter.printTrainings(trainings);
        String trainingType = userInput.getUserInput("Введите тип тренировки, которую необходимо удалить");
        trainingService.deleteTraining(user, trainingType, DateHandler.getDateFromString(date));
    }

    public void editTrainingMenu(User user) {
        if (!authentication.checkAuthenticate(user)) {
            System.out.println(Message.UNAUTHENTICATED_USER);
            return;
        }
        String date = userInput.getUserInput("Введите дату тренировки, которую необходимо откорректировать в формате дд/мм/гггг");
        if (!DateHandler.checkInputDate(date)) {
            System.out.println(Message.INCORRECT_DATE);
            return;
        }
        List<Training> trainings = trainingService.getTrainings(user, date);
        if (trainings.isEmpty()) {
            System.out.println("За указанную дату тренировки отсутствуют");
            return;
        }
        reportPrinter.printTrainings(trainings);
        String trainingType = userInput.getUserInput("Введите тип тренировки, которую необходимо отредактировать");
        Map<String, String> editableData = chooseEditFieldInTraining();
        trainingService.editTraining(user, trainingType, DateHandler.getDateFromString(date), editableData);
    }

    private void getCaloriesReportMenu(User user) {
        String dateFrom = userInput.getUserInput("Введите дату начала периода в формате дд/мм/гггг");
        if (!DateHandler.checkInputDate(dateFrom)) {
            System.out.println(Message.INCORRECT_DATE);
            return;
        }
        String dateTo = userInput.getUserInput("Введите дату окончания периода в формате дд/мм/гггг");
        if (!DateHandler.checkInputDate(dateTo)) {
            System.out.println(Message.INCORRECT_DATE);
            return;
        }
        reportPrinter.printCaloriesReport(user, dateFrom, dateTo);
    }

    private Map<String, String> chooseEditFieldInTraining() {
        Map<String, String> map = new HashMap<>();
        System.out.println(Message.EDIT_MENU);
        String input = userInput.getUserInput();
        for (char c : input.toCharArray()) {
            String cInput = userInput.getUserInput("Введите новое значение для параметра - " + c);
            if (String.valueOf(c).equals("2")) {
                DateHandler.checkInputDate(cInput);
            }
            map.put(String.valueOf(c), cInput);
        }
        return map;
    }
}
