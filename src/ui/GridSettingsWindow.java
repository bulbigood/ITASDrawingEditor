package ui;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import core.Grid;
import core.Log;
import core.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static ui.MainWindow.*;

/**
 * Окно "параметры сетки"
 */
public class GridSettingsWindow extends JFrame {

    /**
     * Заголовок окна
     */
    private static String TITLE = "Параметры сетки";

    /**
     * Минимальная ширина окна
     */
    private static int MIN_WIDTH = 340;

    /**
     * Минимальная высота окна
     */
    private static int MIN_HEIGHT = 180;

    /**
     * Блокировка главного окна во время отображения текущего
     */
    private static boolean LOCK_MAIN_WINDOW = false;


    /**
     * Текстовое поле "шаг сетки"
     */
    private JTextField stepField;
    /**
     * Текстовое поле "Число разбиений в шаге"
     */
    private JTextField partitionCountField;
    /**
     * Флажок "Выравнивание по сетке"
     */
    private JCheckBox roundGridCheckBox;

    /**
     * Инициализирует окно
     */
    public GridSettingsWindow() {
        setTitle(TITLE);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.ICON_PATH));
        setResizable(false);
        setAlwaysOnTop(true);
        initComponents();
    }

    /**
     * Инициализирует компоненты окна
     */
    public void initComponents() {
        setLayout(new FormLayout(
                "2*(pref:grow)",
                "2*(default:grow)"));


        JPanel propertyPanel = new JPanel();
        propertyPanel.setLayout(new FormLayout(
                "right:80dlu, $lcgap, default",
                "3*(default, $lgap), default"));

        {
            JLabel stepLabel = new JLabel();
            stepLabel.setText("Шаг сетки: ");
            propertyPanel.add(stepLabel, CC.xy(1, 1));
            stepField = new JTextField();
            propertyPanel.add(stepField, CC.xy(3, 1));
        }

        {
            JLabel partitionCountLabel = new JLabel();
            partitionCountLabel.setText("Разбиений в шаге: ");
            propertyPanel.add(partitionCountLabel, CC.xy(1, 3));
            partitionCountField = new JTextField();
            propertyPanel.add(partitionCountField, CC.xy(3, 3));
        }

        {
            roundGridCheckBox = new JCheckBox();
            roundGridCheckBox.setText("Выравнивание по сетке");
            propertyPanel.add(roundGridCheckBox, CC.xy(3, 5));
        }


        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FormLayout(
                "10dlu, default:grow, $lcgap, 10dlu, $lcgap, default:grow, $lcgap, 10dlu",
                "default"));

        {
            JButton apply = new JButton();
            apply.setText("ОК");
            apply.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    apply();
                }
            });
            buttonsPanel.add(apply, CC.xy(2, 1));
        }

        {
            final JButton cancel = new JButton();
            cancel.setText("Отмена");
            cancel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    cancel();
                }
            });
            buttonsPanel.add(cancel, CC.xy(6, 1));
        }


        add(propertyPanel, CC.xywh(1, 1, 2, 1));
        add(buttonsPanel, CC.xywh(1, 2, 2, 1));
    }

    /**
     * Приминить изменения
     */
    private void apply() {
        try {
            if (Integer.parseInt(stepField.getText()) < 0 || Integer.parseInt(partitionCountField.getText()) < 0)
                return;
            Grid.stepSize = Integer.parseInt(stepField.getText());
            Grid.stepNumber = Integer.parseInt(partitionCountField.getText());
        } catch (IllegalArgumentException e1) {
            Log.add("Ошибка обработки параметров сетки!");
            return;
        }

        Settings.ROUND_TO_GRID = roundGridCheckBox.isSelected();
        getToolBar().updateGridButton();
        getDrawingArea().repaint();
        setVisible(false);
    }

    /**
     * Не применять изменения
     */
    private void cancel() {
        setVisible(false);
    }

    /**
     * Показывает или скрывает текущее окно
     *
     * @param b true - показывает, false - скрывает
     */
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            if (LOCK_MAIN_WINDOW)
                getMainWindow().setEnabled(false);
            updateValues();
        } else if (!b) {
            getMainWindow().setEnabled(true);
        }
    }

    /**
     * Обновляет значения окна.
     * Например шаг, привязку к сетке и т.д.
     */
    private void updateValues() {
        stepField.setText(Integer.toString(Grid.stepSize));
        partitionCountField.setText(Integer.toString(Grid.stepNumber));
        if (Settings.ROUND_TO_GRID)
            roundGridCheckBox.setSelected(true);
        else
            roundGridCheckBox.setSelected(false);
    }
}
