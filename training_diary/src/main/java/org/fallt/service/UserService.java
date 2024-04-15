package org.fallt.service;

import org.fallt.audit.Audit;
import org.fallt.audit.AuditWriter;
import org.fallt.model.User;
import org.fallt.repository.UserRepository;

import java.util.Collection;
import java.util.List;

/**
 * Сервис для работы с пользователями
 */
public class UserService {

    private UserRepository userRepository;
    private AuditWriter auditWriter;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        auditWriter = new AuditWriter();
    }

    /**
     * Осуществляет поиск пользователя по имени
     * @param name Имя пользователя
     * @return В случае наличия пользователя в хранилище будет возвращен пользователь, иначе будет возвращен null
     */
    public User getUserByName(String name) {
        auditWriter.write(new Audit(name, "get user"));
        return userRepository.getUserByName(name).orElse(null);
    }

    /**
     * Возвращает список всех пользователей из хранилища
     * @return Список пользователей
     */
    public Collection<User> getAllUsers() {
        return userRepository.getAllUser();
    }

    /**
     * Добавляет нового пользователя в хранилище
     * @param user Новый пользователь
     */
    public void addUser(User user) {
        userRepository.addUser(user);
        auditWriter.write(new Audit(user.getName(), "save user"));
    }

}
