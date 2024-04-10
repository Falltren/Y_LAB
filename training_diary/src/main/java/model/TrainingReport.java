package model;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TrainingReport {

    private TrainingReport() {
    }

    public static Map<LocalDate, List<Training>> getFullReportByDate(List<Training> trainings) {
        return trainings.stream()
                .collect(Collectors.groupingBy(Training::getDate, HashMap::new, Collectors.toCollection(ArrayList::new)));
    }
}
