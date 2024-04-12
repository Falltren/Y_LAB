package org.fallt.security;

import lombok.RequiredArgsConstructor;
import org.fallt.model.User;
import org.fallt.service.UserService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class Authentication {

    private final Set<User> authenticatedUsers = new HashSet<>();

    private final UserService userService;

    public User login(String name, String password) {
        Optional<User> optionalUser = userService.getAllUsers().stream()
                .filter(u -> u.getName().equals(name) && u.getPassword().equals(password))
                .findFirst();
        if (optionalUser.isEmpty()) {
            System.out.println("Введены некорректные данные, повторите ввод");
            return null;
        }
        User authenticatedUser = optionalUser.get();
        authenticatedUsers.add(authenticatedUser);
        System.out.println("Вы успешно вошли в систему");
        return authenticatedUser;
    }

    public boolean checkAuthenticate(User user) {
        return authenticatedUsers.contains(user);
    }
}