package graphic_objects.figures;

import core.Screen;
import core.Settings;
import graphic_objects.GraphicObject;
import graphic_objects.meters.Meter;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static core.DrawingArea.getScreen;
import static core.geometry_algorithms.FigureAlgo.computeDistance;
import static core.geometry_algorithms.FigureAlgo.limitedVisibleSize;

/**
 * Точка на плоскости с координатами x,y
 */
public class Point2D implements FigureInterface {

    private GraphicObject owner = null;
    private Meter measuringMeter = null;
    private int POINT_SIZE = -1;
    private Color POINT_COLOR = null;
    private PointType type;
    private double x;
    private double y;

    /**
     * По умолчанию точке задаются координаты x = 0, y = 0
     */
    public Point2D() {
        x = 0;
        y = 0;
        type = PointType.INVISIBLE;
    }

    /**
     * Задает точку на плоскости
     *
     * @param x координата точки по ox
     * @param y координата точки по oy
     */
    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
        type = PointType.INVISIBLE;
    }

    /**
     * Копирует заданную точку
     *
     * @param p заданная точка
     */
    public Point2D(Point2D p) {
        owner = p.getOwner();
        x = p.x;
        y = p.y;
        type = p.getType();
        POINT_SIZE = type.getSize();
        POINT_COLOR = type.getColor();
    }

    /**
     * Задает точку на плоскости с заданным типом
     *
     * @param p    Точка на плоскости
     * @param type Тип точки
     */
    public Point2D(Point2D p, PointType type, GraphicObject f) {
        owner = f;
        x = p.x;
        y = p.y;
        this.type = type;
        POINT_SIZE = type.getSize();
        POINT_COLOR = type.getColor();
    }

    public Point2D(double x, double y, PointType type, GraphicObject f) {
        owner = f;
        this.x = x;
        this.y = y;
        this.type = type;
        POINT_SIZE = type.getSize();
        POINT_COLOR = type.getColor();
    }

    /**
     * @return Тип точки
     */
    public PointType getType() {
        return type;
    }

    public void setType(PointType pt) {
        type = pt;
        POINT_SIZE = pt.getSize();
        POINT_COLOR = pt.getColor();
    }

    /**
     * Возвращает координату точки по ox
     *
     * @return координата точки по ox
     */
    public double getX() {
        return x;
    }

    /**
     * Устанавливает координату точки по ox
     *
     * @param x координата точки по ox
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Возвращает координату точки по oy
     *
     * @return координата точки по oy
     */
    public double getY() {
        return y;
    }

    /**
     * Устанавливает координату точки по oy
     *
     * @param y координата точки по oy
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Заменяет точку на заданную
     *
     * @param p Заданная точка
     */
    public void setTo(Point2D p) {
        owner = p.getOwner();
        x = p.getX();
        y = p.getY();
        type = p.getType();
        POINT_SIZE = p.getType().getSize();
        POINT_COLOR = p.getType().getColor();
    }

    /**
     * Смещает точку по осям
     *
     * @param dx смещение по ox
     * @param dy смещение по oy
     */
    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public GraphicObject getOwner() {
        return owner;
    }

    public void draw(Graphics2D g) {
        Screen sc = getScreen();
        Color vrc = g.getColor();

        double dsize = limitedVisibleSize(POINT_SIZE, owner.getVisibleSize());
        switch (type) {
            case FORMATIVE:
                g.setColor(POINT_COLOR);
                g.fill(new Ellipse2D.Double(sc.XToScreen(x) - dsize, sc.YToScreen(y) - dsize,
                        dsize * 2, dsize * 2));
                break;
            case ANCHOR:
                g.setColor(POINT_COLOR);
                g.fill(new Rectangle2D.Double(sc.XToScreen(x) - dsize, sc.YToScreen(y) - dsize,
                        dsize * 2, dsize * 2));
                break;
            case AUXILIARY:
                g.setColor(POINT_COLOR);
                g.draw(new Line2D.Double(sc.XToScreen(x) - dsize, sc.YToScreen(y),
                        sc.XToScreen(x) + dsize, sc.YToScreen(y)));
                g.draw(new Line2D.Double(sc.XToScreen(x), sc.YToScreen(y) - dsize,
                        sc.XToScreen(x), sc.YToScreen(y) + dsize));
                break;
        }
        g.setColor(vrc);
    }

    @Override
    public boolean contain(Point2D p) {
        return computeDistance(p, this) < GraphicObject.AROUND_SELECT_RADIUS / getScreen().getScale();
    }

    @Override
    public boolean equalTo(FigureInterface fi) {
        Point2D a = (Point2D) fi;
        int ac = Settings.ACCURACY;
        return Math.rint(this.getX() * ac) / ac == Math.rint(a.getX() * ac) / ac
                && Math.rint(this.getY() * ac) / ac == Math.rint(a.getY() * ac) / ac;
    }

    @Override
    public Point2D getPoint(int id) {
        if (id == 0)
            return new Point2D(x, y);
        return null;
    }

    @Override
    public List<Point2D> getPoints() {
        return new ArrayList<Point2D>() {{
            add(new Point2D(x, y));
        }};
    }

    public enum PointType {
        DETACHED("Обособленная", 3, Color.BLUE),
        /**
         * Обозначает точки, перемещение которых изменяет форму фигуры. Графически представляется синим кругом.
         */
        FORMATIVE("Образующая", 3, Color.BLUE),
        /**
         * Используется для перемещения фигуры. Графически представляется черным квадратом
         */
        ANCHOR("Якорная", 3, Color.BLACK),
        /**
         * Графически представляется черным перекрестием
         */
        AUXILIARY("Вспомогательная", 4, Color.BLACK),
        /**
         * Используется в математических расчетах.
         */
        INVISIBLE("Невидимая");

        public static final BasicStroke POINT_STROKE = new BasicStroke(1.5f);

        private String type;
        private Color color;
        private int size;

        PointType(String t) {
            type = t;
        }

        PointType(String t, int size, Color color) {
            type = t;
            this.size = size;
            this.color = color;
        }

        int getSize() {
            return size;
        }

        Color getColor() {
            return color;
        }

        @Override
        public String toString() {
            return type;
        }
    }
}