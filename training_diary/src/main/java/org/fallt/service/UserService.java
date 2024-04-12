package org.fallt.service;

import org.fallt.audit.Audit;
import org.fallt.audit.AuditWriter;
import org.fallt.model.User;
import org.fallt.repository.UserBase;

import java.util.List;

/**
 * Сервис для работы с пользователями
 */
public class UserService {

    private UserBase userBase;
    private AuditWriter auditWriter;

    public UserService(UserBase userBase) {
        this.userBase = userBase;
        auditWriter = new AuditWriter();
    }

    /**
     * Осуществляет поиск пользователя по имени
     * @param name Имя пользователя
     * @return В случае наличия пользователя в хранилище будет возвращен пользователь, иначе будет возвращен null
     */
    public User getUserByName(String name) {
        auditWriter.write(new Audit(name, "get user"));
        return userBase.getUserByName(name).orElse(null);
    }

    /**
     * Возвращает список всех пользователей из хранилища
     * @return Список пользователей
     */
    public List<User> getAllUsers() {
        return userBase.getAllUser();
    }

    /**
     * Добавляет нового пользователя в хранилище
     * @param user Новый пользователь
     */
    public void addUser(User user) {
        userBase.addUser(user);
        auditWriter.write(new Audit(user.getName(), "save user"));
    }

}
