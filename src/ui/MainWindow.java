package ui;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import core.DrawingArea;
import core.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Главное окно приложения
 */

public class MainWindow extends JFrame {

    /**
     * Заголовок окна
     */
    public static String TITLE = "ITAS Drawing Editor";

    /**
     * Версия приложения
     */
    public final static String VERSION = "1.0.0";

    /**
     * Путь к иконке
     */
    public final static String ICON_PATH = "src/res/icon.png";

    /**
     * Ширина окна
     */
    private static int WIDTH = 800;

    /**
     * Высота окна
     */
    private static int HEIGHT = 650;

    /**
     * Минимальная ширина окна
     */
    private static int MIN_WIDTH = 400;

    /**
     * Минимальная высота окна
     */
    private static int MIN_HEIGHT = 300;

    /**
     * Режим отладки
     */
    private static boolean DEBUG_MODE = false;

    /**
     * Текст в заголовке при активном режиме отладки
     */
    private static String DEBUG_MODE_TEXT = "режим отладки";

    private static MainWindow mainWindow;
    private static DrawingArea drawingArea;
    private static ToolBox toolBox;
    private static PropertyBox propertyBox;
    private static MenuBar menuBar;
    private static ToolBar toolBar;
    private static JPanel matrixPanel;
    private static JPanel treePanel;

    /**
     * Запускает программу
     *
     * @param args масив переменных
     */
    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.toUpperCase().equals("DEBUG") || arg.toUpperCase().equals("DEBUG_MODE"))
                setDebugMode(true);
            if (arg.toUpperCase().equals("NOANTIALIASING") || arg.toUpperCase().equals("NO_ANTIALIASING"))
                Settings.ANTIALIASING = false;
        }
        mainWindow = new MainWindow();
    }

    /**
     * Инициализирует окно
     */
    private MainWindow() {
        setTitle(TITLE);
        setBounds(0, 0, WIDTH, HEIGHT);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setLocationRelativeTo(null);
//        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(Toolkit.getDefaultToolkit().getImage(ICON_PATH));
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        EventHandler eventHandler = new EventHandler();
        addWindowListener(eventHandler);
        initComponents();
        setVisible(true);
    }

    /**
     * Инициализирует компоненты
     */
    private void initComponents() {
        matrixPanel = new MatrixPanel();
        treePanel = new FractalTreePanel();
        toolBox = new ToolBox();
        menuBar = new MenuBar();
        toolBar = new ToolBar();
        drawingArea = new DrawingArea();
        propertyBox = new PropertyBox();


        setJMenuBar(menuBar);

        matrixPanel.setVisible(false);
        treePanel.setVisible(false);

        JPanel panel = new JPanel();
        panel.setLayout(new FormLayout(
                "min, pref:grow",
                "min, fill:default:grow, fill:35px"));

        panel.add(toolBar, CC.xywh(1, 1, 2, 1));
        panel.add(toolBox, CC.xywh(1, 2, 1, 2));
        panel.add(drawingArea, CC.xy(2, 2));
        panel.add(propertyBox, CC.xy(2, 3));

        add(panel);

        final JPanel glass = (JPanel) getGlassPane();
        glass.setVisible(true);
        glass.setLayout(null);
        matrixPanel.setLocation(55, 430);
        treePanel.setLocation(55, 285);
        glass.add(matrixPanel);
        glass.add(treePanel);
    }

    /**
     * Устанавливает режим отладки
     *
     * @param b если true - включен, если false - выключен
     */
    private static void setDebugMode(boolean b) {
        if (b)
            TITLE += " - " + DEBUG_MODE_TEXT;
        else if (TITLE.contains(" - " + DEBUG_MODE_TEXT))
            TITLE = TITLE.substring(0, TITLE.indexOf(" - " + DEBUG_MODE_TEXT));
        DEBUG_MODE = b;
    }

    /**
     * Возвращает, включен ли режим отладки
     *
     * @return true - включен, false - выключен
     */
    public static boolean isActiveDebugMode() {
        return DEBUG_MODE;
    }

    /**
     * Возвращает область черчения
     *
     * @return область черчения
     */
    public static DrawingArea getDrawingArea() {
        return drawingArea;
    }

    /**
     * Возвращает панель инструментов
     *
     * @return панель инструментов
     */
    public static ToolBox getToolBox() {
        return toolBox;
    }

    /**
     * Возвращает панель свойств
     *
     * @return панель свойств
     */
    public static PropertyBox getPropertyBox() {
        return propertyBox;
    }

    /**
     * Возвращает строку инструментов
     *
     * @return строка инструментов
     */
    public static ToolBar getToolBar() {
        return toolBar;
    }

    /**
     * Возвращает строку меню
     *
     * @return строка меню
     */
    public static MenuBar getTopMenuBar() {
        return menuBar;
    }

    /**
     * Возвращает главное окно приложения
     *
     * @return главное окно приложения
     */
    public static MainWindow getMainWindow() {
        return mainWindow;
    }

    public static JPanel getMatrixPanel(){
        return matrixPanel;
    }

    public static JPanel getFractalTreePanel(){
        return treePanel;
    }

    /**
     * Обработчик событий окна
     */
    private class EventHandler implements WindowListener {
        @Override
        public void windowOpened(WindowEvent e) {

        }

        public void windowClosing(WindowEvent event) {
            Object[] options = {"Выйти", "Отмена"};
            int n = JOptionPane
                    .showOptionDialog(event.getComponent(), "Вы уверены, что хотите выйти? Все несохраненные данные будут потеряны.",
                            "Предупреждение", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, options,
                            options[0]);
            if (n == 0) {
                System.exit(0);
            }
        }

        @Override
        public void windowClosed(WindowEvent e) {
        }

        @Override
        public void windowIconified(WindowEvent e) {

        }

        @Override
        public void windowDeiconified(WindowEvent e) {

        }

        @Override
        public void windowActivated(WindowEvent e) {

        }

        @Override
        public void windowDeactivated(WindowEvent e) {

        }
    }
}
