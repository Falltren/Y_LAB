package model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class TrainingType {

    private String type;

    @Override
    public String toString() {
        return type;
    }

}

