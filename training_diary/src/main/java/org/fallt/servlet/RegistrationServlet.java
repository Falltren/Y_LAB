package org.fallt.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fallt.dto.request.RegisterRq;
import org.fallt.dto.response.RegisterRs;
import org.fallt.security.Registration;
import org.fallt.util.InstanceCreator;

import java.io.IOException;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {

    private final Registration registration;

    private final ObjectMapper objectMapper;

    public RegistrationServlet() {
        this.registration = new Registration(InstanceCreator.getUserService());
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RegisterRq registerRq = objectMapper.readValue(req.getInputStream(), RegisterRq.class);
        if (registerRq.getName() == null || registerRq.getPassword() == null || registerRq.getConfirmPassword() == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Все данные должны быть заполнены");
            return;
        }
        RegisterRs responseDto = registration.register(registerRq);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        byte[] bytes = objectMapper.writeValueAsBytes(responseDto);
        resp.getOutputStream().write(bytes);
    }
}
