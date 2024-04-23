package org.fallt.repository;

import org.fallt.model.Role;
import org.fallt.model.User;
import org.fallt.repository.impl.UserDaoImpl;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class UserDaoTest {

    private UserDao userDao;

    @Container
    private PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.4"))
            .withUsername("testUser")
            .withPassword("testSecret")
            .withDatabaseName("testDatabase")
            .withInitScript("init_script_test.sql");

    @Test
    void testGetUserByName_thenGetOptionalUser() throws Exception {
        userDao = new UserDaoImpl(DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword()));
        User user = userDao.getUserByName("Jack").orElse(new User());
        assertThat(user.getName()).isEqualTo("Jack");
    }

    @Test
    void testFindAll_thenGetListUsers() throws Exception {
        userDao = new UserDaoImpl(DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword()));
        List<User> users = userDao.findAll();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getPassword()).isEqualTo("pass1");
    }

    @Test
    void testCreate_thenAddNewUser() throws Exception {
        Connection connection = DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        connection.setAutoCommit(false);
        userDao = new UserDaoImpl(connection);
        User user = new User();
        user.setRole(Role.ROLE_ADMIN);
        user.setName("admin");
        user.setPassword("adminPass");
        user.setRegistration(LocalDateTime.now());
        user.setTrainings(new HashSet<>());
        userDao.create(user);
        User createdUser = userDao.getUserByName("admin").orElse(new User());
        assertThat(createdUser.getId()).isEqualTo(2);
        assertThat(createdUser.getName()).isEqualTo("admin");
    }
}
