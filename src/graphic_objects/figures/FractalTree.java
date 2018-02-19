package graphic_objects.figures;

import core.Screen;
import core.geometry_algorithms.VectorAlgo;
import graphic_objects.GraphicObject;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Random;

import static core.DrawingArea.getData;
import static core.DrawingArea.getScreen;
import static core.geometry_algorithms.FigureAlgo.computeDistance;

/**
 * Created by Никита on 06.12.2017.
 */
public class FractalTree extends GraphicObject implements FigureInterface {

    public static double branchAngle = 25;
    public static double branchDistance = 20;
    public static double iterations = 5;

    private Random rand = new Random();

    @Override
    public boolean contain(Point2D p) {
        return false;
    }

    @Override
    public boolean equalTo(FigureInterface fi) {
        return false;
    }

    @Override
    public Point2D getCenter() {
        return points.get(0);
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
        return true;
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
        MINIMUM_SIZE_FIGURE = 10;

        nextBranch(new Point2D(p, Point2D.PointType.FORMATIVE, this), 0, 0);

        setType(GraphicObjectType.TREE);
        getData().addFigure(this);
    }

    private Point2D nextBranch(Point2D branch, double angle, int iter){
        double err1 = rand.nextDouble() - 0.5;
        double err2 = rand.nextDouble() - 0.5;
        double err3 = rand.nextDouble() - 0.5;

        Point2D vec = VectorAlgo.getRotatedVector(new Point2D(0, branchDistance*(1 + err3)), angle);
        Point2D newBranch = new Point2D(branch.getX() + vec.getX(), branch.getY() + vec.getY(), Point2D.PointType.FORMATIVE, this);
        points.add(branch);
        points.add(newBranch);
        if(iter < iterations) {
            boolean branch1 = rand.nextDouble() - 0.2 > 0;
            boolean branch2 = rand.nextDouble() - 0.2 > 0;
            if(branch1)
                points.add(nextBranch(newBranch, angle + branchAngle*(1 + err1), iter + 1));
            if(branch2)
                points.add(nextBranch(newBranch, angle - branchAngle*(1 + err2), iter + 1));
            if(!branch1 && !branch2)
                points.add(nextBranch(newBranch, angle, iter + 1));
        }
        return newBranch;
    }

    @Override
    protected void drawPrimitive(Graphics2D g) {
        Screen sc = getScreen();
        Path2D path = new Path2D.Double();
        for(int i = 0; i < points.size() - 1; i++){
            path.moveTo(sc.XToScreen(points.get(i).getX()), sc.YToScreen(points.get(i).getY()));
            path.lineTo(sc.XToScreen(points.get(i+1).getX()), sc.YToScreen(points.get(i+1).getY()));
        }
        g.draw(path);
    }
}
