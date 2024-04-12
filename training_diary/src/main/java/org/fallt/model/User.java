package org.fallt.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"role", "registration", "trainings"})
public class User {

    private Role role;

    private String name;

    private String password;

    private LocalDateTime registration;

    private List<Training> trainings;
}
