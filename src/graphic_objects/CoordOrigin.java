package graphic_objects;

import graphic_objects.figures.FigureInterface;
import graphic_objects.figures.Point2D;
import graphic_objects.figures.Segment;
import graphic_objects.meters.Meter;

import java.awt.*;
import java.util.ArrayList;

import static core.DrawingArea.getScreen;
import static core.geometry_algorithms.FigureAlgo.computeDistance;

/**
 * Created by Никита on 12.11.2017.
 */
public class CoordOrigin extends Meter {
    private Point2D point;
    private Color color;
    private Stroke stroke;

    public CoordOrigin(int x, int y, Color color){
        point = new Point2D(x, y);
        this.color = color;
        stroke = new BasicStroke(1);
    }

    @Override
    public Point2D getCenter() {
        return null;
    }

    @Override
    public double getVisibleSize() {
        return 0;
    }

    @Override
    public double getSizeMeasurement() {
        return 0;
    }

    @Override
    public double getLength() {
        return 0;
    }

    @Override
    public ArrayList<java.awt.geom.Point2D> getSegmentatedObject(int n) {
        return null;
    }

    public void draw(Graphics2D g){
        Color vrc = g.getColor();
        Stroke vrs = g.getStroke();
        g.setColor(color);
        g.setStroke(stroke);

        Point OXY = new Point((int) getScreen().XToScreen(point.getX()), (int) getScreen().YToScreen(point.getY()));
        g.drawLine(OXY.x, OXY.y, OXY.x, OXY.y - 35);
        g.drawLine(OXY.x, OXY.y - 35, OXY.x - 5, OXY.y - 25);
        g.drawLine(OXY.x, OXY.y - 35, OXY.x + 5, OXY.y - 25);
        g.drawString("Y", OXY.x - 15, OXY.y - 25);

        g.drawLine(OXY.x, OXY.y, OXY.x + 35, OXY.y);
        g.drawLine(OXY.x + 35, OXY.y, OXY.x + 25, OXY.y - 5);
        g.drawLine(OXY.x + 35, OXY.y, OXY.x + 25, OXY.y + 5);
        g.drawString("X", OXY.x + 27, OXY.y + 20);

        g.setColor(vrc);
        g.setStroke(vrs);
    }

    @Override
    public boolean canBeFinished() {
        return false;
    }

    @Override
    public void onChange(double x, double y) {
        setCoords(getScreen().XToPlane(x), getScreen().YToPlane(y));
    }

    @Override
    public void refreshPoints() {

    }

    @Override
    public void nextStage(int x, int y) {

    }

    @Override
    protected boolean editPointWithMouse(int i, Point2D p) {
        return false;
    }

    @Override
    protected void initFigure(Point2D p) {

    }

    @Override
    protected void drawPrimitive(Graphics2D g) {

    }

    @Override
    public void refreshParameters() {

    }

    @Override
    protected void drawArrowLine(Graphics2D g) {

    }

    public Point2D getCoords(){
        return new Point2D(point.getX(), point.getY());
    }

    public Color getColor(){
        return new Color(color.getRGB());
    }

    public void setCoords(double x, double y){
        point.setX(x);
        point.setY(y);
    }

    public void setColor(int r, int g, int b){
        color = new Color(r, g, b);
    }

    public boolean contain(Point2D p) {
        Point2D p_y = new Point2D(point.getX(), point.getY() + 35 / getScreen().getScale());
        Point2D p_x = new Point2D(point.getX() + 35 / getScreen().getScale(), point.getY());

        Segment a = new Segment(point, p_x);
        Segment b = new Segment(point, p_y);

        return Math.min(computeDistance(a, p), computeDistance(b, p)) < AROUND_SELECT_RADIUS / getScreen().getScale();
    }

    @Override
    public boolean equalTo(FigureInterface fi) {
        return false;
    }
}
