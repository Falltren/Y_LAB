package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Training {

    private TrainingType type;

    private LocalDate date;

    private int duration;

    private int spentCalories;

    private String description;

}
