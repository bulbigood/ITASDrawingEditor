package core.geometry_algorithms;

import core.Settings;
import graphic_objects.figures.Point2D;
import graphic_objects.figures.Segment;

import static core.geometry_algorithms.VectorAlgo.*;
import static java.lang.Math.*;

/**
 * Created by Никита on 28.12.2014.
 */
public class FigureAlgo {

    /**
     * Находит расстояние между точкой и отрезком
     *
     * @param segment Отрезок
     * @param point   Точка
     * @return Расстояние
     */
    public static double computeDistance(Segment segment, Point2D point) {
        Point2D[] p = new Point2D[3];
        segment.getPoints().toArray(p);
        Point2D vector = getVector(p[0], p[1]);
        Point2D near = getNearestPointOn(vector, p[0], point);
        double x = near.getX();
        double y = near.getY();

        Point2D v1 = new Point2D(x - p[0].getX(), y - p[0].getY());

        if (equal(v1.getX(), 0))
            v1.setX(0);
        if (equal(v1.getY(), 0))
            v1.setY(0);

        if (v1.getX() * vector.getY() - v1.getY() * vector.getX() < pow(10, -4)) {
            if ((equal(x, p[0].getX()) || x > p[0].getX() && x < p[1].getX() || x < p[0].getX() && x > p[1].getX()) &&
                    (equal(y, p[0].getY()) || y > p[0].getY() && y < p[1].getY() || y < p[0].getY() && y > p[1].getY())) {

                return computeDistance(new Point2D(x, y), point);
            }
        }
        return min(computeDistance(p[0], point), computeDistance(p[1], point));
    }

    /**
     * Находит расстояние между отрезками
     *
     * @param s1 Отрезок 1
     * @param s2 Отрезок 2
     * @return Расстояние
     */
    public static double computeDistance(Segment s1, Segment s2) {
        Point2D[] p = new Point2D[4];
        p[0] = s1.getPoint(0);
        p[1] = s1.getPoint(1);
        p[2] = s2.getPoint(0);
        p[3] = s2.getPoint(1);

        double d;
        Point2D v1 = getVector(s1);
        Point2D v2 = getVector(s2);
        Point2D n1 = getNormal(v1);
        Point2D n2 = getNormal(v2);

        d = Math.min(computeDistance(p[0], p[2]), computeDistance(p[0], p[3]));
        d = Math.min(d, computeDistance(p[1], p[3]));
        d = Math.min(d, computeDistance(p[1], p[2]));

        double c1 = -scalarProduct(n1, p[0]);
        double c2 = -scalarProduct(n2, p[3]);

        double delta = n1.getX() * n2.getY() - n2.getX() * n1.getY();
        double aX = (-c1 * n2.getY() + c2 * n1.getY()) / delta;
        double aY = (-c2 * n1.getX() + c1 * n2.getX()) / delta;
        if (((aX <= p[2].getX() && aX >= p[3].getX()) || (aX <= p[3].getX() && aX >= p[2].getX()))
                && ((aX <= p[0].getX() && aX >= p[1].getX()) || (aX <= p[1].getX() && aX >= p[0].getX()))) {
            if (((aY <= p[2].getY() && aY >= p[3].getY()) || (aY <= p[3].getY() && aY >= p[2].getY()))
                    && ((aY <= p[0].getY() && aY >= p[1].getY()) || (aY <= p[1].getY() && aY >= p[0].getY()))) {
                d = 0;
            }
        }

        double k0 = -scalarProduct(v1, p[2]);
        double k1 = -scalarProduct(v1, p[3]);
        double k2 = -scalarProduct(v2, p[0]);
        double k3 = -scalarProduct(v2, p[1]);
        double delta1 = v1.getX() * n1.getY() - v1.getY() * n1.getX();
        double delta2 = v2.getX() * n2.getY() - v2.getY() * n2.getX();
        Point2D[] xPoints = new Point2D[4];
        xPoints[0] = new Point2D((-k2 * n2.getY() + v2.getY() * c2) / delta2, (-c2 * v2.getX() + n2.getX() * k2) / delta2);
        xPoints[1] = new Point2D((-k3 * n2.getY() + v2.getY() * c2) / delta2, (-c2 * v2.getX() + n2.getX() * k3) / delta2);
        xPoints[2] = new Point2D((-k0 * n1.getY() + v1.getY() * c1) / delta1, (-c1 * v1.getX() + n1.getX() * k0) / delta1);
        xPoints[3] = new Point2D((-k1 * n1.getY() + v1.getY() * c1) / delta1, (-c1 * v1.getX() + n1.getX() * k1) / delta1);


        for (int i = 0; i < 2; i++) {
            if ((xPoints[i].getX() <= p[2].getX() && xPoints[i].getX() >= p[3].getX())
                    || (xPoints[i].getX() <= p[3].getX() && xPoints[i].getX() >= p[2].getX())) {
                if ((xPoints[i].getY() <= p[2].getY() && xPoints[i].getY() >= p[3].getY())
                        || (xPoints[i].getY() <= p[3].getY() && xPoints[i].getY() >= p[2].getY())) {
                    d = Math.min(d, computeDistance(xPoints[i], p[i]));
                }
            }
        }
        for (int i = 2; i < 4; i++) {
            if ((xPoints[i].getX() <= p[0].getX() && xPoints[i].getX() >= p[1].getX())
                    || (xPoints[i].getX() <= p[1].getX() && xPoints[i].getX() >= p[0].getX())) {
                if ((xPoints[i].getY() <= p[0].getY() && xPoints[i].getY() >= p[1].getY())
                        || (xPoints[i].getY() <= p[1].getY() && xPoints[i].getY() >= p[0].getY())) {
                    d = Math.min(d,computeDistance(xPoints[i], p[i]));
                }
            }
        }

        if (abs(d) <= pow(10, -6)) {
            d = 0.0;
        }

        return d;
    }

    /**
     * Возвращает расстояние между двумя точками
     *
     * @param p1 первая точка
     * @param p2 вторая точка
     * @return расстояние между точками
     */
    public static double computeDistance(Point2D p1, Point2D p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        return sqrt(dx * dx + dy * dy);
    }

    /**
     * Вычисляет ближайшую точку к заданной, которая лежит на прямой
     *
     * @param vector Вектор, задающий прямую
     * @param init   Начальная точка вектора
     * @param dest   Проецируемая точка
     * @return Проекция точки dest на прямую
     */
    public static Point2D getNearestPointOn(Point2D vector, Point2D init, Point2D dest) {
        if (unitVector(vector) == 0)
            return dest;

        Point2D normal = getNormal(vector);
        double Dvec = -(vector.getX() * dest.getX() + vector.getY() * dest.getY());
        double Dnor = -(normal.getX() * init.getX() + normal.getY() * init.getY());
        double x = -(Dvec * normal.getY() - Dnor * vector.getY()) / (vector.getX() * normal.getY() - normal.getX() * vector.getY());
        double y = (Dvec * normal.getX() - Dnor * vector.getX()) / (vector.getX() * normal.getY() - normal.getX() * vector.getY());
        return new Point2D(x, y);
    }

    /**
     * Вычисляет точку пересечения прямых, заданных точками
     *
     * @param points Массив из 4 точек: 2, задающие первую прямую, и 2, задающие вторую прямую
     * @return Точку пересечения прямых
     */
    public static Point2D getIntersection(Point2D[] points) {
        //Проверка на случай, когда конечная точка у векторов одна и та же
        if (!(new Point2D(points[0].getX() + points[1].getX(), points[0].getY() + points[1].getY()).equalTo(
                new Point2D(points[2].getX() + points[3].getX(), points[2].getY() + points[3].getY())))) {
            double[] a = new double[2];
            double[] b = new double[2];
            double[] c = new double[2];
            for (int i = 0; i < 2; i++) {
                //Вычисление коэффициентов A, B и C общего уравнения прямой
                a[i] = points[2 * i + 1].getY() - points[2 * i].getY();
                b[i] = points[2 * i].getX() - points[2 * i + 1].getX();
                c[i] = -(a[i] * points[2 * i].getX() + b[i] * points[2 * i].getY());
            }

            double delta = a[0] * b[1] - b[0] * a[1];
            if (delta != 0) {
                Point2D cross = new Point2D();
                cross.setX((-c[0] * b[1] + c[1] * b[0]) / delta);
                cross.setY((-c[1] * a[0] + c[0] * a[1]) / delta);
                return cross;
            } else
                return null;
        }
        return new Point2D((points[0].getX() + points[2].getX()) / 2, (points[0].getY() + points[2].getY()) / 2);
    }

    /**
     * Вычисляет размер графического примитива (толщину линий, точек и т.п.) для отображения на экране
     *
     * @param constant    Минимальный размер графического примитива
     * @param visibleSize Размер (в пикселах) некоторого объекта, от которого зависит отображение граф. примитивов
     * @return Ограниченный размер примитива
     */
    public static double limitedVisibleSize(double constant, double visibleSize) {
        return min(constant, constant * sqrt(visibleSize) / 10);
    }

    public static boolean equal(double a, double b) {
        a *= Settings.ACCURACY;
        a = Math.rint(a) / Settings.ACCURACY;
        b *= Settings.ACCURACY;
        b = Math.rint(b) / Settings.ACCURACY;
        return a == b;
    }
}
