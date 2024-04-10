package model;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Scanner;

@RequiredArgsConstructor
public class Authentication {

    private UserService userService;
    private final Scanner scanner = new Scanner(System.in);

    public Authentication(UserService userService) {
        this.userService = userService;
    }


    public User login() {
        System.out.println("Введите ваше имя");
        String name = scanner.nextLine();
        System.out.println("Введите ваш пароль");
        String password = scanner.nextLine();
        Optional<User> user = userService.getAllUsers().stream()
                .filter(u -> u.getName().equals(name) && u.getPassword().equals(password))
                .findFirst();
        if (user.isEmpty()) {
            System.out.println("Введены некорректные данные, повторите ввод");
            return null;
        }
        userService.setCurrentUser(user.get());
        System.out.println("Вы успешно вошли в систему");
        return user.get();
    }
}
