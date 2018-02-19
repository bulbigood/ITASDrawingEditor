package core;

import graphic_objects.figures.Point2D;

import java.awt.*;
import java.awt.geom.Line2D;

import static core.DrawingArea.getDimensions;
import static core.DrawingArea.getScreen;
import static java.lang.Math.*;

/**
 * Сетка
 */
public class Grid {
    private static final int GRID_BRIGHTNESS = 128;
    private static final float STROKE_WIDTH = (float) 1.0;
    public static int stepSize = 10;
    public static int stepNumber = 10;
    private static double scaledCellSize;
    private static double screenStep;
    private static BasicStroke grid_stroke;

    public static double getSmallestCellSize() {
        return scaledCellSize;
    }

    public static void draw(Graphics2D g, Screen sc) {
        for (int i = numberOfIterations(sc.getScale()), j = 0; j < 3; i--, j++) {
            double scaledStep = stepSize / pow(stepNumber, i);
            screenStep = scaledStep * sc.getScale();
            if (j == 0)
                scaledCellSize = scaledStep;

            if (Settings.DRAW_GRID) {
                Stroke vrS = g.getStroke();
                Color vrC = g.getColor();

                Point2D firstPointProjection = sc.pointToScreen(firstCrossOfLines(scaledStep));
                g.setColor(curColor());
                grid_stroke = new BasicStroke(min((float) max((STROKE_WIDTH * sc.getScale()), STROKE_WIDTH), STROKE_WIDTH));
                g.setStroke(grid_stroke);

                //Вертикальные линии сетки
                for (double x = firstPointProjection.getX(); x < getDimensions().x; x += scaledStep * sc.getScale())
                    g.draw(new Line2D.Double(x, 0, x, getDimensions().y));

                //Горизонтальные линии сетки
                for (double y = firstPointProjection.getY(); y < getDimensions().y; y += scaledStep * sc.getScale())
                    g.draw(new Line2D.Double(0, y, getDimensions().x, y));

                //Возвращаем старый стиль
                g.setStroke(vrS);
                g.setColor(vrC);
            }
        }
    }

    /**
     * Возвращает точку на плоскости, где происходит первое пересечение прямых,
     * считая от левого верхнего угла панели рисования
     *
     * @param cellSize Размер сеточной ячейки
     * @return Точку пересечения прямых
     */
    private static Point2D firstCrossOfLines(double cellSize) {
        double x = getScreen().XToPlane(0);
        double y = getScreen().YToPlane(0);
        return new Point2D(ceil(x / cellSize) * cellSize, floor(y / cellSize) * cellSize);
    }

    /**
     * Расчет яркости рисуемой сетки в диапазоне 0 - 255
     *
     * @return Яркость рисуемой сетки
     */
    private static int getCurBrightness() {
        return (int) (255 - 4 * (screenStep));
    }

    /**
     * Возвращает черный цвет с яркостью в диапазоне 128 - 255
     *
     * @return Цвет рисуемой сетки
     */
    private static Color curColor() {
        return new Color(min(255, max(GRID_BRIGHTNESS, getCurBrightness())),
                min(255, max(GRID_BRIGHTNESS, getCurBrightness())),
                min(255, max(GRID_BRIGHTNESS, getCurBrightness())));
    }

    /**
     * Вычисление числа необходимых итераций по формуле, выведенной из равенства SCALE = 2*5^iter,
     * где iter - число итераций
     *
     * @param scale Масштаб экрана
     * @return Число необходимых итераций
     */
    private static int numberOfIterations(double scale) {
        return (int) Math.round(Math.log(scale * stepSize / 10) / Math.log(stepNumber));
    }
}
