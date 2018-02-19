package core;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Лист
 */
public class Sheet {
    private final BasicStroke SHEET_STROKE = new BasicStroke(2);
    private Point size;

    public Sheet(Format format) {
        size = format.getSize();
    }

    public int getWidth() {
        return size.x;
    }

    public int getHeight() {
        return size.y;
    }

    /**
     * Возвращает ответ на вопрос "лежит ли точка на листе"
     */
    public boolean placedOnSheet(Screen sc, int x, int y) {
        boolean can = false;
        if (sc.XToPlane(x) > 0 && sc.XToPlane(x) < size.x &&
                sc.YToPlane(y) > 0 && sc.YToPlane(y) < size.y)
            can = true;
        return can;
    }

    public void draw(Graphics2D g, Screen sc) {
        Color vrc = g.getColor();
        Stroke vrs = g.getStroke();

        g.setColor(Color.BLACK);
        g.setStroke(SHEET_STROKE);

        g.draw(new Rectangle2D.Double(sc.XToScreen(0), sc.YToScreen(size.y),
                size.x * sc.getScale(), size.y * sc.getScale()));

        g.setColor(vrc);
        g.setStroke(vrs);
    }

    /**
     * Форматы листов
     */
    public enum Format {
        A0(841, 1189), A1(594, 841), A2(420, 594), A3(297, 420), A4(210, 297), A5(148, 210), A6(105, 148), A7(74, 105), A8(52, 74),
        B0(1000, 1414), B1(707, 1000), B2(500, 707), B3(353, 500), B4(250, 353), B5(176, 250), B6(125, 176), B7(88, 125), B8(62, 88),
        C0(917, 1297), C1(648, 917), C2(458, 648), C3(324, 458), C4(229, 324), C5(162, 229), C6(114, 162), C7(81, 114), C8(57, 81);

        private Point size;

        Format(int x, int y) {
            size = new Point();
            size.x = x;
            size.y = y;
        }

        public Point getSize() {
            return size;
        }
    }
}
