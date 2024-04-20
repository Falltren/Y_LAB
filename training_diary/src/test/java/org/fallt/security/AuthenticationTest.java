package org.fallt.security;

import org.fallt.model.Role;
import org.fallt.model.User;
import org.fallt.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class AuthenticationTest {

    private Authentication authentication;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = Mockito.mock(UserService.class);
        authentication = new Authentication(userService);
    }

    @Test
    @DisplayName("Test login")
    void testLogin() {
        User user = new User(1L, Role.ROLE_USER, "John", "123", LocalDateTime.now(), new HashSet<>());
        when(userService.getUserByName("John")).thenReturn(user);
        String name = "John";
        String password = "123";
        User actual = authentication.login(name, password);
        assertThat(actual).isEqualTo(user);
    }

    @Test
    @DisplayName("Test login with incorrect password")
    void testLoginWithWrongData() {
        User user = new User(1L, Role.ROLE_USER, "John", "123", LocalDateTime.now(), new HashSet<>());
        when(userService.getAllUsers()).thenReturn(List.of(user));
        String name = "John";
        String password = "222";
        User actual = authentication.login(name, password);
        assertThat(actual).isNull();
    }
}
