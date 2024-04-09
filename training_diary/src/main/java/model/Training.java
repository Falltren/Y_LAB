package model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public abstract class Training {

    private String name;

    private LocalDate date;

    private int spentCalories;

    private String description;

}
