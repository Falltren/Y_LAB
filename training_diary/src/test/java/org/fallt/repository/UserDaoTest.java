package org.fallt.repository;

import org.fallt.config.ContainerEnvironment;
import org.fallt.model.Role;
import org.fallt.model.User;
import org.fallt.repository.impl.UserDaoImpl;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class UserDaoTest {

    private UserDao userDao;

    @Test
    void testGetUserByName_thenGetOptionalUser() throws Exception {
        try (PostgreSQLContainer<?> container = ContainerEnvironment.postgreSQLContainer) {
            container.start();
            userDao = new UserDaoImpl(DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword()));
            User user = userDao.getUserByName("Jack").orElse(new User());
            assertThat(user.getName()).isEqualTo("Jack");
            container.stop();
        }
    }

    @Test
    void testFindAll_thenGetListUsers() throws Exception {
        try (PostgreSQLContainer<?> container = ContainerEnvironment.postgreSQLContainer) {
            container.start();
            userDao = new UserDaoImpl(DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword()));
            List<User> users = userDao.findAll();
            assertThat(users).hasSize(1);
            assertThat(users.get(0).getPassword()).isEqualTo("pass1");
            container.stop();
        }
    }

    @Test
    void testCreate_thenAddNewUser() throws Exception {
        try (PostgreSQLContainer<?> container = ContainerEnvironment.postgreSQLContainer) {
            container.start();
            Connection connection = DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
            connection.setAutoCommit(false);
            userDao = new UserDaoImpl(connection);
            User user = new User(2L, Role.ROLE_ADMIN, "admin", "adminPass", LocalDateTime.now(), new HashSet<>());
            userDao.create(user);
            User createdUser = userDao.getUserByName("admin").orElse(new User());
            assertThat(createdUser).isEqualTo(user);
            container.stop();
        }
    }
}
