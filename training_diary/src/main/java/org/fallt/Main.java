package org.fallt;

import org.fallt.in.UserInput;
import org.fallt.out.ReportPrinter;
import org.fallt.repository.UserDao;
import org.fallt.repository.UserRepository;
import org.fallt.repository.impl.TrainingDaoImpl;
import org.fallt.repository.impl.UserDaoImpl;
import org.fallt.security.Authentication;
import org.fallt.security.Registration;
import org.fallt.service.TrainingService;
import org.fallt.service.UserMenu;
import org.fallt.service.UserService;
import org.fallt.util.DBUtils;
import org.fallt.util.LiquibaseRunner;

public class Main {

    public static void main(String[] args) {
        LiquibaseRunner liquibaseRunner = new LiquibaseRunner();
        liquibaseRunner.run();
        UserInput userInput = new UserInput();
        UserDao userDao = new UserDaoImpl(DBUtils.getConnection());
        UserService userService = new UserService(userDao);
        Registration registration = new Registration(userService);
        Authentication authentication = new Authentication(userService);
        TrainingService trainingService = new TrainingService(userService, new TrainingDaoImpl(DBUtils.getConnection()));
        ReportPrinter reportPrinter = new ReportPrinter(userService);
        UserMenu userMenu = new UserMenu(userInput, userService, registration, authentication, trainingService, reportPrinter);
        userMenu.start();
    }
}
