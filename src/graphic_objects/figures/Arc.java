package graphic_objects.figures;

import core.Screen;
import core.geometry_algorithms.VectorAlgo;
import graphic_objects.GraphicObject;
import graphic_objects.figures.Point2D.PointType;
import graphic_objects.figures.properties.ArcProperties;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

import static core.DrawingArea.getData;
import static core.DrawingArea.getMouseListener;
import static core.DrawingArea.getScreen;
import static core.geometry_algorithms.FigureAlgo.*;
import static core.geometry_algorithms.PointRounding.roundedPoint;
import static core.geometry_algorithms.VectorAlgo.*;
import static ui.MainWindow.getDrawingArea;

/**
 * Created by Никита on 09.12.2014.
 */
public class Arc extends GraphicObject implements FigureInterface {
    private final static int BORDER_SIZE = 30;
    //Отрезок при начальной точке, при пересечении которого меняется направление дуги
    private Segment startPointBorder;
    private double startPointAngle;

    private boolean radiusBuilding;
    private double radius;
    private double startingAngle;
    private double angularExtent;
    private boolean counterclockwise;


    /**
     * Устанавливает дугу
     *
     * @param c          точка центра
     * @param r          радиус
     * @param startAngle начальный угол
     * @param endAngle   конечный угол
     */
    public void setArc(Point2D c, double r, double startAngle, double endAngle) {
        initFigure(c);
        setRadius(r);
        setStartingAngle(startAngle);
        setAngularExtent(endAngle - startAngle);
        getPoint(1).setType(PointType.FORMATIVE);
        getPoint(2).setType(PointType.FORMATIVE);
        getPoint(3).setType(PointType.ANCHOR);
        radiusBuilding = false;
        refreshPoints();
    }


    public double getRadius() {
        return radius;
    }

    public void setRadius(double rad) {
        if (rad > 0)
            radius = rad;
    }

    public double getStartingAngle() {
        return startingAngle;
    }

    /**
     * Меняет смещение начальной точки
     *
     * @param angle Смещение начальной точки в градусах
     */
    public void setStartingAngle(double angle) {
        startingAngle = Math.abs(angle) % 360;
    }

    public double getAngularExtent() {
        return angularExtent;
    }

    /**
     * Меняет длину дуги в градусах
     *
     * @param extent Длина дуги в градусах
     */
    public void setAngularExtent(double extent) {
        angularExtent = Math.abs(extent) % 360;
        if (angularExtent == 0)
            angularExtent = 1;
    }

    /**
     * Меняет ориентацию дуги в зависимости от положения курсора относительно начальной точки
     */
    private void switchDirection(Point2D rounded) {
        //Обновление отрезка при начальной точке, так как при изменении масштаба его размер будет меняться
        Point2D center = startPointBorder.getCenter();
        Point2D borderVector = getChangedLengthVector(getVector(startPointBorder), BORDER_SIZE / getScreen().getScale());
        startPointBorder.getPoint(0).setX(center.getX() + borderVector.getX());
        startPointBorder.getPoint(0).setY(center.getY() + borderVector.getY());
        startPointBorder.getPoint(1).setX(center.getX() - borderVector.getX());
        startPointBorder.getPoint(1).setY(center.getY() - borderVector.getY());

        //Проверка, пересек ли курсор начальную точку. Если пересек, сменить направление дуги
        Point vrPoint = getMouseListener().getLastPoint();
        Point2D lastPoint = roundedPoint(vrPoint.x, vrPoint.y);
        if(computeDistance(new Segment(rounded, lastPoint), startPointBorder) == 0) {
            counterclockwise = determinantIsPositive(getVector(getCenter(), center), getVector(lastPoint, rounded));
        }
    }

    /**
     * @return Левая дуга
     */
    public boolean isCounterclockwise() {
        return counterclockwise;
    }

    public void buildWithProperties() {
        if (radiusBuilding) {
            radiusBuilding = false;
            getPoint(1).setType(PointType.FORMATIVE);
            getPoint(2).setType(PointType.FORMATIVE);
            getPoint(3).setType(PointType.ANCHOR);
        }
    }

    @Override
    public boolean contain(Point2D p) {
        double dist = computeDistance(getCenter(), p);
        if ((dist < radius + AROUND_SELECT_RADIUS / getScreen().getScale())
                && (dist > radius - AROUND_SELECT_RADIUS / getScreen().getScale())) {
            if (Math.abs(angularExtent) < 180) {
                if (containPoint(p, angularExtent))
                    return true;
            } else {
                double angle = 360 - angularExtent > 0 ? -angularExtent : angularExtent;
                if (!containPoint(p, angle))
                    return true;
            }
            if (roundToNearestPoint(p))
                return true;
        }
        return false;
    }

    @Override
    public boolean equalTo(FigureInterface fi) {
        Arc s = null;
        try {
            s = (Arc) fi;
        } catch (ClassCastException cce) {
            cce.printStackTrace();
            return false;
        }

        return equal(radius, s.getRadius()) && equal(startingAngle, s.getStartingAngle())
                && equal(angularExtent, s.getAngularExtent()) && getCenter().equalTo(s.getCenter());
    }

    @Override
    public Point2D getCenter() {
        return getPoint(0);
    }

    @Override
    public boolean canBeFinished() {
        return !radiusBuilding && computeDistance(getPoint(1), getPoint(2)) * getScreen().getScale() > MINIMUM_SIZE_FIGURE;
    }

    @Override
    public double getVisibleSize() {
        return 2 * radius * getScreen().getScale();
    }

    @Override
    public double getSizeMeasurement() {
        return 2 * radius;
    }

    @Override
    public double getLength() {
        return radius * Math.toRadians(angularExtent);
    }

    @Override
    public ArrayList<java.awt.geom.Point2D> getSegmentatedObject(int n) {
        if (n < 2)
            return null;

        ArrayList<java.awt.geom.Point2D> list = new ArrayList<>();
        double x = points.get(2).getX();
        double y = points.get(2).getY();
        Point2D radiusVector = getEndingRadiusVector();
        double d = angularExtent / (n - 1);

        list.add(new java.awt.geom.Point2D.Double(x, y));
        for(int i = 0; i < n - 1; i++){
            radiusVector = VectorAlgo.getRotatedVector(radiusVector, d);
            x = points.get(0).getX() + radiusVector.getX();
            y = points.get(0).getY() + radiusVector.getY();
            list.add(new java.awt.geom.Point2D.Double(x, y));
        }

        return list;
    }

    @Override
    public Arc clone() {
        Arc obj = (Arc) super.clone();
        java.util.List<Point2D> newPoints = new ArrayList<>();
        for (Point2D p : obj.getPoints())
            newPoints.add(new Point2D(p, p.getType(), obj));
        obj.setPoints(newPoints);
        obj.setType(getType());
        obj.setProperties(new ArcProperties(obj));
        return obj;
    }

    @Override
    public void onChange(double x, double y) {
        Point2D rounded = roundedPoint(x, y);
        if (radiusBuilding)
            setRadius(computeDistance(rounded, getCenter()));
        else {
            boolean lastClockwise = counterclockwise;
            switchDirection(rounded);

            Point2D v1 = getVector(getCenter(), rounded);
            if(counterclockwise){
                Point2D v2 = new Point2D(radius, 0);
                startingAngle = angleBetweenVectors(v1, v2);
                angularExtent = angleBetweenVectors(getEndingRadiusVector(), getRotatedVerticalVector(-startingAngle));
            } else {
                Point2D v2 = getStartingRadiusVector();
                angularExtent = angleBetweenVectors(v1, v2);
            }

            //При изменении направления дуги следует обнулить параметры
            if(lastClockwise != counterclockwise) {
                startingAngle = startPointAngle;
                angularExtent = 0;
            }
            refreshPoints();
        }
    }

    @Override
    public void refreshPoints() {
        if (!radiusBuilding) {
            Point2D vec;
            vec = getRotatedVerticalVector(-startingAngle);
            getPoint(1).setX(getCenter().getX() + vec.getX());
            getPoint(1).setY(getCenter().getY() + vec.getY());
            vec = getRotatedVerticalVector(-(startingAngle + angularExtent));
            getPoint(2).setX(getCenter().getX() + vec.getX());
            getPoint(2).setY(getCenter().getY() + vec.getY());
            vec = getRotatedVerticalVector(-(startingAngle + angularExtent / 2));
            getPoint(3).setX(getCenter().getX() + vec.getX());
            getPoint(3).setY(getCenter().getY() + vec.getY());
            getDrawingArea().repaint();
        }
    }

    @Override
    public void nextStage(int x, int y) {
        if (radiusBuilding && radius * getScreen().getScale() > MINIMUM_SIZE_FIGURE) {
            Point2D p = roundedPoint(x, y);
            Point2D v1 = new Point2D(radius, 0);
            Point2D v2 = getVector(getCenter(), p);
            startPointAngle = angleBetweenVectors(v2, v1);
            startingAngle = startPointAngle;
            angularExtent = 0;

            Point2D borderVector = getChangedLengthVector(v2, BORDER_SIZE / getScreen().getScale());
            startPointBorder = new Segment(new Point2D(p.getX() + borderVector.getX(), p.getY() + borderVector.getY()),
                    new Point2D(p.getX() - borderVector.getX(), p.getY() - borderVector.getY()));

            buildWithProperties();
        }
    }

    @Override
    protected boolean editPointWithMouse(int i, Point2D point) {
        if (i == 1 || i == 2) {
            getPoint(i).move(point.getX() - getPoint(i).getX(), point.getY() - getPoint(i).getY());
            if (!canBeFinished()) {
                refreshPoints();
                return false;
            }

            //Поиск центра окружности по следующему алгоритму: ищется пересечение прямых, отложенных от концевых точек дуги
            Point2D vec = new Point2D(getPoint(2).getX() - getPoint(1).getX(), getPoint(2).getY() - getPoint(1).getY());
            Point2D vecToCenter1 = getRotatedVector(vec, 180 - (180 - angularExtent) / 2);
            Point2D vecToCenter2 = getRotatedVector(vec, (180 - angularExtent) / 2);
            Point2D center = getIntersection(new Point2D[]
                    {getPoint(1),
                            new Point2D(getPoint(1).getX() + vecToCenter1.getX(), getPoint(1).getY() + vecToCenter1.getY()),
                            getPoint(2),
                            new Point2D(getPoint(2).getX() + vecToCenter2.getX(), getPoint(2).getY() + vecToCenter2.getY())});
            getCenter().move(center.getX() - getCenter().getX(), center.getY() - getCenter().getY());
            radius = computeDistance(getCenter(), getPoint(1));
            startingAngle = angleBetweenVectors(getVector(getCenter(), getPoint(1)), new Point2D(radius, 0));
            refreshPoints();
            return true;
        }
        return false;
    }

    @Override
    protected void initFigure(Point2D p) {
        MINIMUM_SIZE_FIGURE = 10;
        radiusBuilding = true;
        counterclockwise = false;
        startingAngle = 0;
        angularExtent = 360;
        points.add(new Point2D(p, PointType.AUXILIARY, this));
        points.add(new Point2D(p, PointType.INVISIBLE, this));
        points.add(new Point2D(p, PointType.INVISIBLE, this));
        points.add(new Point2D(p, PointType.INVISIBLE, this));
        setType(GraphicObjectType.ARC);
        setProperties(new ArcProperties(this));
        getData().addFigure(this);
    }

    @Override
    protected void drawPrimitive(Graphics2D g) {
        Screen sc = getScreen();

        if (radiusBuilding) {
            g.setColor(BUILDING_COLOR);
            g.setStroke(BUILDING_STROKE);
        }

        Point2D c = getCenter();
        g.draw(new Arc2D.Double(sc.XToScreen(c.getX() - radius), sc.YToScreen(c.getY() + radius),
                radius * 2 * sc.getScale(), radius * 2 * sc.getScale(), startingAngle, angularExtent, Arc2D.OPEN));
    }

    /**
     * Поворачивает вектор (radius, 0) на заданный угол по часовой стрелке
     *
     * @param degrees Заданный угол в градусах
     * @return Вектор от центра окружности до конца дуги
     */
    private Point2D getRotatedVerticalVector(double degrees) {
        return getRotatedVector(new Point2D(radius, 0), degrees);
    }

    /**
     * @return Радиус-вектор, направленный к начальной точке
     */
    private Point2D getStartingRadiusVector() {
        return getVector(getCenter(), getPoint(1));
    }

    /**
     * @return Радиус-вектор, направленный к конечной точке
     */
    private Point2D getEndingRadiusVector() {
        return getVector(getCenter(), getPoint(2));
    }

    /**
     * Возвращает отличную от исходной точку, которую можно редактировать
     *
     * @param i Номер исходной точки
     * @return Отличная от исходной точка
     */
    private Point2D getAnotherPoint(int i) {
        return i == 1 ? getPoint(2) : getPoint(1);
    }

    /**
     * Проверяет на наличие точки вблизи дуги с заданным углом. Используется начальный угол, описанный в this
     *
     * @param p     Точка на плоскости
     * @param angle Угол дуги
     * @return
     */
    private boolean containPoint(Point2D p, double angle) {
        Point2D radVec = getVector(getCenter(), p);
        return angle >= 0 && determinantIsPositive(radVec, getStartingRadiusVector())
                && !determinantIsPositive(radVec, getEndingRadiusVector())
                || angle < 0 && !determinantIsPositive(radVec, getStartingRadiusVector())
                && determinantIsPositive(radVec, getEndingRadiusVector());
    }
}
