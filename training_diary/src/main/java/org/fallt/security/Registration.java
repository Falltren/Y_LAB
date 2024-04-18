package org.fallt.security;

import org.fallt.audit.Audit;
import org.fallt.audit.AuditWriter;
import org.fallt.model.Role;
import org.fallt.model.User;
import org.fallt.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Регистрация нового пользователя, Один пользователь с ролью ADMIN добавляется автоматически
 */
public class Registration {

    private UserService userService;

    private AuditWriter auditWriter;

    public Registration(UserService userService) {
        this.userService = userService;
        auditWriter = new AuditWriter();
        userService.addUser(new User(1L, Role.ADMIN, "admin", "admin", LocalDateTime.now(), new HashSet<>()));
    }

    /**
     * Регистрация нового пользователя, если пользователь с указанным именем уже существует,
     * пользователь получит об этом соответствующее уведомление
     *
     * @param name            Имя пользователя
     * @param password        Пароль
     * @param confirmPassword Подтверждение пароля
     */
    public void register(String name, String password, String confirmPassword) {
        if (!checkPassword(password, confirmPassword)) {
            System.out.println("Введенные пароли не совпадают, повторите ввод");
            return;
        }
        User user = new User(1L, Role.USER, name, password, LocalDateTime.now(), new HashSet<>());
        userService.addUser(user);
        auditWriter.write(new Audit(name, "user registered"));
    }

    /**
     * Валидация введенных пользователем паролей
     *
     * @param password        Пароль
     * @param confirmPassword Подтверждение пароля
     * @return Результат проверки совпадения паролей
     */
    private boolean checkPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

}
