package graphic_objects.meters;

import graphic_objects.GraphicObject;
import graphic_objects.figures.FigureInterface;
import graphic_objects.figures.Point2D;
import graphic_objects.figures.Point2D.PointType;
import graphic_objects.figures.Segment;

import java.awt.*;
import java.util.ArrayList;

import static core.DrawingArea.*;
import static core.geometry_algorithms.FigureAlgo.computeDistance;
import static core.geometry_algorithms.FigureAlgo.limitedVisibleSize;
import static core.geometry_algorithms.VectorAlgo.*;

/**
 * Created by Никита on 25.12.2014.
 */
@SuppressWarnings("ALL")
public class Ruler extends Meter {

    private Point2D moveVector = new Point2D();

    @Override
    protected boolean editPointWithMouse(int i, Point2D point) {
        return true;
    }

    @Override
    public boolean canBeFinished() {
        return computeDistance(points.get(0), points.get(1)) * getScreen().getScale() > MINIMUM_SIZE_FIGURE;
    }

    @Override
    public void onChange(double x, double y) {
        Point2D mouse = getScreen().pointToPlane(x, y);

        if (isBuilding()) {
            //При построении
            GraphicObject fig = null;
            Point2D near = null;
            try {
                fig = getSelection().getFigureByPoint(mouse);
                near = fig.getNearestPoint(mouse);
            } catch (NullPointerException npe) {
            }
            if (near != null) {
                fig.addMeter(this);
            } else
                near = mouse;

            owner.getPoints().set(1, near);
            refreshParameters();
        } else {
            //При изменении положения информации об измерении
            distance = computeDistance(new Segment(owner.getPoint(0), owner.getPoint(1)), mouse);
            moveVector = getChangedLengthVector(moveVector, distance);

            //Перебрасываем линейку в противоположную сторону, когда мышь ее пересекает
            double angle = angleBetweenVectors(getVector(owner.getPoint(0), mouse), moveVector);
            if (angle > 90 && angle < 270) {
                moveVector.setX(-moveVector.getX());
                moveVector.setY(-moveVector.getY());
            }
        }
        refreshPoints();
    }

    public void refreshParameters() {
        Point2D[] fp = new Point2D[2];
        double sign = Math.copySign(1, moveVector.getX());

        //Обмен значениями, чтобы первое значение всегда было левым
        if (owner.getPoint(1).getX() > owner.getPoint(0).getX()) {
            fp[0] = new Point2D(owner.getPoint(0));
            fp[1] = new Point2D(owner.getPoint(1));
        } else {
            fp[0] = new Point2D(owner.getPoint(1));
            fp[1] = new Point2D(owner.getPoint(0));
        }

        Point2D figureVector = getVector(fp[0], fp[1]);
        moveVector = getChangedLengthVector(getNormal(figureVector), distance);

        //Проверка на изменение знака, чтобы не допустить "прыгание" линейки
        if (sign != Math.copySign(sign, moveVector.getX())) {
            moveVector.setX(-moveVector.getX());
            moveVector.setY(-moveVector.getY());
        }

        text = String.format("%.2f", getLength()).replace(',', '.');
        angle = angleBetweenVectors(new Point2D(1, 0), figureVector);
    }

    @Override
    public void refreshPoints() {
        getPoint(0).setX(owner.getPoint(0).getX() + moveVector.getX());
        getPoint(0).setY(owner.getPoint(0).getY() + moveVector.getY());
        getPoint(1).setX(owner.getPoint(1).getX() + moveVector.getX());
        getPoint(1).setY(owner.getPoint(1).getY() + moveVector.getY());
    }

    @Override
    public Point2D getCenter() {
        return new Point2D((getPoint(0).getX() + getPoint(1).getX()) / 2,
                (getPoint(0).getY() + getPoint(1).getY()) / 2);
    }

    @Override
    protected void drawArrowLine(Graphics2D g) {
        Point2D p1 = getScreen().pointToScreen(getPoint(0));
        Point2D p2 = getScreen().pointToScreen(getPoint(1));
        g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
        Point2D vector = getChangedLengthVector(getVector(p1, p2),
                limitedVisibleSize(ARROWHEAD_LENGTH, owner.getVisibleSize()));

        vector = getRotatedVector(vector, -ARROWHEAD_ANGLE);
        g.drawLine((int) p1.getX(), (int) p1.getY(), (int) (p1.getX() + vector.getX()), (int) (p1.getY() + vector.getY()));
        g.drawLine((int) p2.getX(), (int) p2.getY(), (int) (p2.getX() - vector.getX()), (int) (p2.getY() - vector.getY()));

        vector = getRotatedVector(vector, 2 * ARROWHEAD_ANGLE);
        g.drawLine((int) p1.getX(), (int) p1.getY(), (int) (p1.getX() + vector.getX()), (int) (p1.getY() + vector.getY()));
        g.drawLine((int) p2.getX(), (int) p2.getY(), (int) (p2.getX() - vector.getX()), (int) (p2.getY() - vector.getY()));
    }

    @Override
    public void nextStage(int x, int y) {

    }

    @Override
    public boolean contain(Point2D p) {
        return computeDistance(new Segment(getPoint(0), getPoint(1)), p) < AROUND_SELECT_RADIUS / getScreen().getScale();
    }

    @Override
    public boolean equalTo(FigureInterface fi) {
        return false;
    }

    @Override
    public double getVisibleSize() {
        return getLength() * getScreen().getScale();
    }

    @Override
    public double getSizeMeasurement() {
        return 0;
    }

    @Override
    public double getLength() {
        return owner.getLength();
    }

    @Override
    public ArrayList<java.awt.geom.Point2D> getSegmentatedObject(int n) {
        return null;
    }

    protected void initFigure(Point2D p) {
        font = new Font("New Courier", Font.BOLD, FONT_SIZE);

        points.add(new Point2D(p, PointType.FORMATIVE, this));
        points.add(new Point2D(p, PointType.FORMATIVE, this));
        distance = 30 / getScreen().getScale();
        setType(GraphicObjectType.RULER);
        buildingGraphicObject = this;

        GraphicObject fig = null;
        Point2D near = null;
        try {
            fig = getSelection().getFigureByPoint(p);
            near = fig.getNearestPoint(p);
        } catch (NullPointerException npe) {
        }

        if (near != null) {
            owner = new Segment(near, new Point2D(near));
            fig.addMeter(this);
        } else {
            owner = new Segment(new Point2D(p), new Point2D(p));
        }
        measureType = MeasureType.BETWEEN_POINTS;
        getData().addMeter(this);
    }

    @Override
    protected void drawPrimitive(Graphics2D g) {
        //Отрисовка перпендикулярных вспомогательных линий
        Point2D p1 = getScreen().pointToScreen(owner.getPoint(0));
        Point2D p2 = getScreen().pointToScreen(owner.getPoint(1));
        Point2D dest1 = getScreen().pointToScreen(getPoint(0));
        Point2D dest2 = getScreen().pointToScreen(getPoint(1));

        drawArrowLine(g);

        g.drawLine((int) p1.getX(), (int) p1.getY(),
                (int) dest1.getX(), (int) dest1.getY());
        g.drawLine((int) p2.getX(), (int) p2.getY(),
                (int) dest2.getX(), (int) dest2.getY());

        Color vrc = g.getColor();
        g.setColor(TEXT_COLOR);
        font = font.deriveFont((float) limitedVisibleSize(FONT_SIZE, getVisibleSize()));
        g.setFont(font);
        if (font.getSize() > MINIMUM_FONT_SIZE) {
            drawText(g, dest1, dest2, text);
        }
        g.setColor(vrc);
    }
}