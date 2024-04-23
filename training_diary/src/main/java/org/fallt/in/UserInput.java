package org.fallt.in;

import java.util.Scanner;

/**
 * Класс предназначен для получения вводимых пользователем данных
 */
public class UserInput {

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Получение от пользователя имени
     * @return Строковое представление введенных пользователем данных
     */
    public String inputName() {
        System.out.println("Введите ваше имя");
        return scanner.nextLine();
    }

    /**
     * Получение от пользователя пароля
     * @return Строковое представление введенных пользователем данных
     */
    public String inputPassword() {
        System.out.println("Введите ваш пароль");
        return scanner.nextLine();
    }

    /**
     * Получение от пользователя даты
     * @return Строковое представление введенных пользователем данных
     */
    public String inputDate() {
        System.out.println("Введите дату в формате дд/мм/гггг");
        return scanner.nextLine();
    }

    /**
     * Получение от пользователя данных о типе тренировки
     * @return Строковое представление введенных пользователем данных
     */
    public String inputTrainingType() {
        System.out.println("Введите тип тренировки");
        return scanner.nextLine();
    }

    /**
     * Метод используется для получения от пользователя номера в соответствующем меню
     * @return Строковое представление введенных пользователем данных
     */
    public String getUserInput() {
        return scanner.nextLine();
    }

    /**
     * Метод получения от пользователя данных общего вида
     * @param text Выводимое пользователю сообщение
     * @return Строковое представление введенных пользователем данных
     */
    public String getUserInput(String text) {
        System.out.println(text);
        return scanner.nextLine();
    }

}
