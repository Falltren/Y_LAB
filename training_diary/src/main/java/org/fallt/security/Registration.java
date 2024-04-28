package org.fallt.security;

import org.fallt.audit.Audit;
import org.fallt.audit.AuditWriter;
import org.fallt.dto.request.RegisterRq;
import org.fallt.dto.response.RegisterRs;
import org.fallt.exception.BadRequestException;
import org.fallt.mapper.UserMapper;
import org.fallt.model.User;
import org.fallt.service.UserService;

/**
 * Регистрация нового пользователя
 */
public class Registration {

    private UserService userService;

    private AuditWriter auditWriter;

    public Registration(UserService userService) {
        this.userService = userService;
        auditWriter = new AuditWriter();
    }

    /**
     * Регистрация пользователя
     *
     * @param registerRq Введенные пользователем данные
     */
    public RegisterRs register(RegisterRq registerRq) {
        if (!checkPassword(registerRq.getPassword(), registerRq.getConfirmPassword())) {
            throw new BadRequestException("Введенные пароли не совпадают, повторите ввод");
        }
        if (userService.getUserByName(registerRq.getName()) != null) {
            throw new BadRequestException("Пользователь с таким именем уже существует");
        }
        User user = UserMapper.INSTANCE.dtoToEntity(registerRq);
        userService.addUser(user);
        auditWriter.write(new Audit(registerRq.getName(), "user registered"));
        return UserMapper.INSTANCE.toRegisterResponse(user);
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
