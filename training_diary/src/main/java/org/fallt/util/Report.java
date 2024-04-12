package org.fallt.util;

import org.fallt.model.Training;
import org.fallt.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Report {

    private Report() {
    }

    public static Map<LocalDate, List<Training>> getUserReport(List<Training> trainings) {
        return trainings.stream()
                .collect(Collectors.groupingBy(Training::getDate, HashMap::new, Collectors.toCollection(ArrayList::new)));
    }

    public static Map<LocalDate, Integer> getCaloriesReport(LocalDate from, LocalDate to, List<Training> trainings) {
        var userReport = getUserReport(trainings);
        return userReport.entrySet().stream()
                .filter(e -> (e.getKey().isAfter(from) || e.getKey().equals(from)) && (e.getKey().isBefore(to) || e.getKey().equals(to)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey, entry -> entry.getValue().stream()
                                .map(Training::getSpentCalories)
                                .reduce(0, Integer::sum)));
    }

    public static Map<String, Map<LocalDate, List<Training>>> getFullReport(List<User> users) {
        Map<String, Map<LocalDate, List<Training>>> fullReport = new HashMap<>();
        for (User user : users) {
            String key = user.getName();
            var value = getUserReport(user.getTrainings());
            fullReport.put(key, value);
        }
        return fullReport;
    }

}
