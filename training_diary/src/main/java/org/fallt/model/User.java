package org.fallt.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"role", "registration", "trainings"})
public class User {

    private Long id;

    private Role role;

    private String name;

    private String password;

    private LocalDateTime registration;

    private Set<Training> trainings;
}
