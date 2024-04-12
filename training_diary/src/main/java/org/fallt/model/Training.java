package org.fallt.model;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Training {

    private TrainingType type;

    private LocalDate date;

    private int duration;

    private int spentCalories;

    private String description;

}