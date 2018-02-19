package graphic_objects.meters;

import graphic_objects.GraphicObject;
import graphic_objects.figures.Point2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static core.geometry_algorithms.VectorAlgo.*;

/**
 * Created by Никита on 25.12.2014.
 */
@SuppressWarnings("ALL")
public abstract class Meter extends GraphicObject {

    //Минимальное расстояние от измеряемой фигуры до выводимой записи
    protected static final Color TEXT_COLOR = Color.black;
    protected static BasicStroke STROKE = new BasicStroke(1);
    protected static Color COLOR = Color.black;
    protected static Color SELECTED_COLOR = Color.green;
    protected static Color BUILDING_COLOR = Color.black;

    int MINIMUM_FONT_SIZE = 9;
    int FONT_SIZE = 16;
    int ARROWHEAD_LENGTH = 10;
    int ARROWHEAD_ANGLE = 15;

    enum MeasureType {
        BETWEEN_POINTS, FIGURE
    }

    protected static Font font;
    protected static MeasureType measureType;
    protected GraphicObject owner; //Измеренный объект
    protected double angle; //Угол в градусах, под которым выводится текст
    protected double distance; //Расстояние от owner до места вывода информации
    protected String text;

    public MeasureType getMeasureType() {
        return measureType;
    }

    public GraphicObject getOwner() {
        return owner;
    }

    @Override
    public void draw(Graphics2D g) {
        Color vrc = g.getColor();
        Stroke vrs = g.getStroke();
        Font vrf = g.getFont();

        g.setStroke(STROKE);
        if (isSelected())
            g.setColor(SELECTED_COLOR);
        else
            g.setColor(COLOR);

        drawPrimitive(g);

        g.setColor(vrc);
        g.setStroke(vrs);
        g.setFont(vrf);
    }

    public static void refreshMeters(ArrayList<GraphicObject> arr) {
        Set<Meter> set = new HashSet();

        for (GraphicObject ins : arr)
            set.addAll(ins.getMeters());
        for (Meter m : set) {
            m.refreshParameters();
            m.refreshPoints();
        }
    }

    public abstract void refreshParameters();

    /**
     * Рисует отрезок со стрелками в обе стороны на месте вывода информации
     *
     * @param g компонент графики
     */
    protected abstract void drawArrowLine(Graphics2D g);

    /**
     * Пишет строку на месте вывода информации
     *
     * @param g Компонент графики
     * @param s Строка
     */
    protected void drawText(Graphics2D g, Point2D p1, Point2D p2, String s) {
        if (p2.getX() < p1.getX()) {
            Point2D vr = new Point2D(p1);
            p1 = p2;
            p2 = vr;
        }

        Point2D vector = getVector(p1, p2);
        //Вычисление ширины строки в пикселах. При этом должен использоваться моноширинный тонкий шрифт с соотношением 1:2
        int stringSize = g.getFont().getSize() * s.length() / 2;
        //Вектор к точке начала вывода строки
        Point2D textStartVector = getChangedLengthVector(vector, (Math.hypot(vector.getX(), vector.getY()) - stringSize) / 2);
        double angle = -angleBetweenVectors(new Point2D(1, 0), vector);

        float x = (float) (p1.getX() + textStartVector.getX());
        float y = (float) (p1.getY() + textStartVector.getY());

        AffineTransform orig = g.getTransform();
        g.translate(x, y);
        g.rotate(Math.toRadians(angle));
        g.translate(-x, -y);

        //Вывод повернутой строки
        g.drawString(s, x, y);
        g.setTransform(orig);
    }
}