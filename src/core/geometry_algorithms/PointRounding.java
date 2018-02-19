package core.geometry_algorithms;

import core.Grid;
import core.KeyListener;
import core.Selection;
import core.Settings;
import graphic_objects.GraphicObject;
import graphic_objects.figures.Point2D;

import java.util.ArrayList;
import java.util.List;

import static core.DrawingArea.*;
import static core.geometry_algorithms.FigureAlgo.computeDistance;
import static core.geometry_algorithms.VectorAlgo.getVector;
import static core.geometry_algorithms.VectorAlgo.unitVector;
import static java.lang.Math.round;

/**
 * Created by Никит on 05.12.2014.
 */
public class PointRounding {

    private static int selectedPointsNumber;

    /**
     * Округляет к ближайшей точке фигуры, или в противном случае округляет до пересечения прямых на сетке
     *
     * @param x X координата точки на панели рисования
     * @param y Y координата точки на панели рисования
     * @return Округленную точку
     */
    public static Point2D roundedPoint(double x, double y) {
        Point2D projectedPoint = getScreen().pointToPlane(x, y);
        if (!KeyListener.isShiftDown()) {
            if (!roundToFigurePoint(projectedPoint))
                roundToGridPoint(projectedPoint);
        }
        return projectedPoint;
    }

    /**
     * Округляет точку на панели до точки другой фигуры
     *
     * @param x X координата точки на панели рисования
     * @param y Y координата точки на панели рисования
     * @return Точку, округленную к точке фигуры
     */
    public static Point2D getRoundToFigurePoint(double x, double y) {
        Point2D projectedPoint = getScreen().pointToPlane(x, y);
        for (GraphicObject f : getData().getFigures())
            if (!f.isBuilding() && !f.isEditing() && f.roundToNearestPoint(projectedPoint))
                break;

        return projectedPoint;
    }

    /**
     * Округляет точку панели рисования до ближайшего пересечения линий на сетке
     *
     * @param x X координата точки на панели рисования
     * @param y Y координата точки на панели рисования
     * @return Округленную к сетке точку
     */
    public static Point2D getRoundToGridPoint(double x, double y) {
        Point2D projectedPoint = getScreen().pointToPlane(x, y);

        //Если рядом нет точки других фигур, то округляем точку к сетке
        if (Settings.ROUND_TO_GRID) {
            double cell = Grid.getSmallestCellSize();
            Point2D rounded = new Point2D(round(projectedPoint.getX() / cell) * cell,
                    (round(projectedPoint.getY() / cell)) * cell);

            if (computeDistance(projectedPoint, rounded) < cell)
                projectedPoint = rounded;
        }
        return projectedPoint;
    }

    /**
     * Округлить заданную точку к ближайшей точке, принадлежащей фигуре
     *
     * @param p Точка на плоскости
     * @return Точка округлилась
     */
    public static boolean roundToFigurePoint(Point2D p) {
        if (Settings.ROUND_TO_FIGURES_POINTS) {
            for (GraphicObject f : getData().getFigures())
                if (!f.isBuilding() && !f.isEditing() && f.roundToNearestPoint(p))
                    return true;
        }
        return false;
    }

    /**
     * Округляет заданную точку до ближайшего пересечения линий на сетке
     *
     * @param p Точка на плоскости
     * @return Точка округлилась
     */
    public static boolean roundToGridPoint(Point2D p) {
        if (Settings.ROUND_TO_GRID) {
            double cell = Grid.getSmallestCellSize();
            Point2D rounded = new Point2D(round(p.getX() / cell) * cell,
                    (round(p.getY() / cell)) * cell);

            if (computeDistance(p, rounded) < cell) {
                p.setTo(rounded);
                return true;
            }
        }
        return false;
    }

    /**
     * Проверка соблюдения дистанции между курсором мыши и объектом при перемещении
     *
     * @param d Дистанция между курсором и какой-либо точкой объекта
     * @return Соблюдение дистанции
     */
    private static boolean distancing(Point2D center, double d) {
        Point2D vector = getVector(center, getSelection().getPressedPoint());
        double dist = unitVector(vector);
        return (d > dist - GraphicObject.AROUND_SELECT_RADIUS / getScreen().getScale())
                && (d < dist + GraphicObject.AROUND_SELECT_RADIUS / getScreen().getScale());
    }

    /**
     * Перемещает лист фигур к точке на плоскости. Если включена сетка, то происходит округление
     * до ближайших координат
     *
     * @param list Лист фигур
     * @param dest Точка на плоскости
     */
    public static void displacePoints(ArrayList<GraphicObject> list, Point2D dest) {
        Selection s = getSelection();
        //Векторы перемещения фигуры при округлении movedPoints
        List<Point2D> vectors = getVectorsToRounded(list, dest);
        //Дистанции от мыши до каждой из округленных точек
        List<Double> distances = getDistancesToRounded(list.get(0).getCenter(), dest, vectors);

        int minDistanceIndex = -1;
        double minDistance = Double.MAX_VALUE;

        //Ищем минимальную дистанцию из всех возможных
        for (int i = 0; i < distances.size(); i++) {
            //расстояние от указателя мыши до фигуры с округленными координатами
            double distance = computeDistance(dest,
                    new Point2D(list.get(0).getCenter().getX() + vectors.get(i).getX(),
                            list.get(0).getCenter().getY() + vectors.get(i).getY()));
            if ((s.isCopyMode() || distancing(list.get(0).getCenter(), distance)) && distances.get(i) < minDistance) {
                minDistance = distances.get(i);
                minDistanceIndex = i;
            }
        }

        //Если оптимальное округление координат найдено, то перемещаем фигуры
        if (minDistanceIndex != -1) {
            for (GraphicObject f : list) {
                for (Point2D p : f.getPoints())
                    p.move(vectors.get(minDistanceIndex).getX(), vectors.get(minDistanceIndex).getY());
            }
            s.movePressedPoint(vectors.get(minDistanceIndex).getX(), vectors.get(minDistanceIndex).getY());
        }
        s.setCopyMode(false);
    }

    private static List<Point2D> getVectorsToRounded(List<GraphicObject> list, Point2D dest) {
        Selection s = getSelection();
        List<Point2D> movedPoints = new ArrayList<>(); //Лист округленных точек всех фигур после перемещения
        List<Point2D> vectors = new ArrayList<>();

        double dx = dest.getX() - list.get(0).getCenter().getX();
        double dy = dest.getY() - list.get(0).getCenter().getY();

        if (!s.isCopyMode()) {
            dx -= s.getPressedPoint().getX() - list.get(0).getCenter().getX();
            dy -= s.getPressedPoint().getY() - list.get(0).getCenter().getY();
        }

        selectedPointsNumber = 0;
        for (GraphicObject f : list) {
            List<Point2D> al = f.getPoints();

            for (Point2D p : al) {
                movedPoints.add(new Point2D(p));
            }

            //Округление к сетке
            for (int i = 0; i < al.size(); i++) {
                movedPoints.get(i + selectedPointsNumber).move(dx, dy);
                roundToGridPoint(movedPoints.get(i + selectedPointsNumber));
            }

            //Векторы перемещения фигуры целиком к каждой округленной точке
            for (int i = 0; i < al.size(); i++) {
                vectors.add(new Point2D(movedPoints.get(i + selectedPointsNumber).getX() - al.get(i).getX(),
                        movedPoints.get(i + selectedPointsNumber).getY() - al.get(i).getY()));
            }
            selectedPointsNumber += al.size();
        }

        //Для каждой невыбранной фигуры ищем округленные точки
        for (GraphicObject graphicObject : getData().getFigures()) {
            if (!graphicObject.isSelected()) {
                for (GraphicObject f : list) {
                    List<Point2D> al = f.getPoints();
                    for (Point2D p : al) {
                        Point2D figRound = new Point2D(p);
                        figRound.move(dx, dy);

                        //Если точка округляется, то добавляем ее в листы
                        if (graphicObject.roundToNearestPoint(figRound) && Settings.ROUND_TO_FIGURES_POINTS) {
                            movedPoints.add(figRound);
                            vectors.add(new Point2D(figRound.getX() - p.getX(), figRound.getY() - p.getY()));
                        }
                    }
                }
            }
        }

        return vectors;
    }

    private static List<Double> getDistancesToRounded(Point2D fulcrum, Point2D dest, List<Point2D> vectors) {
        List<Double> distances = new ArrayList<>();
        //Расчет дистанций между указателем и каждым из возможных перемещений
        for (int i = 0; i < vectors.size(); i++) {
            double dist = computeDistance(dest, new Point2D(fulcrum.getX() + vectors.get(i).getX(),
                    fulcrum.getY() + vectors.get(i).getY()));

            //Уменьшаем расстояние до точек других фигур вдвое, чтобы увеличить приоритет
            if (i >= selectedPointsNumber)
                dist /= 2;
            distances.add(dist);
        }

        return distances;
    }
}
