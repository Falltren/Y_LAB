package org.fallt.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fallt.dto.request.LoginRq;
import org.fallt.dto.response.LoginRs;
import org.fallt.security.Authentication;
import org.fallt.util.InstanceCreator;

import java.io.IOException;

@WebServlet("/user/login")
public class AuthenticationServlet extends HttpServlet {

    private final Authentication authentication;

    private final ObjectMapper objectMapper;

    public AuthenticationServlet() {
        this.authentication = InstanceCreator.getAuthentication();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LoginRq request = objectMapper.readValue(req.getInputStream(), LoginRq.class);
        LoginRs responseDto = authentication.login(request);
        req.getSession().setAttribute("user", request.getName());
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        byte[] bytes = objectMapper.writeValueAsBytes(responseDto);
        resp.getOutputStream().write(bytes);
    }
}
