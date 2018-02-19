package core;

import graphic_objects.CoordOrigin;
import graphic_objects.GraphicObject;
import graphic_objects.figures.Point2D.PointType;
import ui.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import static ui.MainWindow.getDrawingArea;


/**
 * Область черчения
 */
public class DrawingArea extends JComponent {

    /**
     * Цвет области черчения
     */
    private final Color BACKGROUND_COLOR = Color.white;

    private static Point dimensions = new Point();

    private static MouseListener mouseListener;
    private static KeyListener keyListener;
    private static ComponentListener componentListener;

    private static ToolMode toolMode = ToolMode.CURSOR;
    private static Screen screen;
    private static States states;
    private static Selection selection;
    private Sheet sheet;

    private static Data data = new Data();
    private Morpher morpher = new Morpher();

    private static boolean morphingMode = false;

    private CoordOrigin oxy = new CoordOrigin(0, 0, Color.red);
    private CoordOrigin draggable_oxy = new CoordOrigin(0, 0, Color.blue);
    private boolean draggable_oxy_activated = false;

    public DrawingArea() {
        sheet = new Sheet(Sheet.Format.A4);
        screen = new Screen(sheet);
        selection = new Selection();
        states = new States();
        new Clipboard();
        new Grid();

        mouseListener = new MouseListener();
        keyListener = new KeyListener();

        componentListener = new ComponentListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        addMouseWheelListener(mouseListener);
        addKeyListener(keyListener);

        addComponentListener(componentListener);
        setBackground(BACKGROUND_COLOR);
        setOpaque(true);
        setDoubleBuffered(true);

        getStates().fixState();
    }

    /**
     * Возвращает размеры
     *
     * @return
     */
    public static Point getDimensions() {
        return dimensions;
    }

    /**
     * Задает размеры
     *
     * @param x
     * @param y
     */
    public static void setDimensions(int x, int y) {
        dimensions.setLocation(x, y);
    }

    /**
     * Возвращает экземпляр текущего экрана
     *
     * @return экземпляр текущего экрана
     */
    public static Screen getScreen() {
        return screen;
    }

    /**
     * Возвращает текущий режим
     *
     * @return текущий режим
     */
    public static ToolMode getToolMode() {
        return toolMode;
    }

    /**
     * Устанавливает режим
     *
     * @param m новый режим
     */
    public static void setToolMode(ToolMode m) {
        Log.add("Активирован режим: " + m);
        toolMode = m;

        GraphicObject go = GraphicObject.getBuildingGraphicObject();
        if (go != null) {
            getData().removeFigure(go);
            GraphicObject.finishBuildingFigure();
        }
        MainWindow.getToolBox().onModeChange(toolMode);
    }

    public static boolean isMorphingMode(){
        return morphingMode;
    }

    /**
     * Возвращает экземпляр данных об объектах на сцене
     *
     * @return экземпляр данных об объектах на сцене
     */
    public static Data getData() {
        return data;
    }

    /**
     * Возвращает экземпляр выделения
     *
     * @return экземпляр выделения
     */
    public static Selection getSelection() {
        return selection;
    }

    /**
     * Возвращает экземпляр состояний
     *
     * @return экземпляр состояний
     */
    public static States getStates() {
        return states;
    }

    /**
     * Возвращает экзепляр текущего листа
     *
     * @return экзепляр текущего листа
     */
    public Sheet getSheet() {
        return sheet;
    }

    public Morpher getMorpher(){
        return morpher;
    }

    public void pauseMorphing(boolean b){
        morpher.pause(b);
    }

    public void runMorphing(){
        Log.add("Активирован режим морфинга");
        morphingMode = true;
        if(!morpher.isRunning())
            new Thread(morpher).start();
    }

    public void stopMorphing(){
        Log.add("Режим морфинга остановлен");
        morphingMode = false;
        morpher.stop();
        repaint();
    }

    public void enableDraggableOXY(){
        Rectangle2D rekt = getSelection().getSelectedRectangle();
        draggable_oxy.setCoords(rekt.getMinX(), rekt.getMinY());
        data.addMeter(draggable_oxy);
        //draggable_oxy_activated = true;
        repaint();
    }

    public void unableDraggableOXY(){
        //draggable_oxy_activated = false;
        data.removeMeter(draggable_oxy);
        repaint();
    }

    public CoordOrigin getDraggableOXY(){
        return draggable_oxy;
    }

    public boolean draggebleOXYactivated(){
        return draggable_oxy_activated;
    }

    /**
     * Возвращает экземпляр событий мыши
     *
     * @return экзепляр событий мыши
     */
    public static MouseListener getMouseListener() {
        return mouseListener;
    }

    /**
     * Отрисовка изменений
     *
     * @param g компонент графики
     */
    @Override
    public void paint(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(getForeground());
        grabFocus();
        Graphics2D g2 = (Graphics2D) g;
        if (Settings.ANTIALIASING) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        }

        // отрисока сетки
        Grid.draw(g2, screen);
        //oxy.draw(g2);
        //if(draggable_oxy_activated)
        //    draggable_oxy.draw(g2);

        ArrayList<GraphicObject> selectedGraphicObjects = getSelection().getSelected();

        // отрисовка не выделенных фигур
        for (GraphicObject f : getData().getFigures())
            if (!f.isSelected() && !(morphingMode && f.isMorphed()))
                f.draw(g2);

        // отрисовка выделенных фигур
        for (GraphicObject f : selectedGraphicObjects)
            if(!(morphingMode && f.isMorphed()))
                f.draw(g2);

        if(morphingMode){
            Stroke stroke = g2.getStroke();
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            ArrayList<ArrayList<Point2D>> points = morpher.getPoints();
            for(ArrayList<Point2D> points_object : points){
                Path2D path = new Path2D.Double();
                for(int i = 0; i < points_object.size() - 1; i++){
                    path.moveTo(screen.XToScreen(points_object.get(i).getX()), screen.YToScreen(points_object.get(i).getY()));
                    path.lineTo(screen.XToScreen(points_object.get(i+1).getX()), screen.YToScreen(points_object.get(i+1).getY()));
                    //path.quadTo(screen.XToScreen(points_object.get(i).getX()), screen.YToScreen(points_object.get(i).getY()),
                    //        screen.XToScreen(points_object.get(i+1).getX()), screen.YToScreen(points_object.get(i+1).getY()));
                }
                g2.draw(path);
            }

            g2.setStroke(stroke);
        } else {
            //отрисовка измерителей
            for (GraphicObject f : getData().getMeters())
                f.draw(g2);

            // отрисовка точек фигур
            Stroke vrs = g2.getStroke();
            g2.setStroke(PointType.POINT_STROKE);
            for (GraphicObject f : getData().getFigures())
                if (f.isSelected() || Settings.DRAW_POINTS)
                    f.drawPoints(g2, f);
            g2.setStroke(vrs);

            // отрисовка области выделения
            selection.draw(g2);
        }
    }

    /**
     * Слушатель компанента для инициализации экрана при изменении размера
     */
    public class ComponentListener implements java.awt.event.ComponentListener {
        @Override
        public void componentResized(ComponentEvent componentEvent) {
            if (getScreen().isInitialized())
                getScreen().resize(getDrawingArea().getWidth(), getDrawingArea().getHeight());
            else
                getScreen().initialize(getDrawingArea().getWidth(), getDrawingArea().getHeight());
        }

        @Override
        public void componentMoved(ComponentEvent componentEvent) {
        }

        @Override
        public void componentShown(ComponentEvent componentEvent) {
        }

        @Override
        public void componentHidden(ComponentEvent componentEvent) {
        }
    }
}

