package ui;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import core.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.FileSystem;

import static core.DrawingArea.*;
import static ui.MainWindow.getDrawingArea;

/**
 * Панель функциональных кнопок
 */

public class ToolBar extends JPanel {
    /**
     * Кнопка "Новый чертеж"
     */
    ToolButton newButton;
    /**
     * Кнопка "Открыть"
     */
    ToolButton openButton;
    /**
     * Кнопка "Сохранить"
     */
    ToolButton saveButton;
    /**
     * Кнопка "Отмена"
     */
    private ToolButton undoButton;
    /**
     * Кнопка "Повтор"
     */
    private ToolButton redoButton;
    /**
     * Кнопка "Вырезать"
     */
    private ToolButton cutButton;
    /**
     * Кнопка "Копировать"
     */
    private ToolButton copyButton;
    /**
     * Кнопка "Вставить"
     */
    private ToolButton pasteButton;
    /**
     * Кнопка "Удалить"
     */
    private ToolButton removeButton;
    /**
     * Кнопка-переключатель "Привязки"
     */
    private ToolToggleButton roundButton;
    /**
     * Кнопка-переключатель "Сетка"
     */
    private ToolToggleButton gridButton;
    /**
     * Кнопка "Параметры сетки"
     */
    private ToolButton gridSettingsButton;
    /**
     * Кнопка "Отображать точки"
     */
    private ToolToggleButton pointsVisibleButton;
    /**
     * Кнопка "Перемещение по области черчения"
     */
    private ToolToggleButton transformButton;
    /**
     * Кнопка "Показать весь лист"
     */
    private ToolButton scaleReset;
    /**
     * Кнопка "Увеличить масштаб"
     */
    private ToolButton scaleInButton;
    /**
     * Кнопка "Уменьшить масштаб"
     */
    private ToolButton scaleOutButton;
    /**
     * Текстовое поле "Масштаб"
     */
    private JTextField scaleField;

    private ToolToggleButton playMorphing;
    private ToolToggleButton stopMorphing;


    /**
     * Окно параметров сетки
     */
    private GridSettingsWindow gridSettingsWindow = new GridSettingsWindow();

    /**
     * Инициализирует строку инструментов
     */
    public ToolBar() {
        setBorder(BorderFactory.createEtchedBorder());
        setLayout(new FormLayout(
                "20*(default)",
                "default"));
        initFilePanel();
        initStatesPanel();
        initClipboardStates();
        initScalePanel();
        initGraphicPanel();
        initGridPanel();
        initTransformPanel();
        initRepaintPanel();
        initMorphingControlPanel();
    }

    private void initMorphingControlPanel(){
        playMorphing = new ToolToggleButton("morph_play.png", "Запустить/приостановить режим морфинга", false,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if(playMorphing.isSelected()){
                            if(!DrawingArea.isMorphingMode())
                                stopMorphing.doClick();
                            playMorphing.editIcon("morph_pause.png");
                            getDrawingArea().runMorphing();
                            getDrawingArea().pauseMorphing(false);
                        } else {
                            playMorphing.editIcon("morph_play.png");
                            getDrawingArea().pauseMorphing(true);
                        }
                    }
                });
        stopMorphing = new ToolToggleButton("morph_stop.png", "Остановить режим морфинга", true,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        getDrawingArea().stopMorphing();
                        if(playMorphing.isSelected()) {
                            playMorphing.doClick();
                            playMorphing.editIcon("morph_play.png");
                        }
                    }
                });

        JPanel morphingPanel = new JPanel();
        morphingPanel.add(playMorphing);
        morphingPanel.add(stopMorphing);
        add(morphingPanel, CC.xy(9, 1));
    }

    /**
     * Инициализирует файловую панель
     */
    private void initFilePanel() {
        JPanel filePanel = new JPanel();
        newButton = new ToolButton("new.png", "Новый чертеж (Ctrl+N)", true,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        FileData.newFile();
                    }
                });
        openButton = new ToolButton("open.png", "Открыть чертеж (Ctrl+O)", true,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        FileData.openFileChooser();
                    }
                });
        saveButton = new ToolButton("save.png", "Сохранить чертеж (Ctrl+S)", true,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        FileData.saveFileChooser();
                    }
                });

        filePanel.add(newButton);
        filePanel.add(openButton);
        filePanel.add(saveButton);
        add(filePanel, CC.xy(1, 1));
    }

    /**
     * Инициализирует панель состояний
     */
    private void initStatesPanel() {
        JPanel statesPanel = new JPanel();
        undoButton = new ToolButton("undo.png", "Отменить (Ctrl+Z)", false,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        getStates().undo();
                        getDrawingArea().repaint();
                    }
                });
        redoButton = new ToolButton("redo.png", "Повторить (Ctrl+Shift+Z)", false,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        getStates().redo();
                        getDrawingArea().repaint();
                    }
                });

        statesPanel.add(undoButton);
        statesPanel.add(redoButton);
        add(statesPanel, CC.xy(2, 1));
    }

    /**
     * Инициализирует панель буффера-обмены
     */
    private void initClipboardStates() {
        JPanel clipboardStates = new JPanel();
        cutButton = new ToolButton("cut.png", "Вырезать (Ctrl+X)", false,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        Clipboard.cut();
                        getDrawingArea().repaint();
                    }
                });
        copyButton = new ToolButton("copy.png", "Копировать (Ctrl+C)", false,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        Clipboard.copy();
                        getDrawingArea().repaint();
                    }
                });
        pasteButton = new ToolButton("paste.gif", "Вставить (Ctrl+V)", false,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        Clipboard.paste();
                    }
                });
        removeButton = new ToolButton("remove.gif", "Удалить (Delete)", false,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        getSelection().removeSelected();
                    }
                });
        clipboardStates.add(cutButton);
        clipboardStates.add(copyButton);
        clipboardStates.add(pasteButton);
        clipboardStates.add(removeButton);
        add(clipboardStates, CC.xy(3, 1));
    }

    /**
     * Инициализирует панель масштаба
     */
    private void initScalePanel() {
        JPanel scalePanel = new JPanel();
        scaleReset = new ToolButton("reset_scale.png", "Показать весь лист (Ctrl+NumPad0)", true,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        getScreen().initialize(getDrawingArea().getWidth(), getDrawingArea().getHeight());
                        getDrawingArea().repaint();
                    }
                });
        scaleInButton = new ToolButton("scale_plus.png", "Увеличить масштаб (Ctrl+NumPad+)", true,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        getScreen().addScaleToCenter(1);
                    }
                });
        scaleOutButton = new ToolButton("scale_minus.png", "Уменьшить масштаб (Ctrl+NumPad-)", true,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        getScreen().addScaleToCenter(-1);
                    }
                });
        scaleField = new ToolTextField("Масштаб", new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == 10) {
                    try {
                        Double scale = Double.parseDouble(scaleField.getText());
                        getScreen().setScale(scale * getScreen().getDefaultScale());
                    } catch (IllegalArgumentException e1) {
                        Log.add("Ошибка обработки масштаба поля!");
                    }
                    setFieldScale(getScreen().getScale());
                }
            }
        });
        scalePanel.add(scaleReset);
        scalePanel.add(scaleInButton);
        scalePanel.add(scaleOutButton);
        scalePanel.add(scaleField);
        add(scalePanel, CC.xy(4, 1));
    }

    /**
     * Инициализирует панель привязок и точек
     */
    private void initGraphicPanel() {
        JPanel graphicPanel = new JPanel();
        roundButton = new ToolToggleButton("round.png", "Привязки (Ctrl+D)", Settings.ROUND_TO_FIGURES_POINTS,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        Settings.ROUND_TO_FIGURES_POINTS = roundButton.isSelected();
                        getDrawingArea().repaint();
                    }
                });
        pointsVisibleButton = new ToolToggleButton("points.png", "Точки (Ctrl+E)", Settings.DRAW_POINTS,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        Settings.DRAW_POINTS = pointsVisibleButton.isSelected();
                        getDrawingArea().repaint();
                    }
                });
        graphicPanel.add(roundButton);
        graphicPanel.add(pointsVisibleButton);
        add(graphicPanel, CC.xy(6, 1));
    }

    /**
     * Инициализирует панель сетки
     */
    private void initGridPanel() {
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new FormLayout(
                "2*(default)",
                "default"));
        gridButton = new ToolToggleButton(Settings.ROUND_TO_GRID ? "round_to_grid.png" : "grid.png", "Сетка (Ctrl+G, Ctrl+F)", Settings.DRAW_GRID,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        Settings.DRAW_GRID = gridButton.isSelected();
                        getDrawingArea().repaint();
                    }
                });
        gridPanel.add(gridButton, CC.xy(1, 1));

        gridSettingsButton = new ToolButton("grid_settings.png", "Параметры сетки (Alt+G)", true,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        gridSettingsWindow.setVisible(true);
                    }
                });
        gridSettingsButton.editSize(13, 26);
        gridPanel.add(gridSettingsButton, CC.xy(2, 1));
        add(gridPanel, CC.xy(5, 1));
    }

    private void initPointsPanel() {
    }

    /**
     * Инициализирует панель перемещения
     */
    private void initTransformPanel() {
        JPanel transformPanel = new JPanel();
        transformButton = new ToolToggleButton("transform.png", "Режим перемещения (Ctrl+T)", false, new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        transformPanel.add(transformButton);
        add(transformPanel, CC.xy(7, 1));
    }

    /**
     * Инициализирует панель перерисовки
     */
    private void initRepaintPanel() {
        JPanel repaintPanel = new JPanel();
        JButton repaintButton = new ToolButton("repaint.png", "Перестроить (Ctrl+R)", true,
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        getDrawingArea().repaint();
                    }
                });
        repaintPanel.add(repaintButton);
        add(repaintPanel, CC.xy(8, 1));
    }

    /**
     * Возвращает, включен ли режим перемещение по области черчения
     *
     * @return true - включен, false - выключен
     */
    public boolean isSelectedTransform() {
        return transformButton.isSelected();
    }

    /**
     * Устанавливает значение поля масштаба
     *
     * @param scale масштаб
     */
    public void setFieldScale(double scale) {
        scaleField.setText(String.format("%.3f", scale / getScreen().getDefaultScale()).replace(',', '.'));
    }

    /**
     * Активирует/деактивирует кнопку "Отмена"
     *
     * @param b true - активировать, false - деактивировать
     */
    public void setUndoEnabled(boolean b) {
        undoButton.setEnabled(b);
    }

    /**
     * Активирует/деактивирует кнопку "Повтор"
     *
     * @param b true - активировать, false - деактивировать
     */
    public void setRedoEnabled(boolean b) {
        redoButton.setEnabled(b);
    }

    /**
     * Активирует/деактивирует кнопку "Вырезать"
     *
     * @param b true - активировать, false - деактивировать
     */
    public void setCutEnabled(boolean b) {
        cutButton.setEnabled(b);
    }

    /**
     * Активирует/деактивирует кнопку "Копировать"
     *
     * @param b true - активировать, false - деактивировать
     */
    public void setCopyEnabled(boolean b) {
        copyButton.setEnabled(b);
    }

    /**
     * Активирует/деактивирует кнопку "удалить"
     *
     * @param b true - активировать, false - деактивировать
     */
    public void setPasteEnabled(boolean b) {
        pasteButton.setEnabled(b);
    }

    /**
     * Активирует/деактивирует кнопку "удалить"
     *
     * @param b true - активировать, false - деактивировать
     */
    public void setRemoveEnabled(boolean b) {
        removeButton.setEnabled(b);
    }

    /**
     * Эмулирует нажатие кнопки "Сетка"
     */
    public void clickGridButton() {
        gridButton.doClick();
        Settings.DRAW_GRID = gridButton.isSelected();
        getDrawingArea().repaint();
    }

    /**
     * Эмулирует нажатие кнопки "Привязки"
     */
    public void clickRoundButton() {
        roundButton.doClick();
        Settings.ROUND_TO_FIGURES_POINTS = roundButton.isSelected();
        getDrawingArea().repaint();
    }

    /**
     * Эмулирует нажатие кнопки "Точки фигур"
     */
    public void clickPointsVisibleButton() {
        pointsVisibleButton.doClick();
        Settings.DRAW_POINTS = pointsVisibleButton.isSelected();
        getDrawingArea().repaint();
    }

    /**
     * Эмулирует нажатие кнопки "Перемещения по облости"
     */
    public void clickTransformButton() {
        transformButton.doClick();
        Settings.DRAW_POINTS = pointsVisibleButton.isSelected();
        getDrawingArea().repaint();
    }

    /**
     * Обновляет иконку у кнопки "Сетка"
     */
    public void updateGridButton() {
        gridButton.editIcon(Settings.ROUND_TO_GRID ? "round_to_grid.png" : "grid.png");
    }

    /**
     * Возвращает окно параметров сетки
     *
     * @return окно параметров сетки
     */
    public GridSettingsWindow getGridSettingsWindow() {
        return gridSettingsWindow;
    }

    /**
     * Кнопка строки инструментов
     */
    class ToolButton extends JButton {
        /**
         * Ширина кнопки
         */
        private final int width = 26;
        /**
         * Высота кнопки
         */
        private final int height = 26;

        /**
         * Инициализирует кнопку
         *
         * @param icon        название иконки(включая расширение)
         * @param toolTipText текст всплывающей подсказки
         * @param enable      активна ли кнопка
         * @param listener    слушатель
         */
        public ToolButton(String icon, String toolTipText, boolean enable, MouseAdapter listener) {
            super(new ImageIcon(FileSystem.class.getResource("/res/" + icon)));
            setToolTipText(toolTipText);
            setEnabled(enable);
            addMouseListener(listener);
            Dimension d = new Dimension(width, height);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
        }

        /**
         * Изменяет размер
         *
         * @param w ширина
         * @param h высота
         */
        public void editSize(int w, int h) {
            Dimension d = new Dimension(w, h);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
        }
    }

    /**
     * Кнопка-переключатель строки инструментов
     */
    class ToolToggleButton extends JToggleButton {
        /**
         * Ширина кнопки
         */
        private final int width = 26;
        /**
         * Высота кнопки
         */
        private final int height = 26;

        /**
         * Инициализирует кнопку
         *
         * @param icon        название иконки(включая расширение)
         * @param toolTipText текст всплывающей подсказки
         * @param selected    включена ли кнопка
         * @param listener    слушатель
         */
        public ToolToggleButton(String icon, String toolTipText, boolean selected, MouseAdapter listener) {
            super(new ImageIcon(FileSystem.class.getResource("/res/" + icon)));
            setToolTipText(toolTipText);
            setSelected(selected);
            addMouseListener(listener);
            Dimension d = new Dimension(width, height);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
        }

        /**
         * Изменяет иконку
         *
         * @param icon название иконки
         */
        public void editIcon(String icon) {
            setIcon(new ImageIcon(FileSystem.class.getResource("/res/" + icon)));
        }
    }

    /**
     * Текстовое поле строки инструментов
     */
    class ToolTextField extends JTextField {
        /**
         * Ширина текстового поля
         */
        private final int width = 65;
        /**
         * Высота текстового поля
         */
        private final int height = 26;

        /**
         * Инициализирует текстовое поле
         *
         * @param toolTipText текст всплывающей подсказки
         * @param listener    слушатель
         */
        public ToolTextField(String toolTipText, KeyAdapter listener) {
            setToolTipText(toolTipText);
            addKeyListener(listener);
            Dimension d = new Dimension(width, height);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
        }
    }
}
