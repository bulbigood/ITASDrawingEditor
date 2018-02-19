package core;

import graphic_objects.GraphicObject;

import java.util.ArrayList;

import static core.DrawingArea.getSelection;
import static ui.MainWindow.getToolBar;
import static ui.MainWindow.getTopMenuBar;

/**
 * Система состояний
 */
public class States {

    /**
     * Максимальное количество состояний
     */
    public static final int MAX_STATE_COUNT = 50;

    /**
     * Текущее состояние, нормируемое с нуля
     */
    private int thisState = -1;

    /**
     * Список состояний
     */
    private ArrayList<ArrayList<GraphicObject>> states = new ArrayList<>();

    /**
     * Возвращает количество состояний
     *
     * @return количество состояний
     */
    public int getCount() {
        return states.size();
    }

    /**
     * Позволяет перейти к предыдущему состоянию
     */
    public void undo() {
        if (getCount() > 0 && thisState > 0) {
            GraphicObject graphicObject = GraphicObject.getBuildingGraphicObject();
            if (graphicObject != null) {
                GraphicObject.finishBuildingFigure();
                DrawingArea.getData().removeFigure(graphicObject);
            }
            getSelection().unselectAll();
            thisState--;
            DrawingArea.getData().setFigures(states.get(thisState));
            Log.add("Переход к состоянию " + thisState);
            onStateChange();
        }
    }

    /**
     * Позволяет перейти на следующее состояние
     */
    public void redo() {
        if (getCount() > 0 && thisState + 1 < getCount()) {
            GraphicObject graphicObject = GraphicObject.getBuildingGraphicObject();
            if (graphicObject != null) {
                GraphicObject.finishBuildingFigure();
                DrawingArea.getData().removeFigure(graphicObject);
            }
            getSelection().unselectAll();
            thisState++;
            DrawingArea.getData().setFigures(states.get(thisState));
            Log.add("Переход к состоянию " + thisState);
            onStateChange();
        }
    }

    /**
     * Добавляет новое состояние
     */
    public void fixState() {
        /*if (getCountObjectsInStates() > 3000){
            states.clear();
            thisState = -1;
        }*/
        if (getCount() > MAX_STATE_COUNT) {
            removeStatesWithPosition(0, 1);
            thisState--;
        }

        ArrayList<GraphicObject> data = new ArrayList<>();

        for (GraphicObject f : DrawingArea.getData().getFigures()) {
            data.add(f.clone());
        }
        ++thisState;
        removeStatesWithPosition(thisState, getCount() - thisState);
        states.add(data);
        Log.add("Состояние фиксировано" + DrawingArea.getData().getFigures());
        onStateChange();
    }

    /**
     * Удаляет состояния с заданной позиции
     *
     * @param start заданная позиция
     * @param count количество удаляемых состояний
     */
    public void removeStatesWithPosition(int start, int count) {
        for (int i = 0; i < count; i++)
            states.remove(start);
    }

    /**
     * Вызывается при изменении состояния
     */
    public void onStateChange() {
        if (thisState <= 0) {
            getToolBar().setUndoEnabled(false);
            getTopMenuBar().setUndoEnabled(false);
        } else {
            getToolBar().setUndoEnabled(true);
            getTopMenuBar().setUndoEnabled(true);
        }
        if (thisState == getCount() - 1) {
            getToolBar().setRedoEnabled(false);
            getTopMenuBar().setRedoEnabled(false);
        } else {
            getToolBar().setRedoEnabled(true);
            getTopMenuBar().setRedoEnabled(true);
        }
    }

    /**
     * Сбрасывает все состояния
     */
    public void resetAll() {
        thisState = -1;
        removeStatesWithPosition(0, getCount() - 1);
        onStateChange();
    }

    /**
     * Возвращает количество объектов во всех состояниях
     *
     * @return количество объектов во всех состояниях
     */
    public long getCountObjectsInStates() {
        long count = 0;
        for (ArrayList<GraphicObject> al : states)
            count += al.size();
        return count;
    }
}
