package core;

import graphic_objects.GraphicObject;
import graphic_objects.figures.Point2D;
import graphic_objects.meters.Meter;
import ui.MainWindow;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import static core.DrawingArea.*;
import static core.geometry_algorithms.PointRounding.displacePoints;
import static ui.MainWindow.*;

/**
 * Система выделений
 */
public class Selection {
    /**
     * Размер кисти у рамки выделения
     */
    private final static float[] dash_size = {4.0f};
    /**
     * Кисть рамки выделения
     */
    private final static BasicStroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 4.0f, dash_size, 2.0f);

    /**
     * Увеличение размера статичной рамки выделения на константную величину
     */
    private final static int RECTANGLE_INDENT = 5;

    /**
     * Цвет рамки выделения
     */
    private final static Color STROKE_COLOR = new Color(255, 100, 0, 255);
    private Rectangle2D selectingRect;
    private Rectangle2D staticRect;
    private double left, right, top, bottom;

    /**
     * Активен ли процесс выделения (нужно при прорисовке рамки в DrawingArea.paint())
     */
    private boolean selecting = false;

    private boolean copyMode = false;

    private Point2D mousePressed = new Point2D();

    private ArrayList<GraphicObject> selectedFigures = new ArrayList<>();
    private ArrayList<GraphicObject> figuresByCommonPoint = new ArrayList<>();
    private GraphicObject selectedMeter = null;
    private Group selectedGroup = null;

    public Selection() {
        selectingRect = new Rectangle2D.Double();
        staticRect = new Rectangle2D.Double();
        initStaticRect();
    }

    /**
     * Задает область выделения и проверяет принадлежат ли центры фигур данной области
     *
     * @param a точка начала выделения
     * @param b точка конца выделения
     */
    public void selectArea(Point a, Point b) {
        selectingRect.setRect(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.abs(a.x - b.x), Math.abs(a.y - b.y));
        setSelecting(true);

        // принадлежит ли хоть одна точка фигур области выделения
        for (GraphicObject f : getData().getFigures()) {
            for (int i = 0; i < f.getPoints().size(); i++) {
                if (contain(f.getPoint(i))) {
                    select(f, true);
                    break;
                }
                select(f, false);
            }
        }
        getDrawingArea().repaint();
    }

    /**
     * Сменить выделение фигуры
     *
     * @param f Фигура
     * @param b Выделить фигуру
     */
    public void select(GraphicObject f, boolean b) {
        if (f.isSelected() != b) {
            if (b) {
                if (mayBeSelected(f)) {
                    if (f instanceof Meter)
                        selectedMeter = f;
                    else
                        selectedFigures.add(f);
                    f.setSelected(b);
                }
            } else {
                if (f instanceof Meter)
                    selectedMeter = null;
                else
                    selectedFigures.remove(f);
                f.setSelected(b);
            }

            Group selected_group = getData().getGroup(selectedFigures);
            if(selected_group == null) {
                selectedGroup = null;
                MainWindow.getToolBox().selectGrouping(false);
                MainWindow.getToolBox().selectMorphing(1);
            }
            else {
                selectedGroup = selected_group;
                MainWindow.getToolBox().selectGrouping(true);

                if(getDrawingArea().getMorpher().contains(selectedGroup))
                    MainWindow.getToolBox().selectMorphing(3);
                //else
                //    MainWindow.getToolBox().selectMorphing(1);
            }
        }
    }

    public void selectNextGroup(GraphicObject fig){
        Group group = getData().getNextGroup(fig, selectedGroup);
        unselectAll();
        if(group != null){
            Iterator iterator = group.iterator();
            while(iterator.hasNext()){
                select((GraphicObject) iterator.next(), true);
            }
        } else {
            select(fig, true);
        }
    }

    public void selectGroup(GraphicObject fig){
        Group group = getData().getNextGroup(fig, null);
        if(group != null) {
            Iterator iterator = group.iterator();
            while (iterator.hasNext()) {
                select((GraphicObject) iterator.next(), true);
            }
        }
    }

    public void unselectGroup(GraphicObject fig){
        Group group = getData().getNextGroup(fig, null);
        if(group != null) {
            Iterator iterator = group.iterator();
            while (iterator.hasNext()) {
                select((GraphicObject) iterator.next(), false);
            }
        }
    }

    public void groupObjects() {
        if(getCountSelected() > 0) {
            if(getData().getGroup(selectedFigures) == null) {
                selectedGroup = new Group(selectedFigures);
                getData().addGrope(selectedGroup);
                MainWindow.getToolBox().selectGrouping(true);
                for(GraphicObject f : selectedFigures)
                    f.setGrouped(true);
            }
        }
    }

    public void ungroupObjects() {
        if(getCountSelected() > 0) {
            Group searched = getData().getGroup(selectedFigures);
            if(searched != null) {
                getData().removeGroup(searched);
                MainWindow.getToolBox().selectGrouping(false);

                if(searched.equals(getDrawingArea().getMorpher().getStartingPairGroup()) ||
                        searched.equals(getDrawingArea().getMorpher().getEndingPairGroup())) {
                    getDrawingArea().getMorpher().removeCreatingMorphingPair();
                    MainWindow.getToolBox().selectMorphing(1);
                } else if(getDrawingArea().getMorpher().contains(searched)){
                    getDrawingArea().getMorpher().removeMorphingPair(searched);
                    MainWindow.getToolBox().selectMorphing(1);
                }

                for(GraphicObject f : selectedFigures) {
                    //Проверка на то, что объект не находится ни в одной группе
                    Group group = getData().getNextGroup(f, null);
                    if(group == null)
                        f.setGrouped(false);
                }
            }
        }
    }

    public Group getSelectedGroup(){
        return selectedGroup;
    }

    /**
     * @param f Инструмент
     * @return Выделение возможно
     */
    private boolean mayBeSelected(GraphicObject f) {
        return selectedMeter == null && !(getCountSelected() > 0 && f instanceof Meter);
    }

    /**
     * Инициализирует прямоугольник, который ограничивает выделенные фигуры
     */
    private void initStaticRect() {
        left = Double.MAX_VALUE;
        right = Double.MIN_VALUE;
        top = Double.MAX_VALUE;
        bottom = Double.MIN_VALUE;
    }

    /**
     * Проверяет попадание курсора в область, ограниченную серой рамкой
     *
     * @param x X координата курсора
     * @param y Y координата курсора
     * @return Курсор попал в прямоугольник
     */
    public boolean hitStaticRect(int x, int y) {
        if (staticRect.contains(x, y))
            return true;
        else {
            initStaticRect();
            return false;
        }
    }

    /**
     * Перерасчитывает границы статичного прямоугольника выделения
     */
    public void setStaticRect() {
        Screen sc = getScreen();
        initStaticRect();
        for (GraphicObject f : getData().getFigures()) {
            if (f.isSelected()) {
                left = Math.min(left, sc.XToScreen(f.getCenter().getX()) - f.getVisibleSize() / 2 * sc.getScale());
                right = Math.max(right, sc.XToScreen(f.getCenter().getX()) + f.getVisibleSize() / 2 * sc.getScale());
                top = Math.min(top, sc.YToScreen(f.getCenter().getY()) - f.getVisibleSize() / 2 * sc.getScale());
                bottom = Math.max(bottom, sc.YToScreen(f.getCenter().getY()) + f.getVisibleSize() / 2 * sc.getScale());
            }
        }
        staticRect.setRect(Math.max(Math.min(left, getDimensions().getX()), 0) - RECTANGLE_INDENT,
                Math.max(Math.min(top, getDimensions().getY()), 0) - RECTANGLE_INDENT,
                Math.min(right - left, Math.min(getDimensions().getX(), Math.min(right,
                        getDimensions().getX() - left))) + 2 * RECTANGLE_INDENT,
                Math.min(bottom - top, Math.min(getDimensions().getY(), Math.min(bottom,
                        getDimensions().getY() - top))) + 2 * RECTANGLE_INDENT);
    }

    public Rectangle2D getSelectedRectangle(){
        Rectangle2D rekt = new Rectangle2D.Double();
        double left = Double.MAX_VALUE;
        double right = Double.MIN_VALUE;
        double top = Double.MIN_VALUE;
        double bottom = Double.MAX_VALUE;

        if(getCountSelected() > 0) {
            for (GraphicObject f : getData().getFigures()) {
                if (f.isSelected()) {
                    for (Point2D p : f.getPoints()) {
                        left = Math.min(left, p.getX());
                        right = Math.max(right, p.getX());
                        top = Math.max(top, p.getY());
                        bottom = Math.min(bottom, p.getY());
                    }
                }
            }
        } else {
            left = 0;
            right = 0;
            top = 0;
            bottom = 0;
        }

        rekt.setRect(left, bottom, right - left, top - bottom);
        return rekt;
    }

    /**
     * Возвращает флаг процесса выделения
     *
     * @return true - идет выделение, false - выделение завершено
     */
    public boolean isSelecting() {
        return this.selecting;
    }

    /**
     * Устанавливает флаг, что идет процесс выделения
     *
     * @param sel true - идет выделение, false - выделение завершено
     */
    public void setSelecting(boolean sel) {
        this.selecting = sel;
    }

    /**
     * Рисует рамку выделения
     *
     * @param g компонент графики
     */
    public void draw(Graphics2D g) {
        Color vrc = g.getColor();
        Stroke vrs = g.getStroke();

        g.setColor(STROKE_COLOR);
        g.setStroke(dashed);
        if (isSelecting())
            g.draw(selectingRect);

        g.setColor(vrc);
        g.setStroke(vrs);
    }

    /**
     * Попадает ли заданная точка в область выделения
     *
     * @param point заданная точка
     * @return true - попадает, false - не попадает
     */
    public boolean contain(Point2D point) {
        java.awt.geom.Point2D p =
                new java.awt.geom.Point2D.Double(getScreen().pointToScreen(point).getX(), getScreen().pointToScreen(point).getY());
        return selectingRect.contains(p);
    }

    /**
     * @param p точка на экране
     * @return лист фигур в заданной точке
     */
    public ArrayList<GraphicObject> getFiguresByPoint(Point p) {
        ArrayList<GraphicObject> list = new ArrayList<>();
        mousePressed = getScreen().pointToPlane(p);

        for (GraphicObject f : getData().getFigures())
            if (f.contain(mousePressed)) {
                list.add(f);
            }
        //Добавление линейки в список
        for (GraphicObject f : getData().getMeters())
            if (f.contain(mousePressed)) {
                list.add(f);
                break;
            }
        return list;
    }

    /**
     * @param p точка на плоскости
     * @return фигуру в заданной точке
     */
    public GraphicObject getFigureByPoint(Point2D p) {
        for (GraphicObject f : getData().getFigures())
            if (f.contain(p)) {
                return f;
            }
        return null;
    }

    /**
     * Снимает выделение со всех фигур
     */
    public void unselectAll() {
        for (GraphicObject f : getData().getFigures())
            select(f, false);
        for (GraphicObject f : getData().getMeters())
            select(f, false);
        getDrawingArea().repaint();
        //setStaticRect();
    }

    /**
     * Устанавливает выделение на все фигуры
     */
    public void selectAll() {
        getSelection().unselectAll();
        for (GraphicObject f : getData().getFigures())
            select(f, true);
        setToolMode(ToolMode.CURSOR);
        getDrawingArea().repaint();
        onSelectionChanged();
    }

    /**
     * Возвращает выделенные фигуры
     *
     * @return список выделенных фигур
     */
    public ArrayList<GraphicObject> getSelected() {
        return selectedFigures;
    }

    /**
     * Инициализирует лист фигур, которые имеют общую точку под курсором
     *
     * @param x X координата курсора
     * @param y Y координата курсора
     */
    public boolean initFiguresByCommonPoint(int x, int y) {
        figuresByCommonPoint.clear();
        for (GraphicObject f : getSelected())
            if (f.initEditingPoint(x, y)) {
                figuresByCommonPoint.add(f);
            }
        return figuresByCommonPoint.size() > 0;
    }

    /**
     * Приводит номер редактируемой точки в дефолтное состояние. Применяется после окончания редактирования точки
     */
    public void setToZeroEditingPoint() {
        for (GraphicObject f : getSelected())
            f.setEditingPoint(-1);
    }

    public ArrayList<GraphicObject> getFiguresByCommonPoint() {
        return figuresByCommonPoint;
    }

    /**
     * Возвращает количество выделенных фигур
     *
     * @return количество выделенных фигур
     */
    public int getCountSelected() {
        return getSelected().size();
    }

    /**
     * Смещает выделенные фигуры
     *
     * @param mouse точка на панели рисования
     */
    public void moveSelected(Point mouse) {
        if (selectedMeter != null) {
            selectedMeter.onChange(mouse.x, mouse.y);
        } else
            displacePoints(getSelected(), getScreen().pointToPlane(mouse));
        getDrawingArea().repaint();
    }

    /**
     * Удаляет выделенные фигуры
     */
    public void removeSelected() {
        boolean isRemoved = false;

        ungroupObjects();
        Iterator<GraphicObject> it = getSelected().iterator();
        while (it.hasNext()) {
            GraphicObject f = it.next();
            it.remove();
            getData().removeFigure(f);
            isRemoved = true;
        }
        if (selectedMeter != null)
            getData().removeMeter(selectedMeter);
        if (isRemoved) {
            selectedFigures.clear();
            getDrawingArea().repaint();
            getStates().fixState();
            onSelectionChanged();
        }
    }

    /**
     * Перемещает общую точку выделенных фигур под курсор. Перед применением требует initFiguresByCommonPoint
     *
     * @param x X координата курсора
     * @param y Y координата курсора
     */
    public void editSelected(double x, double y) {
        for (GraphicObject f : getFiguresByCommonPoint())
            f.movePointWithMouse(x, y);
    }

    /**
     * Вызывается, когда происходит изменение выделения
     */
    public void onSelectionChanged() {
        setStaticRect();
        getDrawingArea().repaint();
        getPropertyBox().onSelectionChanged();

        if (getCountSelected() >= 1 && GraphicObject.getBuildingGraphicObject() == null) {
            getToolBar().setCutEnabled(true);
            getToolBar().setCopyEnabled(true);
            getToolBar().setRemoveEnabled(true);

            getTopMenuBar().setCutEnabled(true);
            getTopMenuBar().setCopyEnabled(true);
            getTopMenuBar().setRemoveEnabled(true);
        } else {
            getToolBar().setCutEnabled(false);
            getToolBar().setCopyEnabled(false);
            if (selectedMeter != null)
                getToolBar().setRemoveEnabled(true);
            else
                getToolBar().setRemoveEnabled(false);

            getTopMenuBar().setCutEnabled(false);
            getTopMenuBar().setCopyEnabled(false);
            if (selectedMeter != null)
                getTopMenuBar().setRemoveEnabled(true);
            else
                getTopMenuBar().setRemoveEnabled(false);
        }
    }

    /**
     * @return Точку на плоскости, откуда началось перемещение примитивов
     */
    public Point2D getPressedPoint() {
        return mousePressed;
    }

    /**
     * Перемещает точку mousePressed
     *
     * @param dx вектор смещения по X
     * @param dy вектор смещения по Y
     */
    public void movePressedPoint(double dx, double dy) {
        mousePressed.move(dx, dy);
    }

    /**
     * Задает режим копирования фигур
     *
     * @param b true - включен, false - выключен
     */
    public void setCopyMode(boolean b) {
        copyMode = b;
    }

    /**
     * Возвращает включен ли режим копирования фигур
     *
     * @return true - включен, false - выключен
     */
    public boolean isCopyMode() {
        return copyMode;
    }
}	