package ui;

import javax.swing.*;
import java.awt.*;

import static ui.MainWindow.getMainWindow;

/**
 * Окно горячих клавиш
 */
public class HotKeysWindow extends JFrame {
    /**
     * Заголовок окна
     */
    private static String TITLE = "Горячие клавиши";

    /**
     * Минимальная ширина окна
     */
    private static int MIN_WIDTH = 800;

    /**
     * Минимальная высота окна
     */
    private static int MIN_HEIGHT = 600;

    /**
     * Блокировка главного окна во время отображения текущего
     */
    private static boolean LOCK_MAIN_WINDOW = false;

    /**
     * Инициализирует окно
     */
    public HotKeysWindow() {
        setTitle(TITLE);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.ICON_PATH));
        setLocationRelativeTo(null);
        initComponents();
    }

    /**
     * Инициализирует компоненты
     */
    public void initComponents() {
        Object[] headers = {"Последовательность", "Описание"};
        Object[][] data = {
                {"Ctrl+N", "Новый чертеж"},
                {"Ctrl+O", "Открыть чертеж"},
                {"Ctrl+S", "Сохранить чертеж"},
                {"Alt+F4", "Завершить работу программы"},
                {"Ctrl+Z", "Отменить совершенное действие"},
                {"Ctrl+Shift+Z", "Повторить совершенное действие"},
                {"Ctrl+X", "Вырезать геометрические фигуры"},
                {"Ctrl+C", "Копировать геометрические фигуры"},
                {"Ctrl+V", "Вставить геометрические фигуры"},
                {"Ctrl+A", "Выделить все геометрические фигуры"},
                {"Delete", "Удалить геометрические фигуры"},
                {"Ctrl+NumPad0", "Показать весь лист"},
                {"Ctrl+NumPad+", "Увеличить масштаб поля черчения"},
                {"Ctrl+NumPad-", "Уменьшить масштаб поля черчения"},
                {"NumPad+", "Увеличить шаг сетки"},
                {"NumPad-", "Уменьшить шаг сетки"},
                {"Alt+NumPad+", "Увеличить количество разбиений в шаге сетки"},
                {"Alt+NumPad-", "Уменьшить количество разбиений в шаге сетки"},
                {"Ctrl+G", "Включить/отключить сетку"},
                {"Ctrl+F", "Включить/отключить выравнивание по сетке"},
                {"Alt+G", "Открыть окно параметров сетки"},
                {"Ctrl+D", "Включить/отключить привзку к точкам геометрических фигур"},
                {"Ctrl+E", "Включить/отключить отображение всех точек геометрических фигур"},
                {"Ctrl+T", "Включить/отключить режим перемещения по области черчения"},
                {"Ctrl+R", "Перестроить область черчения"},
                {"1-7", "Активировать инструмент (в порядке их расположения на панели инструментов)"}
        };
        JTable table = new JTable(data, headers);
        table.setEnabled(false);
        table.setRowHeight(45);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        add(scrollPane);
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
