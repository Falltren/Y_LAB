package org.fallt.security;

import lombok.RequiredArgsConstructor;
import org.fallt.audit.Audit;
import org.fallt.audit.AuditWriter;
import org.fallt.model.User;
import org.fallt.service.UserService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Аутентификация пользователя
 */
@RequiredArgsConstructor
public class Authentication {

    private final Set<User> authenticatedUsers = new HashSet<>();

    private final UserService userService;

    private AuditWriter auditWriter = new AuditWriter();

    /**
     * Аутентификация пользователя, производится проверка наличия указанного пользователя и соответствующего ему пароля в хранилище
     * @param name Имя пользователя
     * @param password Пароль пользователя
     * @return При успешной аутентификации возвращается пользователь их хранилища, иначе будет возвращен null
     */
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
        auditWriter.write(new Audit(name, "user is logged in"));
        return authenticatedUser;
    }

    /**
     * Проверка наличия аутентификации у пользователя, в случае если пользователь не аутентифицирован, он получит соответствующее уведомление
     * @param user Пользователь
     * @return Результат наличия аутентификации у пользователя
     */
    public boolean checkAuthenticate(User user) {
        return authenticatedUsers.contains(user);
    }
}
