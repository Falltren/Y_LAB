package org.fallt.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fallt.dto.request.TrainingDto;
import org.fallt.service.TrainingService;
import org.fallt.util.InstanceCreator;
import org.fallt.util.SessionUtils;

import java.io.IOException;
import java.util.List;

@WebServlet("/training")
public class GetAllTrainingsServlet extends HttpServlet {

    private final TrainingService trainingService;

    private final ObjectMapper objectMapper;

    public GetAllTrainingsServlet() {
        trainingService = InstanceCreator.getTrainingService();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtils.isAuthenticated(req)) {
            resp.sendRedirect("/user/login");
            return;
        }
        String userName = String.valueOf(req.getSession().getAttribute("user"));
        List<TrainingDto> response = trainingService.getTrainings(userName);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        byte[] bytes = objectMapper.writeValueAsBytes(response);
        resp.getOutputStream().write(bytes);
    }
}
