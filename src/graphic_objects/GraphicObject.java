package graphic_objects;

import core.Group;
import graphic_objects.figures.FigureInterface;
import graphic_objects.figures.Point2D;
import graphic_objects.figures.Point2D.PointType;
import graphic_objects.figures.properties.Properties;
import graphic_objects.meters.Meter;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static core.DrawingArea.*;
import static core.geometry_algorithms.FigureAlgo.computeDistance;
import static core.geometry_algorithms.PointRounding.roundedPoint;

/**
 * Абстрактный класс для всех создаваемых фигур
 */
public abstract class GraphicObject implements FigureInterface, Cloneable {
    /**
     * Расстояние вокруг фигуры в пикселах, при котором она будет выделена
     */
    public static final int AROUND_SELECT_RADIUS = 10;
    /**
     * Цвет фигуры
     */
    protected static Color COLOR = Color.blue;
    /**
     * Цвет выделенной фигуры
     */
    protected static Color SELECTED_COLOR = Color.green;
    /**
     * Цвет фигуры при инициализации
     */
    protected static Color BUILDING_COLOR = Color.black;
    /**
     * Цвет фигуры в группе
     */
    protected static Color GROUPING_COLOR = Color.black;
    /**
     * Тип линии фигуры
     */
    protected static BasicStroke STROKE = new BasicStroke(1.5f);
    /**
     * Размер кисти у рамки выделения
     */
    private final static float[] dash_size = {4.0f};
    /**
     * Тип линии фигуры при инициализации
     */
    protected static final BasicStroke BUILDING_STROKE = new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 4.0f, dash_size, 2.0f);
    /**
     * Ссылка на создающуюся фигуру
     */
    protected static GraphicObject buildingGraphicObject = null;

    public static int MINIMUM_SIZE_FIGURE = 15;
    /**
     * Лист точек, задающих форму фигуры
     */
    protected List<Point2D> points = new ArrayList<>();
    /**
     * Номер редактируемой точки в листе
     */
    protected int editingPoint = -1;
    /**
     * Тип фигуры (отрезок, окружность и т.д.)
     */
    private GraphicObjectType type;
    /**
     * Измеритель фигуры
     */
    private Set<Meter> measuringMeter = new HashSet<>();

    /**
     * Флаг выделения фигуры
     */
    private boolean selected = false;
    private boolean grouped = false;
    private boolean morphed = false;
    /**
     * Флаг создания фигуры (создается ли текущая фигура)
     */
    private boolean building = false;
    /**
     * Экземпляр свойств фигуры
     */
    private Properties properties;

    /**
     * Возвращает, создается ли фигура
     *
     * @return true - создается, false - не создается
     */
    public static boolean figureIsBuilding() {
        return GraphicObject.getBuildingGraphicObject() != null;
    }

    /**
     * @return Создаваемую в данный момент фигуру
     */
    public static GraphicObject getBuildingGraphicObject() {
        return buildingGraphicObject;
    }

    /**
     * Завершает создание фигуры
     */
    public static void finishBuildingFigure() {
        getSelection().select(buildingGraphicObject, false);
        buildingGraphicObject.refreshPoints();
        buildingGraphicObject.setBuilding(false);
        buildingGraphicObject = null;
    }

    /**
     * Задает создаваемую фигуру
     *
     * @param f фигура
     */
    public static void startBuildingFigure(GraphicObject f) {
        buildingGraphicObject = f;
    }

    /**
     * Получает панель свойств данной фигуры
     *
     * @return панель свойств
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * Устанавливает панель свойств для данной фигуры
     *
     * @param p панель свойств
     */
    public void setProperties(Properties p) {
        this.properties = p;
    }

    /**
     * Возвращает список опорных точек
     *
     * @return список опорных точек
     */
    public List<Point2D> getPoints() {
        return points;
    }

    /**
     * Заменяет все точки фигуры
     *
     * @param arr Лист точек фигуры
     */
    public void setPoints(List<Point2D> arr) {
        points = arr;
    }

    /**
     * @return Возвращает индекс точки, которая редактируется в данный момент
     */
    public int getEditingPointIndex() {
        return editingPoint;
    }

    /**
     * @return Возвращает точку, которая редактируется в данный момент
     */
    public Point2D getEditingPoint() {
        return getPoint(editingPoint);
    }

    /**
     * Изменяет редактируемую точку
     */
    public void setEditingPoint(int a) {
        editingPoint = a;
    }

    /**
     * Возвращает точку по ее идентификатору
     *
     * @param id идентификатор точки
     * @return Точку фигуры
     */
    public Point2D getPoint(int id) {
        return points.get(id);
    }

    /**
     * @return Тип фигуры
     */
    public GraphicObjectType getType() {
        return type;
    }

    /**
     * Установливает тип фигуры
     *
     * @param type типа фигуры
     */
    protected void setType(GraphicObjectType type) {
        this.type = type;
    }

    /**
     * @return Фигура создается в данный момент
     */
    public boolean isBuilding() {
        return building;
    }

    public boolean isEditing() {
        return editingPoint != -1;
    }

    public boolean isMeasured() {
        return measuringMeter.size() != 0;
    }

    public void addMeter(Meter m) {
        measuringMeter.add(m);
    }

    public void removeMeter(Meter m) {
        measuringMeter.remove(m);
    }

    public List<Meter> getMeters() {
        return new ArrayList<>(measuringMeter);
    }

    /**
     * Помечает фигуру как создающуюся/созданную
     *
     * @param f true - создающуюся, false - созданную
     */
    public void setBuilding(boolean f) {
        this.building = f;
    }

    /**
     * @return Фигура является выделенной
     */
    public boolean isSelected() {
        return selected;
    }

    public boolean isGrouped() { return grouped; }

    public boolean isMorphed(){
        return morphed;
    }
    /**
     * Помечает фигуру выделенной/не выделенной.
     *
     * @param f true - выделенной, false - не выделенной
     */
    //TODO: замечание: должна быть доступна только из Selection!
    public void setSelected(boolean f) {
        selected = f;
    }

    public void setGrouped(boolean b) {
        grouped = b;
    }

    public void setMorphed(boolean b) {
        morphed = b;
    }
    /**
     * Округляет точку, расположенную на плоскости, к точке, которая принадлежит этой фигуре.
     * Если поблизости нет точек фигуры, то ничего не происходит
     *
     * @param p Округляемая точка на плоскости
     * @return Точка округлена
     */
    public boolean roundToNearestPoint(Point2D p) {
        for (Point2D fp : points) {
            if (computeDistance(p, fp) < AROUND_SELECT_RADIUS / getScreen().getScale()) {
                p.setTo(fp);
                return true;
            }
        }
        return false;
    }

    /**
     * @param p Точка на плоскости
     * @return Ссылку на ближайшую точку фигуры
     */
    public Point2D getNearestPoint(Point2D p) {
        for (Point2D fp : points) {
            if (computeDistance(p, fp) < AROUND_SELECT_RADIUS / getScreen().getScale()) {
                return fp;
            }
        }
        return null;
    }

    /**
     * Инициализирует точку фигуры для редактирования
     *
     * @param x X координата курсора
     * @param y Y координата курсора
     * @return Точка инициализирована
     */
    public boolean initEditingPoint(int x, int y) {
        if (isSelected() && getVisibleSize() / 2 * getScreen().getScale() > MINIMUM_SIZE_FIGURE) {
            Point2D roundedPoint = getScreen().pointToPlane(x, y);
            if (roundToNearestPoint(roundedPoint)
                    && roundedPoint.getType() == PointType.FORMATIVE) {
                for (int i = 0; i < points.size(); i++)
                    if (getPoint(i).equalTo(roundedPoint)) {
                        editingPoint = i;
                        return true;
                    }
            }
        }
        return false;
    }

    /**
     * Возвращает ближайшую точку к заданной, принадлежащую фигуре
     *
     * @param p Заданная точка на плоскости
     * @return ближайшую точку к p
     */
    public static Point2D getNearestFigurePoint(Point2D p) {
        for (GraphicObject go : getData().getFigures()) {
            for (Point2D fp : go.getPoints()) {
                if (computeDistance(p, fp) < AROUND_SELECT_RADIUS / getScreen().getScale()) {
                    return fp;
                }
            }
        }
        return null;
    }

    public abstract Point2D getCenter();

    public abstract double getVisibleSize();

    public abstract double getSizeMeasurement();

    public abstract double getLength();

    /**
     * Возвращает последовательность связанных между собой точек, которые при соединении образуют графическое представление данного объекта
     * @param n Число точек в разбиении
     * @return Массив точек после разбиения
     */
    public abstract ArrayList<java.awt.geom.Point2D> getSegmentatedObject(int n);

    /**
     * Вызывается при создании фигуры
     *
     * @param x X координата начальной точки
     * @param y Y координата начальной точки
     */
    public void onCreate(int x, int y) {
        getSelection().select(this, true);
        setBuilding(true);
        Point2D p = null;
        if (this instanceof Meter)
            p = getScreen().pointToPlane(x, y);
        else
            p = roundedPoint(x, y);
        initFigure(p);
        onChange(x, y);
    }

    /**
     * Смещает редактируемую точку фигуры к курсору
     *
     * @param x X координата курсора
     * @param y Y координата курсора
     */
    public void movePointWithMouse(double x, double y) {
        Point2D roundedPoint = roundedPoint(x, y);
        editPointWithMouse(editingPoint, roundedPoint);
    }

    /**
     * Отрисовывает фигуру
     *
     * @param g компонент графики
     */
    public void draw(Graphics2D g) {
        Color vrc = g.getColor();
        Stroke vrs = g.getStroke();

        g.setStroke(STROKE);
        if (isSelected())
            g.setColor(SELECTED_COLOR);
        else if(isGrouped())
            g.setColor(GROUPING_COLOR);
        else
            g.setColor(COLOR);

        drawPrimitive(g);

        if(getProperties() != null)
            getProperties().onChange();
        g.setColor(vrc);
        g.setStroke(vrs);
    }

    /**
     * Отрисовывает точки фигур
     *
     * @param g Компонент графики
     */
    public void drawPoints(Graphics2D g, GraphicObject f) {
        for (Point2D p : f.getPoints())
            p.draw(g);
    }

    /**
     * Клонирует текущий объект
     *
     * @return копия объекта
     */
    @Override
    public GraphicObject clone() {
        try {
            GraphicObject obj = (GraphicObject) super.clone();
            obj.setSelected(false);
            return obj;
        } catch (CloneNotSupportedException e1) {
            e1.printStackTrace();
            return null;
        }
    }

    /**
     * @return Фигура может быть достроена
     */
    public abstract boolean canBeFinished();

    /**
     * Вызывается при изменении фигуры.
     * Например при перемещении строящейся точки
     *
     * @param x X координата строящейся точки
     * @param y Y координата строящейся точки
     */
    public abstract void onChange(double x, double y);

    /**
     * Обновляет свойства всех точек фигуры
     */
    public abstract void refreshPoints();

    /**
     * Начинает следующий этап создания фигуры
     *
     * @param x
     * @param y
     */
    public abstract void nextStage(int x, int y);

    /**
     * Меняет координаты заданной точки фигуры к точке на плоскости
     *
     * @param i Номер заданной точки в списке
     * @param p Точка на плоскости
     * @return Успешность выполнения
     */
    protected abstract boolean editPointWithMouse(int i, Point2D p);

    /**
     * Инициализирует точки фигуры
     *
     * @param p Точка на плоскости
     */
    protected abstract void initFigure(Point2D p);

    /**
     * Отрисовка конкретной фигуры
     *
     * @param g Компонент графики
     */
    protected abstract void drawPrimitive(Graphics2D g);

    public enum GraphicObjectType {
        LINE("Линия"), SEGMENT("Отрезок"), CIRCLE("Окружность"),
        ARC("Дуга окружности"), RULER("Линейка"), PROTRACTOR("Транспортир"), TREE("Фрактальное дерево");

        private String type;

        GraphicObjectType(String t) {
            type = t;
        }

        @Override
        public String toString() {
            return type;
        }
    }
}