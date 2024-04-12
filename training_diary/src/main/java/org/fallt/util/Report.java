package org.fallt.util;

import org.fallt.model.Training;
import org.fallt.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public final class Report {

    private Report() {
    }

    public static Map<LocalDate, List<Training>> getUserReport(List<Training> trainings) {
        return trainings.stream()
                .collect(Collectors.groupingBy(Training::getDate, HashMap::new, Collectors.toCollection(ArrayList::new)));
    }

//    public static Map<LocalDate, Integer> getCaloriesReport(LocalDate from, LocalDate to){
//        var userReport =
//    }

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
