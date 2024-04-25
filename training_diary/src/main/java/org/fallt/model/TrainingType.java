package org.fallt.model;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TrainingType {

    private Integer id;

    private String type;

    @Override
    public String toString() {
        return type;
    }

}

