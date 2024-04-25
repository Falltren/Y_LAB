package org.fallt.service;

import org.fallt.audit.Audit;
import org.fallt.audit.AuditWriter;
import org.fallt.model.User;
import org.fallt.repository.UserDao;

import java.util.Collection;

/**
 * Сервис для работы с пользователями
 */
public class UserService {

    private UserDao userDao;
    private AuditWriter auditWriter;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
        auditWriter = new AuditWriter();
    }

    /**
     * Осуществляет поиск пользователя по имени
     *
     * @param name Имя пользователя
     * @return В случае наличия пользователя в хранилище будет возвращен пользователь, иначе будет возвращен null
     */
    public User getUserByName(String name) {
        auditWriter.write(new Audit(name, "get user"));
        return userDao.getUserByName(name).orElse(null);
    }

    /**
     * Возвращает список всех пользователей из хранилища
     *
     * @return Список пользователей
     */
    public Collection<User> getAllUsers() {
        return userDao.findAll();
    }

    /**
     * Добавляет нового пользователя в хранилище
     *
     * @param user Новый пользователь
     */
    public void addUser(User user) {
        userDao.create(user);
        auditWriter.write(new Audit(user.getName(), "save user"));
    }

}
