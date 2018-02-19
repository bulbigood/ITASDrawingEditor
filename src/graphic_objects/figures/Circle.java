package graphic_objects.figures;

import core.Screen;
import core.geometry_algorithms.VectorAlgo;
import graphic_objects.GraphicObject;
import graphic_objects.figures.Point2D.PointType;
import graphic_objects.figures.properties.CircleProperties;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import static core.DrawingArea.getData;
import static core.DrawingArea.getScreen;
import static core.geometry_algorithms.FigureAlgo.computeDistance;
import static core.geometry_algorithms.FigureAlgo.equal;
import static core.geometry_algorithms.PointRounding.roundedPoint;
import static ui.MainWindow.getDrawingArea;

public class Circle extends GraphicObject implements FigureInterface {
    private double radius;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double rad) {
        radius = rad;
        refreshPoints();
    }

    @Override
    public Circle clone() {
        Circle obj = (Circle) super.clone();
        java.util.List<Point2D> newPoints = new ArrayList<>();
        for (Point2D p : obj.getPoints())
            newPoints.add(new Point2D(p, p.getType(), obj));
        obj.setPoints(newPoints);
        obj.setType(getType());
        obj.setProperties(new CircleProperties(obj));
        return obj;
    }

    @Override
    public Point2D getCenter() {
        return getPoint(0);
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
        return radius * 2 * Math.PI;
    }

    @Override
    public ArrayList<java.awt.geom.Point2D> getSegmentatedObject(int n) {
        if (n < 4)
            return null;

        ArrayList<java.awt.geom.Point2D> list = new ArrayList<>();
        double x = points.get(0).getX();
        double y = points.get(0).getY() + radius;
        Point2D radiusVector = new Point2D(0, radius);
        double d = 360.0 / (n - 1);

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
    public boolean contain(Point2D p) {
        boolean b = false;
        double vr = computeDistance(points.get(0), p);

        if ((vr < radius + AROUND_SELECT_RADIUS / getScreen().getScale())
                && (vr > radius - AROUND_SELECT_RADIUS / getScreen().getScale()))
            b = true;

        return b;
    }

    @Override
    public boolean equalTo(FigureInterface fi) {
        Circle s = null;
        try {
            s = (Circle) fi;
        } catch (ClassCastException cce) {
            cce.printStackTrace();
            return false;
        }

        return equal(radius, s.getRadius()) && getCenter().equalTo(s.getCenter());
    }

    @Override
    public boolean canBeFinished() {
        return radius * getScreen().getScale() > MINIMUM_SIZE_FIGURE;
    }

    @Override
    public void onChange(double x, double y) {
        setRadius(computeDistance(roundedPoint(x, y), getPoint(0)));
    }

    @Override
    public void refreshPoints() {
        points.get(1).setX(getCenter().getX());
        points.get(2).setX(getCenter().getX());
        points.get(3).setX(getCenter().getX() - radius);
        points.get(4).setX(getCenter().getX() + radius);
        points.get(1).setY(getCenter().getY() - radius);
        points.get(2).setY(getCenter().getY() + radius);
        points.get(3).setY(getCenter().getY());
        points.get(4).setY(getCenter().getY());
        getDrawingArea().repaint();
    }

    @Override
    public void nextStage(int x, int y) {

    }

    @Override
    protected boolean editPointWithMouse(int i, Point2D point) {
        if (i >= 0 && i <= 4) {
            Point2D vector = new Point2D(point.getX() - getPoint(i).getX(), point.getY() - getPoint(i).getY());
            switch (i) {
                case 1:
                    getPoint(1).move(0, vector.getY());
                    getPoint(3).move(vector.getY() / 2, vector.getY() / 2);
                    getPoint(4).move(-vector.getY() / 2, vector.getY() / 2);
                    break;
                case 2:
                    getPoint(2).move(0, vector.getY());
                    getPoint(3).move(-vector.getY() / 2, vector.getY() / 2);
                    getPoint(4).move(vector.getY() / 2, vector.getY() / 2);
                    break;
                case 3:
                    getPoint(3).move(vector.getX(), 0);
                    getPoint(1).move(vector.getX() / 2, vector.getX() / 2);
                    getPoint(2).move(vector.getX() / 2, -vector.getX() / 2);
                    break;
                case 4:
                    getPoint(4).move(vector.getX(), 0);
                    getPoint(1).move(vector.getX() / 2, -vector.getX() / 2);
                    getPoint(2).move(vector.getX() / 2, vector.getX() / 2);
                    break;
            }

            boolean b = canBeFinished();

            double radvr = radius;
            radius = Math.abs((getPoint(2).getY() - getPoint(1).getY()) / 2);

            if (b && !canBeFinished()) {
                radius = radvr;
                refreshPoints();
                return false;
            }

            getCenter().setX((getPoint(4).getX() + getPoint(3).getX()) / 2);
            getCenter().setY((getPoint(2).getY() + getPoint(1).getY()) / 2);

            getDrawingArea().repaint();
            return true;
        }
        return false;
    }

    @Override
    protected void drawPrimitive(Graphics2D g) {
        Screen sc = getScreen();
        Point2D c = getCenter();
        g.draw(new Ellipse2D.Double(sc.XToScreen(c.getX() - radius), sc.YToScreen(c.getY() + radius),
                radius * 2 * sc.getScale(), radius * 2 * sc.getScale()));
    }

    @Override
    protected void initFigure(Point2D p) {
        MINIMUM_SIZE_FIGURE = 10;
        points.add(new Point2D(p, PointType.AUXILIARY, this));
        points.add(new Point2D(p, PointType.FORMATIVE, this));
        points.add(new Point2D(p, PointType.FORMATIVE, this));
        points.add(new Point2D(p, PointType.FORMATIVE, this));
        points.add(new Point2D(p, PointType.FORMATIVE, this));
        setType(GraphicObjectType.CIRCLE);
        setProperties(new CircleProperties(this));
        getData().addFigure(this);
    }

    /**
     * Устанавливает окружность
     *
     * @param c точка центра
     * @param r радиус
     */
    public void setCircle(Point2D c, double r) {
        initFigure(c);
        setRadius(r);
        refreshPoints();
    }

}