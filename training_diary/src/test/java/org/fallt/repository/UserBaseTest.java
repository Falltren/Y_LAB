package org.fallt.repository;

import org.fallt.model.Role;
import org.fallt.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class UserBaseTest {

    private UserBase userBase;

    @BeforeEach
    public void setUp() {
        this.userBase = new UserBase();
    }

    @Test
    @DisplayName("Test add user")
    void testAddUser() {
        User newUser = createUser();
        userBase.addUser(newUser);
        assertThat(userBase.getAllUser()).hasSize(1);
    }

    @Test
    @DisplayName("Test get user by name")
    void testGetUserByName() {
        User expectedUser = createUser();
        userBase.addUser(expectedUser);
        User actualUser = userBase.getUserByName("John").orElseThrow();
        assertThat(actualUser).isEqualTo(expectedUser);
    }


    private User createUser() {
        return new User(Role.USER, "John", "123", LocalDateTime.now(), new ArrayList<>());
    }
}
