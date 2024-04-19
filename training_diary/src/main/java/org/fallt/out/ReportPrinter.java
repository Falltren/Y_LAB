package org.fallt.out;

import lombok.RequiredArgsConstructor;
import org.fallt.model.Role;
import org.fallt.model.Training;
import org.fallt.model.User;
import org.fallt.service.UserService;
import org.fallt.util.DateHandler;
import org.fallt.util.ReportCreator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Класс для вывода отчетов пользователей
 */
@RequiredArgsConstructor
public class ReportPrinter {

    private static final String DATE_PATTERN = "dd/MM/yyyy";

    private final UserService userService;

    /**
     * Вывод информации о всех пользователях
     */
    public void printAllUserInfo() {
        List<User> users = userService.getAllUsers().stream()
                .filter(u -> !u.getRole().equals(Role.ROLE_ADMIN))
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
     * Метод предназначен для вывода данных по тренировкам пользователя
     * @param trainings Set, хранящий тренировки пользователя
     */
    public void printAllTrainings(Set<Training> trainings) {
        Map<LocalDate, List<Training>> report = ReportCreator.getUserReport(trainings);
        for (Map.Entry<LocalDate, List<Training>> entry : report.entrySet()) {
            System.out.println(entry.getKey().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
            printTrainings(entry.getValue());
        }
    }

    /**
     * Вывод информации о тренировках
     * @param trainings Список тренировок
     */
    public void printTrainings(List<Training> trainings) {
        for (Training training : trainings) {
            System.out.println("Тренировка: " + "\n"
                    + "тип тренировки: " + training.getType() + "\n"
                    + "продолжительность: " + training.getDuration() + " мин" + "\n"
                    + "количество затраченных калорий " + training.getSpentCalories() + " кал" + "\n"
                    + "дополнительная информация: " + training.getDescription() + "\n");
        }
    }

    /**
     * Метод предназначен для вывода отчета о затраченных пользователем калориях
     * @param user Пользователь
     * @param dateFrom Дата начала отчета
     * @param dateTo Дата окончания отчета
     */
    public void printCaloriesReport(User user, String dateFrom, String dateTo) {
        Set<Training> trainings = userService.getUserByName(user.getName()).getTrainings();
        Map<LocalDate, Integer> caloriesReport = ReportCreator.getCaloriesReport(DateHandler.getDateFromString(dateFrom), DateHandler.getDateFromString(dateTo), trainings);
        caloriesReport.forEach((key, value) -> System.out.println(key.format(DateTimeFormatter.ofPattern(DATE_PATTERN)) + " " + value + " кал" + "\n"));
    }
}
