package org.fallt.security;

import org.fallt.model.Role;
import org.fallt.model.User;
import org.fallt.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Registration {

    private UserService userService;

    public Registration(UserService userService) {
        this.userService = userService;
        userService.addUser(new User(Role.ADMIN, "admin", "admin", LocalDateTime.now(), new ArrayList<>()));
    }

    public void register(String name, String password, String confirmPassword) {
        if (!checkPassword(password, confirmPassword)) {
            System.out.println("Введенные пароли не совпадают, повторите ввод");
            return;
        }
        User user = new User(Role.USER, name, password, LocalDateTime.now(), new ArrayList<>());
        userService.addUser(user);
    }

    private boolean checkPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

}
