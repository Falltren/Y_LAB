package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Role role;

    private String name;

    private String password;

    private LocalDateTime registration;

    private List<Training> trainings;
}
