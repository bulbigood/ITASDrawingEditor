package core;

import graphic_objects.GraphicObject;
import graphic_objects.figures.Point2D;
import graphic_objects.figures.Segment;
import graphic_objects.meters.Meter;
import graphic_objects.meters.Ruler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collections;

import static core.DrawingArea.*;
import static ui.MainWindow.*;

/**
 * События мыши области черчения
 */
public class MouseListener implements java.awt.event.MouseListener, MouseMotionListener, MouseWheelListener {

    /**
     * Начальная точка при выделении
     * null - нет точки
     */
    Point startSelectPoint = null;
    /**
     * Начальная точка при перемещении
     * null - нет точки
     */
    Point startMovePoint = null;

    /**
     * Точка последнего местоположения мыши
     */
    Point lastPoint = new Point();

    private ArrayList<GraphicObject> graphicObjectList;
    private int pressedButton;
    private boolean mouseOnDrawingArea;
    private boolean pointIsEditing;
    private boolean meterCreated;

    /**
     * @param y Y координата панели DrawingArea
     * @return
     */
    private int nativeYAxis(int y) {
        return getDimensions().y - y;
    }

    public Point getLastPoint(){
        return lastPoint;
    }
    /////////////////////////////////////////////////////////////

    /**
     * Начинает перемещение по области черчения
     *
     * @param e событие мыши
     */
    private void startMoveDrawingArea(MouseEvent e) {
        Log.add("Начинаем перемещение по DrawingArea");
        startMovePoint = new Point(e.getX(), nativeYAxis(e.getY()));
    }

    /**
     * Перемещает область черчения
     *
     * @param e событие мыши
     */
    private void moveDrawingArea(MouseEvent e) {
//        Log.add("Перемещение DrawingArea");
        if (mouseOnDrawingArea) {
            int rx = startMovePoint.x - e.getX();
            int ry = startMovePoint.y - nativeYAxis(e.getY());
            getScreen().move(rx, ry);
            startMovePoint.setLocation(e.getX(), nativeYAxis(e.getY()));
        }
    }

    /////////////////////////////////////////////////////////

    /**
     * Начинает массовое выделение
     *
     * @param e событие мыши
     */
    private void startMassSelection(MouseEvent e) {
        Log.add("Начинаем выделение");
        getSelection().unselectAll();
        startSelectPoint = new Point(e.getX(), e.getY());
        startMovePoint = null;
        getSelection().onSelectionChanged();
    }

    /**
     * Массовое выделение
     *
     * @param e событие мыши
     */
    private void massSelection(MouseEvent e) {
//        Log.add("Изменилась область выделения");
        getSelection().selectArea(new Point(e.getX(), e.getY()), new Point(startSelectPoint));
    }

    /**
     * Завершает массовое выделение
     *
     * @param e событие мыши
     */
    private void endMassSelection(MouseEvent e) {
        Log.add("Выделение завершено");
        getSelection().setSelecting(false);
        getSelection().onSelectionChanged();
    }


////////////////////////////////////////////////////////////

    /**
     * Начинает строить фигуру
     */
    private void startBuildFigure(int x, int y) {
        Log.add("Строится новая фигура");
        getSelection().unselectAll();
        GraphicObject newGraphicObject = null;
        try {
            newGraphicObject = getToolMode().getDrawClass().newInstance();
            newGraphicObject.onCreate(x, y);
            GraphicObject.startBuildingFigure(newGraphicObject);
        } catch (InstantiationException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        getSelection().onSelectionChanged();
    }

    /**
     * Переходит к следующему этапу строящейся фигуры
     *
     * @param e событие мыши
     */
    private void buildFigureNextStage(MouseEvent e) {
        Log.add("К строящейся фигуре добавлена точка (" + e.getX() + "," + " " + e.getY() + ")");
        GraphicObject.getBuildingGraphicObject().nextStage(e.getX(), e.getY());
        getSelection().onSelectionChanged();
    }

    /**
     * Изменяет строящуюся фигуру
     *
     * @param e событие мыши
     */
    private void changeBuildingFigure(MouseEvent e) {
//        Log.add("Строящаяся точка обновлена");
        if (GraphicObject.getBuildingGraphicObject() instanceof Segment && KeyListener.isShiftDown()) {
            Point2D fixPoint = GraphicObject.getBuildingGraphicObject().getPoints().get(0);
            fixPoint = new Point2D(getScreen().XToScreen(fixPoint.getX()), getScreen().YToScreen(fixPoint.getY()));
            double absOnX = Math.abs(fixPoint.getX() - e.getX());
            double absOnY = Math.abs(fixPoint.getY() - e.getY());
            if (absOnX > absOnY) {
                GraphicObject.getBuildingGraphicObject().onChange(e.getX(), fixPoint.getY());
            } else {
                GraphicObject.getBuildingGraphicObject().onChange(fixPoint.getX(), e.getY());
            }
        } else {
            GraphicObject.getBuildingGraphicObject().onChange(e.getX(), e.getY());
        }
        getDrawingArea().repaint();
    }

    /**
     * Заврешает строить фигуру
     */
    private void endBuildFigure() {
        GraphicObject go = GraphicObject.getBuildingGraphicObject();
        Log.add("Создана фигура " + go);
        boolean b = GraphicObject.getBuildingGraphicObject() instanceof Ruler;
        GraphicObject.finishBuildingFigure();
        if (!b)
            getStates().fixState();
        if (getToolMode() == ToolMode.DRAW_CYCLE_SEGMENT) {
            startBuildFigure((int) getScreen().XToScreen(go.getPoint(1).getX()), (int) getScreen().YToScreen(go.getPoint(1).getY()));
        }
        getSelection().onSelectionChanged();
    }

    /////////////////////////////////////////////////////////////////

    private boolean figureIsMoved = false;

    /**
     * Начинает перемещение фигур
     *
     * @param e событие мыши
     */
    private void startMoveFigures(MouseEvent e) {
        Log.add("Начинаем перемещение фигур(-ы)");
        startMovePoint = new Point(e.getX(), nativeYAxis(e.getY()));
    }

    /**
     * Перемещает фигуры
     *
     * @param e событие мыши
     */
    private void moveFigures(MouseEvent e) {
        if (!startMovePoint.equals(new Point(e.getX(), nativeYAxis(e.getY())))) {
            getSelection().moveSelected(new Point(e.getX(), e.getY()));
            startMovePoint = new Point(e.getX(), nativeYAxis(e.getY()));

            //Рефреш привязанных измерителей
            Meter.refreshMeters(getSelection().getSelected());

            figureIsMoved = true;
        }
    }

    /**
     * Завершает перемещение фигур
     *
     * @param e событие мыши
     */
    private void endMoveFigures(MouseEvent e) {
        getSelection().onSelectionChanged();
        Log.add("Премещение завершено");
        if (getSelection().getCountSelected() > 0 && figureIsMoved) {
            getStates().fixState();
            figureIsMoved = false;
        }
    }

    ///////////////////////////////////////////////////////

    /**
     * Выделяет/снимает выделение с фигуры
     *
     * @param e событие мыши
     */
    private void selectOrUnSelectFigure(MouseEvent e) {
        if (KeyListener.isControlDown())
            //Это нужно для правильного выделения Ctrl'ом
            Collections.reverse(graphicObjectList);
        GraphicObject ins = graphicObjectList.get(0);
        if (!ins.isSelected()) {
            if (!KeyListener.isControlDown()) {
                getSelection().unselectAll();
            }
            if(ins.isGrouped())
                getSelection().selectGroup(ins);
            else
                getSelection().select(ins, true);

            Log.add("Выделено " + ins);
            getDrawingArea().repaint();
        } else {
            if (KeyListener.isControlDown()) {
                if(ins.isGrouped())
                    getSelection().unselectGroup(ins);
                else
                    getSelection().select(ins, false);
                Log.add("Отмена выделения " + ins);
                getDrawingArea().repaint();
            }
        }
    }

    ///////////////////////////////////////////////////////////

    /**
     * Начинает редактирование общих точек
     *
     * @param e событие мыши
     */
    private void startEditingCommonPoints(MouseEvent e) {
        pointIsEditing = true;
    }

    /**
     * Редактирует общеие точки
     *
     * @param e событие мыши
     */
    private void editingCommonPoints(MouseEvent e) {
        ArrayList<GraphicObject> graphicObjects = getSelection().getFiguresByCommonPoint();
        if (KeyListener.isShiftDown() && graphicObjects.size() == 1 && graphicObjects.get(0) instanceof Segment) {
            GraphicObject graphicObject = graphicObjects.get(0);
            graphic_objects.figures.Point2D fixPoint = graphicObject.getPoints().get(graphicObject.getEditingPointIndex() == 1 ? 0 : 1);
            fixPoint = new Point2D(getScreen().XToScreen(fixPoint.getX()), getScreen().YToScreen(fixPoint.getY()));
            double absOnX = Math.abs(fixPoint.getX() - e.getX());
            double absOnY = Math.abs(fixPoint.getY() - e.getY());
            if (absOnX > absOnY) {
                getSelection().editSelected(e.getX(), fixPoint.getY());
            } else {
                getSelection().editSelected(fixPoint.getX(), e.getY());
            }
        } else {
            getSelection().editSelected(e.getX(), e.getY());
        }

        Meter.refreshMeters(graphicObjects);
    }

    /**
     * Завершает редактирование общих точке
     *
     * @param e событие мыши
     */
    private void endEditingCommonPoints(MouseEvent e) {
        getSelection().setToZeroEditingPoint();
        pointIsEditing = false;
        getSelection().onSelectionChanged();
        Log.add("Точка(-и) фигур перемещены");
        getStates().fixState();
    }

    private void startMeasuring(MouseEvent e) {
        Point2D p = getScreen().pointToPlane(e.getX(), e.getY());
        final GraphicObject go = getSelection().getFigureByPoint(p);
        if (go != null) {
            JPanel measuringPanel = new JPanel();
            measuringPanel.add(new JLabel() {{
                switch (go.getType()) {
                    case SEGMENT:
                        setText("Длина отрезка: ");
                        break;
                    case CIRCLE:
                        setText("Диаметр окружности: ");
                        break;
                    case ARC:
                        setText("Диаметр дуги: ");
                        break;
                }
                setText(getText() + String.format("%.4f", go.getSizeMeasurement()).replace(',', '.') + " мм");
            }});
            getPropertyBox().setPanel(measuringPanel);
        } else {
            getPropertyBox().setPanel(null);
        }
        meterCreated = false;
    }

    private void measuring(MouseEvent e) {
        if (!meterCreated) {
            startBuildFigure(e.getX(), e.getY());
            meterCreated = true;
        }
    }


    /**
     * Вызывается при нажатии кнопки мыши без отпускания
     *
     * @param e событие мыши
     */
    public void mousePressed(MouseEvent e) {
        if (!isMorphingMode()) {
            pressedButton = e.getButton();
            startSelectPoint = null;
            startMovePoint = null;

            Log.add("Нажата клавиша мыши " + pressedButton + " в точке (" + e.getX() + ", " + e.getY() + ")");
            switch (pressedButton) {
                case MouseEvent.BUTTON1:
                    if (getToolBar().isSelectedTransform()) {
                        startMoveDrawingArea(e);
                    } else {
                        switch (getToolMode()) {
                            case CURSOR:
                                //Фигура заменена на лист фигур
                                graphicObjectList = getSelection().getFiguresByPoint(new Point(e.getX(), e.getY()));

                                if (graphicObjectList.size() > 0) {
                                    if (getSelection().initFiguresByCommonPoint(e.getX(), e.getY()))
                                        //все необходимые операции происходят в условии ветвления, больше ничего не нужно
                                        startEditingCommonPoints(e);
                                    else {
                                        selectOrUnSelectFigure(e);
                                        if (!KeyListener.isControlDown())
                                            startMoveFigures(e);
                                    }
                                } else if (!KeyListener.isControlDown())
                                    startMassSelection(e);
                                break;
                            case RULER:
                                startMeasuring(e);
                                break;
                            default:
                                if (GraphicObject.figureIsBuilding())
                                    if (GraphicObject.getBuildingGraphicObject().canBeFinished())
                                        endBuildFigure();
                                    else
                                        buildFigureNextStage(e);
                                else
                                    startBuildFigure(e.getX(), e.getY());
                                break;
                        }
                    }
                    break;
                case MouseEvent.BUTTON2:
                case MouseEvent.BUTTON3:
                    startMoveDrawingArea(e);
                    break;
            }
        }
    }

    /**
     * При перемещении курсора мыши
     *
     * @param e событие мыши
     */
    public void mouseMoved(MouseEvent e) {
        if (GraphicObject.figureIsBuilding())
            changeBuildingFigure(e);
        lastPoint.setLocation(e.getX(), e.getY());
    }

    /**
     * Вызывается при нажатии кнопки и перемещении курсора мыши без отпускания кнопки
     *
     * @param e событие мыши
     */
    public void mouseDragged(MouseEvent e) {
        if (!isMorphingMode()) {
            switch (pressedButton) {
                case MouseEvent.BUTTON1:
                    if (getToolBar().isSelectedTransform()) {
                        moveDrawingArea(e);
                    } else {
                        switch (getToolMode()) {
                            case CURSOR:
                                if (startSelectPoint == null)
                                    if (startMovePoint == null)
                                        // для редактирования общих точек
                                        editingCommonPoints(e);
                                    else
                                        moveFigures(e);
                                else
                                    massSelection(e);
                                break;
                            case RULER:
                                measuring(e);
                            default:
                                if (GraphicObject.figureIsBuilding())
                                    changeBuildingFigure(e);
                                break;
                        }
                    }
                    break;
                case MouseEvent.BUTTON2:
                case MouseEvent.BUTTON3:
                    moveDrawingArea(e);
                    break;
            }
        }
    }

    /**
     * При отпускании кнопки мыши
     *
     * @param e
     */
    public void mouseReleased(MouseEvent e) {
        if (!isMorphingMode()) {
            if (pressedButton == MouseEvent.BUTTON1) {
                if (!getToolBar().isSelectedTransform()) { // если режим перемещения области черчения выключен
                    if (getSelection().isSelecting())  // если есть рамка для выделения
                        endMassSelection(e);
                    if (startMovePoint != null) // если есть начальная точка перемещения
                        endMoveFigures(e);
                    if (pointIsEditing)
                        endEditingCommonPoints(e);
                    if (GraphicObject.figureIsBuilding())
                        if (GraphicObject.getBuildingGraphicObject().canBeFinished())
                            endBuildFigure();
                }
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int scrollAmount = 1;
        int wheelRotation = e.getWheelRotation();
        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL)
            scrollAmount = e.getScrollAmount();
        getScreen().addScale(-scrollAmount * wheelRotation, e.getX(), e.getY());
    }

    public void mouseClicked(MouseEvent e) {
        if (!isMorphingMode()) {
            if (e.getClickCount() == 2 && !KeyListener.isControlDown()) {
                graphicObjectList = getSelection().getFiguresByPoint(new Point(e.getX(), e.getY()));
                if (graphicObjectList.size() > 0) {
                    for (GraphicObject fig : graphicObjectList) {
                        if (fig.isGrouped() && fig.isSelected()) {
                            getSelection().selectNextGroup(fig);
                            break;
                        }
                    }
                }
                Log.add("Двойной клик!");
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
        mouseOnDrawingArea = true;
    }

    public void mouseExited(MouseEvent e) {
        mouseOnDrawingArea = false;
    }
}
