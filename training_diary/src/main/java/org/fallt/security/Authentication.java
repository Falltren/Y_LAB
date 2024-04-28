package org.fallt.security;

import lombok.RequiredArgsConstructor;
import org.fallt.audit.Audit;
import org.fallt.audit.AuditWriter;
import org.fallt.dto.request.LoginRq;
import org.fallt.dto.response.LoginRs;
import org.fallt.mapper.UserMapper;
import org.fallt.model.User;
import org.fallt.service.UserService;

import java.util.HashSet;
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
     *
     * @param request
     * @return Пользователь
     */
    public LoginRs login(LoginRq request) {
        User user = userService.getUserByName(request.getName());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            System.out.println("Введены некорректные данные, повторите ввод");
            return null;
        }
        authenticatedUsers.add(user);
        System.out.println("Вы успешно вошли в систему");
        auditWriter.write(new Audit(request.getName(), "user is logged in"));
        return UserMapper.INSTANCE.toLoginResponse(user);
    }

    /**
     * Проверка наличия аутентификации у пользователя, в случае если пользователь не аутентифицирован, он получит соответствующее уведомление
     *
     * @param user Пользователь
     * @return Результат наличия аутентификации у пользователя
     */
    public boolean checkAuthenticate(User user) {
        return authenticatedUsers.contains(user);
    }
}
