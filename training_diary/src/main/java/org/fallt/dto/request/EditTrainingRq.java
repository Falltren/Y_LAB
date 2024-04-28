package org.fallt.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditTrainingRq {

    private String currentType;

    private LocalDate currentDate;

    private TrainingDto newValue;
}
