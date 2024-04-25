package org.fallt.repository;

import org.fallt.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для взаимодействия с таблицей пользователей в базе данных
 */
public interface UserDao {
    /**
     * Сохранение нового пользователя
     * @param user Объект класса User
     */
    void create(User user);

    /**
     * Метод поиска пользователя по имени
     * @param name Имя пользователя
     * @return Объект Optional с найденным по указанному имени пользователем
     */
    Optional<User> getUserByName(String name);

    /**
     * Метод возвращает всех пользователей, находящихся в базе данных
     * @return Список пользователей
     */
    List<User> findAll();

}
