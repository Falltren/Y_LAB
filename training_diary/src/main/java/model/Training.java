package model;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Training {

    private TrainingType type;

    @ToString.Exclude
    private LocalDate date;

    @ToString.Exclude
    private int duration;

    private int spentCalories;

    @ToString.Exclude
    private String description;

}
