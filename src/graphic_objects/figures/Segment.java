package graphic_objects.figures;

import core.Screen;
import graphic_objects.GraphicObject;
import graphic_objects.figures.Point2D.PointType;
import graphic_objects.figures.properties.SegmentProperties;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import static core.DrawingArea.getData;
import static core.DrawingArea.getScreen;
import static core.geometry_algorithms.FigureAlgo.computeDistance;
import static core.geometry_algorithms.PointRounding.roundedPoint;
import static ui.MainWindow.getDrawingArea;

public class Segment extends GraphicObject implements FigureInterface {

    public Segment() {
    }

    public Segment(Point2D p1, Point2D p2) {
        points.add(p1);
        points.add(p2);
        points.add(new Point2D((points.get(0).getX() + points.get(1).getX()) / 2,
                (points.get(0).getY() + points.get(1).getY()) / 2));
        setType(GraphicObjectType.SEGMENT);
    }

    @Override
    public Segment clone() {
        Segment obj = (Segment) super.clone();
        List<Point2D> newPoints = new ArrayList<>();
        for (Point2D p : obj.getPoints())
            newPoints.add(new Point2D(p, p.getType(), obj));
        obj.setPoints(newPoints);
        obj.setType(getType());
        obj.setProperties(new SegmentProperties(obj));
        return obj;
    }

    @Override
    public Point2D getCenter() {
        return getPoint(2);
    }

    @Override
    public double getVisibleSize() {
        return getLength() * getScreen().getScale();
    }

    @Override
    public double getSizeMeasurement() {
        return getLength();
    }

    @Override
    public double getLength() {
        return computeDistance(points.get(0), points.get(1));
    }

    @Override
    public ArrayList<java.awt.geom.Point2D> getSegmentatedObject(int n) {
        if (n < 2)
            return null;

        ArrayList<java.awt.geom.Point2D> list = new ArrayList<>();
        double x = points.get(0).getX();
        double y = points.get(0).getY();
        double dx = (points.get(1).getX() - x) / n;
        double dy = (points.get(1).getY() - y) / n;

        list.add(new java.awt.geom.Point2D.Double(x, y));
        for(int i = 0; i < n-1; i++){
            x += dx;
            y += dy;
            list.add(new java.awt.geom.Point2D.Double(x, y));
        }

        return list;
    }

    @Override
    public boolean contain(Point2D p) {
        return computeDistance(this, p) < AROUND_SELECT_RADIUS / getScreen().getScale();
    }

    @Override
    public boolean equalTo(FigureInterface fi) {
        Segment s = null;
        try {
            s = (Segment) fi;
        } catch (ClassCastException cce) {
            cce.printStackTrace();
            return false;
        }

        return getPoint(0).equalTo(s.getPoint(0)) || getPoint(0).equalTo(s.getPoint(1))
                && getPoint(1).equalTo(s.getPoint(0)) || getPoint(1).equalTo(s.getPoint(1));
    }

    @Override
    public boolean canBeFinished() {
        return computeDistance(points.get(0), points.get(1)) * getScreen().getScale() > MINIMUM_SIZE_FIGURE;
    }

    @Override
    public void onChange(double x, double y) {
        Point2D p = roundedPoint(x, y);
        getPoint(1).setX(p.getX());
        getPoint(1).setY(p.getY());
        refreshPoints();
    }

    @Override
    public void refreshPoints() {
        points.get(2).setX((points.get(0).getX() + points.get(1).getX()) / 2);
        points.get(2).setY((points.get(0).getY() + points.get(1).getY()) / 2);
        getDrawingArea().repaint();
    }

    @Override
    public void nextStage(int x, int y) {

    }

    @Override
    protected boolean editPointWithMouse(int i, Point2D p) {
        if (i == 0 || i == 1) {
            Point2D move = new Point2D(p.getX() - getPoint(i).getX(), p.getY() - getPoint(i).getY());
            getPoint(i).move(move.getX(), move.getY());

            if (!canBeFinished()) {
                getPoint(i).move(-move.getX(), -move.getY());
                return false;
            }
            refreshPoints();
            return true;
        }
        return false;
    }

    @Override
    protected void drawPrimitive(Graphics2D g) {
        Screen sc = getScreen();
        g.draw(new Line2D.Double(sc.XToScreen(points.get(0).getX()),
                sc.YToScreen(points.get(0).getY()),
                sc.XToScreen(points.get(1).getX()),
                sc.YToScreen(points.get(1).getY())));
    }

    @Override
    protected void initFigure(Point2D p) {
        points.add(new Point2D(p, PointType.FORMATIVE, this));
        points.add(new Point2D(p, PointType.FORMATIVE, this)); // в дальнейшем будем редактировать ее
        points.add(new Point2D(p, PointType.ANCHOR, this));
        setType(GraphicObjectType.SEGMENT);
        setProperties(new SegmentProperties(this));
        getData().addFigure(this);
    }

    /**
     * Устанавливает сегмент
     *
     * @param p1 начальная точка
     * @param p2 конечная точка
     */
    public void setSegment(Point2D p1, Point2D p2) {
        initFigure(p1);
        getPoint(1).setX(p2.getX());
        getPoint(1).setY(p2.getY());
        refreshPoints();
    }

}