package core;

import graphic_objects.GraphicObject;
import graphic_objects.figures.Point2D;

import java.util.ArrayList;
import java.util.List;

import static core.DrawingArea.getSelection;
import static core.DrawingArea.getStates;
import static core.geometry_algorithms.PointRounding.displacePoints;
import static ui.MainWindow.*;

/**
 * Буфер обмена
 * TODO: вставка в заданную область
 */
public class Clipboard {

    /**
     * Список фигур в буфере
     */
    static List<GraphicObject> bufferedGraphicObjects = new ArrayList<>();

    /**
     * Смещение при вставке
     */
    private static double offset;

    /**
     * Помещает выделенные фигуры в буфер
     */
    public static void copy() {
        List<GraphicObject> selectedGraphicObjects = getSelection().getSelected();
        if (selectedGraphicObjects.size() > 0) {
            offset = 0;
            bufferedGraphicObjects.clear();
            for (GraphicObject f : selectedGraphicObjects)
                bufferedGraphicObjects.add(f.clone());
            Log.add("Помещены в буфер: " + bufferedGraphicObjects);
            onBufferChange();
        }
    }

    /**
     * Добавляет фигуры из буфера на экран
     */
    public static void paste() {
        if (bufferedGraphicObjects != null && bufferedGraphicObjects.size() > 0) {
            offset += Grid.getSmallestCellSize();
            getSelection().unselectAll();

            ArrayList<GraphicObject> list = new ArrayList<>();
            for (GraphicObject f : bufferedGraphicObjects)
                list.add(f.clone());

            getSelection().setCopyMode(true);
            displacePoints(list,
                    new Point2D(list.get(0).getCenter().getX() + offset, list.get(0).getCenter().getY() - offset));

            for (GraphicObject f : list) {
                DrawingArea.getData().addFigure(f);
                getSelection().select(f, true);
            }
            getSelection().setStaticRect();
            Log.add("Сделаны копии фигур " + bufferedGraphicObjects);
            getStates().fixState();
            getDrawingArea().repaint();
            getSelection().onSelectionChanged();
        }
    }

    /**
     * Помещает выделенные фигуры в буфер и удаляет их
     */
    public static void cut() {
        if (getSelection().getCountSelected() > 0) {
            copy();
            getSelection().removeSelected();
            Log.add("Вырезаны фигуры " + bufferedGraphicObjects);
        }
    }

    /**
     * Вызывается при изменении буффера
     */
    private static void onBufferChange() {
        if (bufferedGraphicObjects.size() == 0) {
            getToolBar().setPasteEnabled(false);
            getTopMenuBar().setPasteEnabled(false);
        } else {
            getToolBar().setPasteEnabled(true);
            getTopMenuBar().setPasteEnabled(true);
        }
    }
}
