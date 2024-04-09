package model;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

public class Registration {

    private UserBase base;

    private Scanner scanner = new Scanner(System.in);

    public void register() {
        System.out.println("Введите ваше имя");
        String name = scanner.nextLine();
        if (isExistedUser(name)) {
            System.out.println("Пользователь с таким именем уже существует, введите новое имя");
            return;
        }
        System.out.println("Введите пароль");
        String password = scanner.nextLine();
        System.out.println("Повторите пароль");
        String confirmPassword = scanner.nextLine();
        if (!checkPassword(password, confirmPassword)) {
            System.out.println("Введенные пароли не совпадают, повторите ввод");
            return;
        }
        User user = new User(Role.USER, name, password, LocalDateTime.now());
        user.setName(name);
    }

    private boolean checkPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    private boolean isExistedUser(String name) {
        Optional<User> optionalUser = base.getUserByName(name);
        return optionalUser.isPresent();
    }
}
