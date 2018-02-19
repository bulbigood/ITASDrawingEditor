package graphic_objects.meters;

import graphic_objects.figures.FigureInterface;
import graphic_objects.figures.Point2D;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Никита on 15.01.2015.
 */
public class Protractor extends Meter {
    @Override
    public void refreshParameters() {

    }

    @Override
    protected void drawArrowLine(Graphics2D g) {

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

    @Override
    public boolean canBeFinished() {
        return false;
    }

    @Override
    public void onChange(double x, double y) {

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
    public boolean contain(Point2D p) {
        return false;
    }

    @Override
    public boolean equalTo(FigureInterface fi) {
        return false;
    }
}