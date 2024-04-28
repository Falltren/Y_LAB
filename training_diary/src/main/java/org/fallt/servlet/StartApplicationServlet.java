package org.fallt.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import org.fallt.util.InstanceCreator;

@WebServlet(value = "/start", loadOnStartup = 1)
public class StartApplicationServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        InstanceCreator.injectDependency();
    }
}
