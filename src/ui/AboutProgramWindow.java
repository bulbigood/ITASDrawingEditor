package ui;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;

import static ui.MainWindow.getMainWindow;

/**
 * Окно "о программе"
 */
public class AboutProgramWindow extends JFrame {

    /**
     * Заголовок окна
     */
    private static String TITLE = "О программе";

    /**
     * Минимальная ширина окна
     */
    private static int MIN_WIDTH = 400;

    /**
     * Минимальная высота окна
     */
    private static int MIN_HEIGHT = 250;

    /**
     * Блокировка главного окна во время отображения текущего
     */
    private static boolean LOCK_MAIN_WINDOW = false;

    /**
     * Инициализирует окно
     */
    public AboutProgramWindow() {
        setTitle(TITLE);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.ICON_PATH));
        setLocationRelativeTo(null);
        setResizable(false);
        setAlwaysOnTop(true);
        initComponents();
    }

    /**
     * Инициализирует компоненты
     */
    public void initComponents() {
        setLayout(new FormLayout(
                "1*(default)",
                "pref"));

        String text = "<html>" +
                "<center><h2>" + MainWindow.TITLE + "</h2></center>" +
                "<center><font face=’verdana’ size = 2>Версия " + MainWindow.VERSION + "</font></center>" +
                "<br><center><font face=’arial’ size = 3>Данное программное обеспечение выполнено в качестве курсовой" +
                " работы студентами учебной группы РИС-13-1б кафедры Информационных технологий и автоматизированных" +
                " систем Пермского национального исследовательского политехнического университета по специальности " +
                "\"Программная инженерия\", направлению \"Разработка программно-информационных систем\"." +
                "</font></center></html>";

        Font font = new Font(null, Font.PLAIN, 10);

        JLabel htmlLabel = new JLabel();
        htmlLabel.setText(text);
        htmlLabel.setFont(font);
        add(htmlLabel, CC.xy(1, 1));
    }

    /**
     * Показывает или скрывает текущее окно
     *
     * @param b true - показывает, false - скрывает
     */
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (LOCK_MAIN_WINDOW && b)
            getMainWindow().setEnabled(false);
        else if (!b) {
            getMainWindow().setEnabled(true);
        }
    }
}