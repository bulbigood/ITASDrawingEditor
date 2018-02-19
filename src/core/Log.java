package core;

import java.util.ArrayList;

/**
 * Журнал событий
 */
public class Log {

    /**
     * Список сообщений
     */
    private static ArrayList<String> messages = new ArrayList<>();

    /**
     * Логирует сообщение
     *
     * @param message сообщение
     */
    public static void add(String message) {
        messages.add(message);
        System.out.println(message);
    }

    /**
     * Логирует сообщение
     *
     * @param message сообщение
     */
    public static void add(int message) {
        add(Integer.toString(message));
    }

    /**
     * Логирует сообщение
     *
     * @param message сообщение
     */
    public static void add(double message) {
        add(Double.toString(message));
    }

    /**
     * Логирует объект
     *
     * @param obj объект
     */
    public static void add(Object obj) {
        add(obj.toString());
    }
}
