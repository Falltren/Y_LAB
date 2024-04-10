package in;

import model.*;

import java.util.Scanner;

public class Menu {

    private UserBase userBase;
    private Scanner scanner;

    private Registration registration;

    private Authentication authentication;

    private UserService userService;

    private TrainingService trainingService;

    private boolean isStop;

    public Menu() {
        this.userBase = new UserBase();
        this.scanner = new Scanner(System.in);
        this.registration = new Registration(userBase);
        this.userService = new UserService(userBase);
        this.authentication = new Authentication(userService);
        this.trainingService = new TrainingService(userService);
        this.isStop = false;
    }

    public void start() {
        while (!isStop) {
            System.out.println(Message.MAIN_MENU);
            int selection = scanner.nextInt();
            if (selection < 0 || selection > 3) {
                System.out.println(Message.INCORRECT_MENU_NUMBER);
            }
            switch (selection) {
                case 1 -> register();
                case 2 -> authentication();
            }
        }
    }

    private void register() {
        registration.register();
    }

    private void authentication() {
        boolean isRegister = authentication.login();
        if (isRegister){
            System.out.println(Message.USER_MENU);
            int selection = scanner.nextInt();
            switch (selection){
                case 1 -> trainingService.addNewTraining();
                case 2 -> trainingService.deleteTraining();
                case 3 ->
            }
        }
    }
}
