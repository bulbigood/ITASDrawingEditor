package core;

import graphic_objects.GraphicObject;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static core.DrawingArea.*;
import static ui.MainWindow.getDrawingArea;
import static ui.MainWindow.getToolBar;

/**
 * События клавиатуры области черчения
 */
public class KeyListener extends KeyAdapter {

    /**
     * Нажата ли клавиша ctrl
     */
    private static boolean ctrlDown = false;

    /**
     * Нажата ли клавиша shift
     */
    private static boolean shiftDown = false;

    /**
     * Возвращает, является ли клавиша ctrl нажатой
     *
     * @return true - нажата, false - не нажата
     */
    public static boolean isControlDown() {
        return ctrlDown;
    }

    /**
     * Возвращает, является ли клавиша shift нажатой
     *
     * @return true - нажата, false - не нажата
     */
    public static boolean isShiftDown() {
        return shiftDown;
    }

    public static void resetShiftCtrl() {
        ctrlDown = false;
        shiftDown = false;
    }

    /**
     * Вызывается по нажатию клавиши
     *
     * @param e событие
     */
    public void keyPressed(KeyEvent e) {
        ctrlDown = e.isControlDown();
        shiftDown = e.isShiftDown();
    }

    /**
     * Вызывается по отпусканию клавиши
     *
     * @param e событие
     */
    public void keyReleased(KeyEvent e) {
        ctrlDown = e.isControlDown();
        shiftDown = e.isShiftDown();

        GraphicObject graphicObject = GraphicObject.getBuildingGraphicObject();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (graphicObject != null && graphicObject.canBeFinished()) {
                    Log.add("Создано с клавиатуры: " + graphicObject);
                    GraphicObject.finishBuildingFigure();
                    getSelection().onSelectionChanged();
                    getStates().fixState();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                if (graphicObject != null) {
                    Log.add("Отменено с клавиатуры: " + graphicObject);
                    GraphicObject.finishBuildingFigure();
                    getData().removeFigure(graphicObject);
                } else {
                    Log.add("Выделение снято клавишей ESC");
                    getSelection().unselectAll();
                }
                break;
        }

        if (e.getKeyCode() == KeyEvent.VK_G && e.isControlDown()) {
            getToolBar().clickGridButton();
        }
        if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
            Settings.ROUND_TO_GRID = !Settings.ROUND_TO_GRID;
            getToolBar().updateGridButton();
        }
        if (e.getKeyCode() == KeyEvent.VK_D && e.isControlDown()) {
            getToolBar().clickRoundButton();
        }
        if (e.getKeyCode() == KeyEvent.VK_E && e.isControlDown()) {
            getToolBar().clickPointsVisibleButton();
        }
        if (e.getKeyCode() == KeyEvent.VK_T && e.isControlDown()) {
            getToolBar().clickTransformButton();
        }
        if (e.getKeyCode() == KeyEvent.VK_1) {
            setToolMode(ToolMode.CURSOR);
        }
        if (e.getKeyCode() == KeyEvent.VK_2) {
            setToolMode(ToolMode.DRAW_SEGMENT);
        }
        if (e.getKeyCode() == KeyEvent.VK_3) {
            setToolMode(ToolMode.DRAW_CYCLE_SEGMENT);
        }
        if (e.getKeyCode() == KeyEvent.VK_4) {
            setToolMode(ToolMode.DRAW_CIRCLE);
        }
        if (e.getKeyCode() == KeyEvent.VK_5) {
            setToolMode(ToolMode.DRAW_ARC);
        }
        if (e.getKeyCode() == KeyEvent.VK_6) {
            setToolMode(ToolMode.RULER);
        }
        if (e.getKeyCode() == KeyEvent.VK_ADD) {
            if (e.isAltDown()) {
                if (Grid.stepNumber < Grid.stepSize)
                    Grid.stepNumber++;
            } else {
                Grid.stepSize++;
            }
            getDrawingArea().repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
            if (e.isAltDown()) {
                if (Grid.stepNumber > 1)
                    Grid.stepNumber--;
            } else {
                if (Grid.stepSize > 1)
                    Grid.stepSize--;
            }
            getDrawingArea().repaint();
        }
    }
}
