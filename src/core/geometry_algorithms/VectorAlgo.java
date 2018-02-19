package core.geometry_algorithms;

import graphic_objects.figures.Point2D;
import graphic_objects.figures.Segment;

import static java.lang.Math.*;

/**
 * Created by Никита on 28.12.2014.
 */
public class VectorAlgo {

    /**
     * Вычисляет вектор между двумя точками
     *
     * @param from Начальная точка
     * @param to   Конечная точка
     * @return Вектор
     */
    public static Point2D getVector(Point2D from, Point2D to) {
        return new Point2D(to.getX() - from.getX(), to.getY() - from.getY());
    }

    /**
     * Вычисляет вектор из отрезка
     *
     * @param line Отрезок
     * @return Вектор
     */
    public static Point2D getVector(Segment line) {
        return getVector(line.getPoint(1), line.getPoint(0));
    }

    /**
     * @param a Вектор 1
     * @param b Вектор 2
     * @return Скалярное произведение векторов
     */
    public static double scalarProduct(Point2D a, Point2D b) {
        return a.getX() * b.getX() + a.getY() * b.getY();
    }

    /**
     * @param a Вектор
     * @return Модуль(длина) вектора
     */
    public static double unitVector(Point2D a) {
        return sqrt(a.getX() * a.getX() + a.getY() * a.getY());
    }

    /**
     * @param p Вектор
     * @return Нормаль вектора
     */
    public static Point2D getNormal(Point2D p) {
        return new Point2D(p.getY(), -p.getX());
    }

    /**
     * Получение определителя матрицы двух векторов. Результат положительный, когда угол < 180 градусов
     *
     * @param v1 Первая точка дуги, задающая startingAngle
     * @param v2 Вторая точка дуги, задающая angularExtent
     * @return Знак результата векторного перемножения
     */
    public static boolean determinantIsPositive(Point2D v1, Point2D v2) {
        return v2.getX() * v1.getY() - v2.getY() * v1.getX() >= 0;
    }

    /**
     * Поворачивает произвольный вектор на заданный угол по часовой стрелке
     *
     * @param vector  Произвольный вектор
     * @param degrees Заданный угол в градусах
     * @return Повернутый вектор
     */
    public static Point2D getRotatedVector(Point2D vector, double degrees) {
        double rad = toRadians(degrees);
        return new Point2D(vector.getX() * cos(rad) + vector.getY() * sin(rad),
                -vector.getX() * sin(rad) + vector.getY() * cos(rad));
    }

    /**
     * Считает угол между двумя векторами от 0 до 360
     *
     * @param v1 Первая точка дуги, задающая startingAngle
     * @param v2 Вторая точка дуги, задающая angularExtent
     * @return Угол в градусах
     */
    public static double angleBetweenVectors(Point2D v1, Point2D v2) {
        if(unitVector(v1) * unitVector(v2) == 0)
            return 0;
        double angle = toDegrees(acos(scalarProduct(v1, v2) / (unitVector(v1) * unitVector(v2))));
        if (determinantIsPositive(v1, v2))
            return angle % 360;
        else
            return (360 - angle) % 360;
    }

    /**
     * Высчитывает вектор заданной длины в том же направлении, что и vector
     *
     * @param vector Направляющий вектор
     * @param length Длина вектора
     * @return Вектор с заданной длиной
     */
    public static Point2D getChangedLengthVector(Point2D vector, double length) {
        double x = 0, y = 0;
        if (vector.getY() != 0) {
            double k = vector.getX() / vector.getY();
            y = copySign(length, vector.getY()) / sqrt(k * k + 1);
            x = k * y;
        } else {
            x = copySign(length, vector.getX());
        }
        return new Point2D(x, y);
    }
}
