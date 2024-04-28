package org.fallt.util;

import jakarta.servlet.http.HttpServletRequest;
import org.fallt.service.UserService;

public class SessionUtils {

    private final UserService userService;



    private SessionUtils() {
        userService = InstanceCreator.getUserService();
    }

    public static boolean isAuthenticated(HttpServletRequest request) {
        return request.getSession(false) != null && request.getSession().getAttribute("user") != null;
    }

}
