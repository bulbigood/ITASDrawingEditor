package graphic_objects.figures;

import java.util.List;

/**
 * Данный интерфейс должен быть реализован другими фигурами
 */
public interface FigureInterface {

    /**
     * Возвращает, есть ли фигура в заданной точке
     *
     * @param p точка
     * @return true - есть, false - нету
     */
    boolean contain(Point2D p);

    /**
     * @param fi Фигура
     * @return Фигуры равны
     */
    boolean equalTo(FigureInterface fi);

    Point2D getPoint(int id);

    List<Point2D> getPoints();
}