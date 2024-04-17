package org.fallt;

import org.fallt.in.UserInput;
import org.fallt.service.UserMenu;
import org.fallt.out.ReportPrinter;
import org.fallt.repository.UserRepository;
import org.fallt.security.Authentication;
import org.fallt.security.Registration;
import org.fallt.service.TrainingService;
import org.fallt.service.UserService;

public class Main {

    public static void main(String[] args) {
        UserInput userInput = new UserInput();
        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);
        Registration registration = new Registration(userService);
        Authentication authentication = new Authentication(userService);
        TrainingService trainingService = new TrainingService(userService);
        ReportPrinter reportPrinter = new ReportPrinter(userService);
        UserMenu userMenu = new UserMenu(userInput, userService, registration, authentication, trainingService, reportPrinter);
        userMenu.start();
    }
}
