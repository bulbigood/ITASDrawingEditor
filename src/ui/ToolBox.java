package ui;

import core.DrawingArea;
import core.Morpher;
import core.ToolMode;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.FileSystem;
import java.util.ArrayList;

import static ui.MainWindow.getDrawingArea;
import static ui.MainWindow.getFractalTreePanel;
import static ui.MainWindow.getMatrixPanel;

/**
 * Панель инструментов
 */
public class ToolBox extends JPanel {

    /**
     * Контейнер панели инструментов
     */
    private static JToolBar toolBar = new JToolBar();

    /**
     * Кнопка инструмента "Курсор"
     */
    private ToolButton cursorButton = new ToolButton("cursor.png", "Курсор", ToolMode.CURSOR, null);
    /**
     * Кнопка инструмента "Отрезок"
     */
    private ToolButton lineButton = new ToolButton("segment.png", "Отрезок", ToolMode.DRAW_SEGMENT, null);
    /**
     * Кнопка инструмента "Непрерывный отрезок"
     */
    private ToolButton segmentButton = new ToolButton("cycle_segment.png", "Беспрерывный отрезок", ToolMode.DRAW_CYCLE_SEGMENT, null);
    /**
     * Кнопка инструмента "Окружность"
     */
    private ToolButton circleButton = new ToolButton("circle.png", "Окружность", ToolMode.DRAW_CIRCLE, null);
    /**
     * Кнопка инструмента "Дуга"
     */
    private ToolButton arcButton = new ToolButton("arc.png", "Дуга", ToolMode.DRAW_ARC, null);
    /**
     * Кнопка инструмента "Фрактальное дерево"
     */
    private ToolButton treeButton = new ToolButton("fractal.png", "Фрактальное дерево", ToolMode.DRAW_TREE, MainWindow.getFractalTreePanel());
    /**
     * Кнопка инструмента "Линейка"
     */
    private ToolButton rulerButton = new ToolButton("line_meter.png", "Линейка", ToolMode.RULER, null);
    /**
     * Кнопка "Группировка"
     */
    private ToolButton groupButton = new ToolButton("group.png", "Группировка", null, null);
    /**
     * Кнопка "Матричное преобразование"
     */
    private ToolButton matrixButton = new ToolButton("matrix.png", "Матричное преобразование", null, null);
    /**
     * Кнопка "Морфинг"
     */
    private ThreeStatesJButton morphingButton = new ThreeStatesJButton("morphing.png", "Морфинг");

    /**
     * Список всех кнопок
     */
    private ArrayList<ToolButton> buttons = new ArrayList<>();

    /**
     * Инициализация панели
     */
    public ToolBox() {

        setBorder(BorderFactory.createEtchedBorder());

        toolBar.setOrientation(SwingConstants.VERTICAL);
        toolBar.setFloatable(false);

        buttons.add(cursorButton);
        buttons.add(lineButton);
        buttons.add(segmentButton);
        buttons.add(circleButton);
        buttons.add(arcButton);
        buttons.add(treeButton);

        buttons.add(rulerButton);

        cursorButton.needSeparator();
        treeButton.needSeparator();

        for (ToolButton b : buttons) {
            toolBar.add(b);
            if (b.isNeedSeparator())
                toolBar.addSeparator(new Dimension(0, 36));
            b.addActionListener(new ModeListener(b.getToolMode()));
        }
        onModeChange(DrawingArea.getToolMode());

        toolBar.addSeparator(new Dimension(0, 36));
        matrixButton.addActionListener(new MatrixButtonListener());
        toolBar.add(matrixButton);
        toolBar.addSeparator(new Dimension(0, 36));
        groupButton.addActionListener(new GroupButtonListener());
        morphingButton.addActionListener(new MorphingButtonListener());
        toolBar.add(groupButton);
        toolBar.add(morphingButton);
        add(toolBar);
    }

    /**
     * Вызывется, когда изменяется режим
     *
     * @param m режим
     */
    public void onModeChange(ToolMode m) {
        for (ToolButton b : buttons) {
            b.unSelect();
            if (b.getToolMode() == m)
                b.select();
        }
    }

    public void selectGrouping(boolean group){
        if(group)
            groupButton.select();
        else
            groupButton.unSelect();
}

    public void selectMorphing(int i){
        switch(i){
            case 1:
                if(morphingButton.getState() != 2)
                    morphingButton.setState1();
                break;
            case 2:
                morphingButton.setState2();
                break;
            case 3:
                if(morphingButton.getState() != 2)
                    morphingButton.setState3();
                break;
        }
    }

    public void selectMatrix(boolean b){
        if(b) {
            matrixButton.select();
            getMatrixPanel().setVisible(true);
            getDrawingArea().enableDraggableOXY();
        }
        else {
            matrixButton.unSelect();
            getMatrixPanel().setVisible(false);
            getDrawingArea().unableDraggableOXY();
        }
    }

    /**
     * Слушатель кнопки группировки
     */
    class GroupButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(groupButton.isSelected())
                DrawingArea.getSelection().ungroupObjects();
            else
                DrawingArea.getSelection().groupObjects();
        }
    }

    /**
     * Слушатель кнопки морфинга
     */
    class MorphingButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            switch(morphingButton.getState()){
                case 1:
                    if(DrawingArea.getSelection().getCountSelected() > 0){
                        DrawingArea.getSelection().groupObjects();
                        getDrawingArea().getMorpher().setStartingPairGroup(DrawingArea.getSelection().getSelectedGroup());
                        morphingButton.setState2();
                    }
                    break;
                case 2:
                    if(DrawingArea.getSelection().getCountSelected() > 0){
                        DrawingArea.getSelection().groupObjects();
                        getDrawingArea().getMorpher().setEndingPairGroup(DrawingArea.getSelection().getSelectedGroup());
                        if(getDrawingArea().getMorpher().finalizeMorphingPair())
                            morphingButton.setState3();
                    } else {
                        getDrawingArea().getMorpher().removeCreatingMorphingPair();
                        morphingButton.setState1();
                    }
                    break;
                case 3:
                    getDrawingArea().getMorpher().removeMorphingPair(DrawingArea.getSelection().getSelectedGroup());
                    morphingButton.setState1();
                    break;
            }
        }
    }

    class MatrixButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(matrixButton.isSelected())
                selectMatrix(false);
            else
                selectMatrix(true);
        }
    }

    /**
     * Слушатель кнопок режима рисования
     */
    class ModeListener implements ActionListener {
        final ToolMode toolMode;

        public ModeListener(ToolMode toolMode) {
            this.toolMode = toolMode;
        }

        public void actionPerformed(ActionEvent e) {
            DrawingArea.setToolMode(toolMode);
        }
    }

    /**
     * Кнопка для инструментов
     */
    class ToolButton extends JButton {
        /**
         * Ширина кнопки
         */
        private final int width = 36;

        /**
         * Высота кнопки
         */
        private final int height = 36;

        /**
         * Цвет активной кнопки
         */
        private Color selectButtonColor = new ColorUIResource(168, 228, 255);

        /**
         * Цвет неактивной кнопки
         */
        private Color unSelectButtonColor = new ColorUIResource(238, 238, 238);

        /**
         * Необходимость разделителя после кнопки
         */
        private boolean needSeparator = false;

        private boolean selected = false;

        /**
         * Режим кнопки
         */
        private ToolMode toolMode = null;

        private JPanel settingsPanel = null;

        /**
         * Инициализирует кнопку
         *
         * @param icon        название иконки кнопки (включая расширение)
         * @param toolTipText текст всплывающей подсказки
         * @param m           режим для кнопки
         */
        public ToolButton(String icon, String toolTipText, ToolMode m, JPanel settings) {
            super(new ImageIcon(FileSystem.class.getResource("/res/" + icon)));
            setToolTipText(toolTipText);
            toolMode = m;
            settingsPanel = settings;

            Dimension d = new Dimension(width, height);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
        }

        /**
         * Выделяет кнопку
         */
        public void select() {
            selected = true;
            setBackground(selectButtonColor);
            if(settingsPanel != null)
                settingsPanel.setVisible(true);
        }

        /**
         * Отменяет выделение кнопки
         */
        public void unSelect() {
            selected = false;
            setBackground(unSelectButtonColor);
            if(settingsPanel != null)
                settingsPanel.setVisible(false);
        }

        public boolean isSelected(){
            return selected;
        }

        /**
         * Установливает необходимость разделителя после кнопки
         */
        public void needSeparator() {
            needSeparator = true;
        }

        /**
         * Возвращает, необходим ли разделитель после кнопки
         *
         * @return true - нужен, false - не нужен
         */
        public boolean isNeedSeparator() {
            return needSeparator;
        }

        /**
         * Устанавливает режим для кнопки
         *
         * @param m режим
         */
        public void setToolMode(ToolMode m) {
            toolMode = m;
        }

        /**
         * Получает текущий режим кнопки
         *
         * @return режим
         */
        public ToolMode getToolMode() {
            return toolMode;
        }
    }
}
