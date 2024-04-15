package org.fallt.in;

import org.fallt.model.Role;
import org.fallt.model.Training;
import org.fallt.model.User;
import org.fallt.repository.UserRepository;
import org.fallt.security.Authentication;
import org.fallt.security.Registration;
import org.fallt.service.TrainingService;
import org.fallt.service.UserService;
import org.fallt.util.Message;
import org.fallt.util.ReportCreator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Класс для взаимодействия пользователя с приложением
 */
public class UserDisplay {

    private UserRepository userRepository;
    private Scanner scanner;
    private Registration registration;
    private Authentication authentication;
    private UserService userService;
    private TrainingService trainingService;

    private boolean isStop;

    private static final String DATE_PATTERN = "dd/MM/yyyy";

    public UserDisplay() {
        this.userRepository = new UserRepository();
        this.scanner = new Scanner(System.in);
        this.userService = new UserService(userRepository);
        this.registration = new Registration(userService);
        this.authentication = new Authentication(userService);
        this.trainingService = new TrainingService(userService);
        this.isStop = false;
    }

    /**
     * Запуск главного меню приложения с возможностью зарегистрироваться, входа в систему и завершения работы приложения
     */
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

    /**
     * Ввод пользователем регистрационных данных
     */
    private void register() {
        System.out.println("Введите ваше имя");
        String name = scanner.nextLine();
        if (userService.getUserByName(name) != null) {
            System.out.println("Пользователь с таким именем уже существует, введите новое имя");
            return;
        }
        System.out.println("Введите пароль");
        String password = scanner.nextLine();
        System.out.println("Повторите пароль");
        String confirmPassword = scanner.nextLine();
        registration.register(name, password, confirmPassword);
    }

    /**
     * Ввод пользователем учетных данных для входа в систему
     */
    private void authentication() {
        System.out.println("Введите ваше имя");
        String name = scanner.nextLine();
        System.out.println("Введите ваш пароль");
        String password = scanner.nextLine();
        User registerUser = authentication.login(name, password);
        if (registerUser == null) {
            return;
        }
        if (registerUser.getRole().equals(Role.ADMIN)) {
            getAdminMenu();
        } else {
            getUserMenu(registerUser);
        }
    }

    /**
     * Предоставлению аутентифицированному пользователю главного меню пользователя с возможностью добавления,
     * удаления, редактирования тренировок, а также получения отчета по всем тренировкам и потраченным калориям в разрезе дней
     * @param user Пользователь
     */
    private void getUserMenu(User user) {
        while (true) {
            System.out.println(Message.USER_MENU);
            String selection = scanner.nextLine();
            switch (selection) {
                case "1" -> inputTrainingData(user);
                case "2" -> deleteTraining(user);
                case "3" -> editTraining(user);
                case "4" -> printAllTrainings(user.getTrainings());
                case "5" -> printCaloriesReport(user);
                case "0" -> {
                    return;
                }
                default -> System.out.println(Message.INCORRECT_MENU_NUMBER);
            }
        }
    }

    /**
     * Вывод на экран пользователя отчета по затраченным калориям по дням за указанный период времени
     * @param user Пользователь
     */
    private void printCaloriesReport(User user) {
        System.out.println("Введите дату начала периода в формате дд/мм/гггг");
        String dateFrom = scanner.nextLine();
        if (!checkInputDate(dateFrom)) {
            System.out.println();
            return;
        }
        System.out.println("Введите дату окончания периода в формате дд/мм/гггг");
        String dateTo = scanner.nextLine();
        if (!checkInputDate(dateFrom)) {
            System.out.println();
            return;
        }
        List<Training> trainings = userService.getUserByName(user.getName()).getTrainings();
        var caloriesReport = ReportCreator.getCaloriesReport(getDateFromString(dateFrom), getDateFromString(dateTo), trainings);
        caloriesReport.forEach((key, value) -> System.out.println(key.format(DateTimeFormatter.ofPattern(DATE_PATTERN)) + " " + value + " кал" + "\n"));
    }

    /**
     * Вывод администратору основного меню с возможностью просмотра данных по тренировкам всех пользователей
     */
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

    /**
     * Вывод пользователю экрана редактирования тренировки, используется вспомогательный метод {@link #chooseEditFieldInTraining() chooseEditFieldInTraining}
     * @param user Пользователь
     */
    private void editTraining(User user) {
        if (!authentication.checkAuthenticate(user)) {
            System.out.println(Message.UNAUTHENTICATED_USER);
            return;
        }
        System.out.println("Введите дату тренировки, которую необходимо отредактировать в формате дд/мм/гггг");
        String date = scanner.nextLine();
        if (!checkInputDate(date)) {
            System.out.println(Message.INCORRECT_DATE);
            return;
        }
        List<Training> trainings = trainingService.watchTrainings(user, date);
        if (trainings.isEmpty()) {
            System.out.println("За указанную дату тренировки отсутствуют");
            return;
        }
        printTrainings(trainings);
        System.out.println("Введите тип тренировки, которую необходимо отредактировать");
        String trainingType = scanner.nextLine();
        Map<String, String> editableData = chooseEditFieldInTraining();
        trainingService.editTraining(user, trainingType, getDateFromString(date), editableData);
    }

    /**
     * Предоставление пользователю меню ввода данных новой тренировки
     * @param user Пользователь
     */
    private void inputTrainingData(User user) {
        if (!authentication.checkAuthenticate(user)) {
            System.out.println(Message.UNAUTHENTICATED_USER);
            return;
        }
        System.out.println("Введите тип тренировки");
        String type = scanner.nextLine();
        System.out.println("Введите дату тренировки в формате дд/мм/гггг");
        String date = scanner.nextLine();
        if (!checkInputDate(date)) {
            System.out.println(Message.INCORRECT_DATE);
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

    /**
     * Предоставлению пользователю меню удаления тренировки, поиск удаляемой тренировки осуществляется по дате и типу тренировки
     * @param user
     */
    private void deleteTraining(User user) {
        if (!authentication.checkAuthenticate(user)) {
            System.out.println(Message.UNAUTHENTICATED_USER);
            return;
        }
        System.out.println("Введите дату тренировки, которую необходимо удалить в формате дд/мм/гггг");
        String date = scanner.nextLine();
        if (!checkInputDate(date)) {
            System.out.println(Message.INCORRECT_DATE);
            return;
        }
        List<Training> trainings = trainingService.watchTrainings(user, date);
        if (trainings.isEmpty()) {
            System.out.println("За указанную дату тренировки отсутствуют");
            return;
        }
        printTrainings(trainings);
        System.out.println("Введите тип тренировки, которую необходимо удалить");
        String trainingType = scanner.nextLine();
        trainingService.deleteTraining(user, trainingType, getDateFromString(date));
    }

    /**
     * Вспомогательный метод проверки введенной пользователем даты,
     * при некорректном вводе пользователь получит соответствующее уведомление в месте вызова метода
     * @param input Дата в виде строки
     * @return Результат проверки корректности введенной даты
     */
    private boolean checkInputDate(String input) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN);
        try {
            dateFormat.parse(input);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Преобразует введенную пользователем строку в LocalDate
     * @param date
     * @return
     */
    private LocalDate getDateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        return LocalDate.parse(date, formatter);
    }

    /**
     * Вывод пользователю данных о тренировках
     * @param trainings
     */
    private void printTrainings(List<Training> trainings) {
        for (Training training : trainings) {
            System.out.println("Тренировка: " + "\n"
                    + "тип тренировки: " + training.getType() + "\n"
                    + "продолжительность: " + training.getDuration() + " мин" + "\n"
                    + "количество затраченных калорий " + training.getSpentCalories() + " кал" + "\n"
                    + "дополнительная информация: " + training.getDescription() + "\n");
        }
    }

    /**
     * Получает отчет по тренировкам пользователя и формирует удобочитаемый вывод для пользователя
     * @param trainings Список тренировок
     */
    private void printAllTrainings(List<Training> trainings) {
        Map<LocalDate, List<Training>> report = ReportCreator.getUserReport(trainings);
        for (Map.Entry<LocalDate, List<Training>> entry : report.entrySet()) {
            System.out.println(entry.getKey().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
            printTrainings(entry.getValue());
        }
    }

    /**
     * Метод предоставляет данные о всех тренировках пользователей, доступен только пользователям с ролью ADMIN
     */
    private void printAllUserInfo() {
        List<User> users = userService.getAllUsers().stream()
                .filter(u -> !u.getRole().equals(Role.ADMIN))
                .toList();
        var report = ReportCreator.getFullReport(users);
        for (Map.Entry<String, Map<LocalDate, List<Training>>> entry : report.entrySet()) {
            System.out.println(entry.getKey());
            for (Map.Entry<LocalDate, List<Training>> userInfo : entry.getValue().entrySet()) {
                System.out.println(userInfo.getKey().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
                printTrainings(userInfo.getValue());
            }
        }
    }

    /**
     * Метод для формирования Map, содержащей значения для корректировки информации о тренировке
     * @return Map с обновленным значениям (value) и шифры полей экземпляра тренировки (key)
     */
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
