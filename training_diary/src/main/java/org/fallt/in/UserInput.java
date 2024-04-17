package org.fallt.in;

import java.util.Scanner;

public class UserInput {

    private final Scanner scanner = new Scanner(System.in);

    public String inputName() {
        System.out.println("Введите ваше имя");
        return scanner.nextLine();
    }

    public String inputPassword() {
        System.out.println("Введите ваш пароль");
        return scanner.nextLine();
    }

    public String inputDate() {
        System.out.println("Введите дату в формате дд/мм/гггг");
        return scanner.nextLine();
    }

    public String inputTrainingType() {
        System.out.println("Введите тип тренировки");
        return scanner.nextLine();
    }

    public String getUserInput() {
        return scanner.nextLine();
    }

    public String getUserInput(String text) {
        System.out.println(text);
        return scanner.nextLine();
    }

}
