package core;

import graphic_objects.figures.Point2D;

import java.awt.*;

import static core.DrawingArea.*;
import static ui.MainWindow.getDrawingArea;
import static ui.MainWindow.getToolBar;

/**
 * Класс предназначен для преобразований координат интерфейса и плоскости
 */
public class Screen {
    /**
     * Коэффициент величины масштабирования
     */
    private final float ZOOM_RATIO = (float) 0.05;
    private final double ZOOM_HIGH_LIMIT = 16000;
    private final double ZOOM_LOW_LIMIT = 0.001;

    private double default_scale;
    private double aspect_ratio;

    private boolean initialized;
    private double x;
    private double y;
    private double w;
    private double h;
    private double scale;
    private Sheet sheet;

    public Screen(Sheet sh) {
        sheet = sh;
        initialized = false;
    }

    /**
     * @return Соотношение сторон экрана
     */
    public double aspectRatio() {
        return aspect_ratio;
    }

    /**
     * @return Масштаб, при котором лист выравнивается по высоте
     */
    public double getDefaultScale() {
        return default_scale;
    }

    /**
     * @return Переменные класса инициализированы
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Возвращает X координату центра экрана на плоскости
     *
     * @return X координата
     */
    public double getX() {
        return x + getScaledWidth() / 2;
    }

    /**
     * Возвращает Y координату центра экрана на плоскости
     *
     * @return Y координата
     */
    public double getY() {
        return y + getScaledHeight() / 2;
    }

    /**
     * Возвращает ширину экрана на плоскости
     *
     * @return Ширину экрана
     */
    public double getScaledWidth() {
        return w;
    }

    /**
     * Возвращает высоту экрана на плоскости
     *
     * @return Высоту экрана
     */
    public double getScaledHeight() {
        return h;
    }

    /**
     * Возвращает масштаб экрана
     *
     * @return Масштаб
     */
    public double getScale() {
        return scale;
    }

    /**
     * Перемещение экрана на указанные промежутки с учетом текущего масштаба
     *
     * @param x X координата вектора перемещения
     * @param y Y координата вектора перемещения
     */
    public void move(int x, int y) {
        this.x += x / getScale();
        this.y += y / getScale();
        getSelection().setStaticRect();
        getDrawingArea().repaint();
    }

    /**
     * Меняет координаты и масштаб экрана
     *
     * @param x     X координата нового местоположения
     * @param y     Y координата нового местоположения
     * @param scale Масштаб
     */
    public void setPosition(double x, double y, double scale) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        w = getDimensions().x / scale;
        h = getDimensions().y / scale;
        getSelection().setStaticRect();
        getDrawingArea().repaint();
        onScaleChange();
    }

    /**
     * Меняет масштаб экрана
     *
     * @param scale масштаб
     */
    public void setScale(double scale) {
        if (scale > 0 && scale < ZOOM_HIGH_LIMIT) {
            this.scale = scale;
            w = getDimensions().x / scale;
            h = getDimensions().y / scale;
            getSelection().setStaticRect();
            getDrawingArea().repaint();
            onScaleChange();
        }
    }

    /**
     * Меняет координаты и масштаб экрана
     *
     * @param p     Точка нового местоположения
     * @param scale Масштаб
     */
    public void setPosition(Point2D p, double scale) {
        setPosition(p.getX(), p.getY(), scale);
    }

    /**
     * Задает новый размер экрана
     *
     * @param width  Ширина экрана
     * @param height Высота экрана
     */
    public void resize(int width, int height) {
        w *= (double) width / getDimensions().x;
        h *= (double) height / getDimensions().y;
        setDimensions(width, height);
    }

    /**
     * Изменяет масштаб экрана с фокусом в произвольной точке
     *
     * @param z      величина, передающаяся от getWheelRotation()
     * @param mouseX X координата указателя мыши
     * @param mouseY Y координата указателя мыши
     */
    public void addScale(double z, int mouseX, int mouseY) {
        if (z < 0 && scale > ZOOM_LOW_LIMIT || z > 0 && scale < ZOOM_HIGH_LIMIT) {
            double vr = z * getScale() * ZOOM_RATIO + scale;
            double vrX = XToPlane(mouseX);
            double vrY = YToPlane(mouseY);

            x = vrX - (vrX - x) * (scale / vr);
            y = vrY - (vrY - y) * (scale / vr);
            scale = vr;
            w = getDimensions().x / scale;
            h = getDimensions().y / scale;
            getSelection().setStaticRect();
            getDrawingArea().repaint();
            onScaleChange();
        }
    }

    /**
     * Изменяет масштаб экрана с фокусом в центре
     *
     * @param z величина, передающаяся от getWheelRotation()
     */
    public void addScaleToCenter(double z) {
        if (z < 0 && scale > ZOOM_LOW_LIMIT || z > 0 && scale < ZOOM_HIGH_LIMIT) {
            scale += z * getScale() * ZOOM_RATIO;
            w = getDimensions().x / scale;
            h = getDimensions().y / scale;
            getSelection().setStaticRect();
            getDrawingArea().repaint();
            onScaleChange();
        }
    }

    /**
     * Проекция X координаты с панели рисования на плоскость
     *
     * @param x X координата проецируемой точки
     * @return X координату проекции точки
     */
    public double XToPlane(double x) {
        return this.x + x / scale - w / 2;
    }

    /**
     * Проекция Y координаты с панели рисования на плоскость
     *
     * @param y Y координата проецируемой точки
     * @return Y координату проекции точки
     */
    public double YToPlane(double y) {
        return this.y + (getDimensions().y - y) / scale - h / 2;
    }

    /**
     * Проекция точки с панели рисования на плоскость
     *
     * @param x X координата проецируемой точки
     * @param y Y координата проецируемой точки
     * @return Проекцию точки
     */
    public Point2D pointToPlane(double x, double y) {
        return new Point2D(XToPlane(x), YToPlane(y));
    }

    /**
     * Проекция точки с панели рисования на плоскость
     *
     * @param p Проецируемая точка
     * @return Проекцию точки
     */
    public Point2D pointToPlane(Point p) {
        return new Point2D(XToPlane(p.x), YToPlane(p.y));
    }

    /**
     * Проекция X координаты с плоскости на панель рисования
     *
     * @param x X координата проецируемой точки
     * @return X координату проекции точки
     */
    public double XToScreen(double x) {
        return (x - getX()) * getScale() + getDimensions().x;
    }

    /**
     * Проекция Y координаты с плоскости на панель рисования
     *
     * @param y Y координата проецируемой точки
     * @return Y координату проекции точки
     */
    public double YToScreen(double y) {
        return (getY() - y) * scale;
    }

    /**
     * Проекция точки с плоскости на панель рисования
     *
     * @param p Проецируемая точка
     * @return Проекцию точки
     */
    public Point2D pointToScreen(Point2D p) {
        return new Point2D(XToScreen(p.getX()), YToScreen(p.getY()));
    }

    /**
     * Проекция точки с плоскости на панель рисования
     *
     * @param x X координата проецируемой точки
     * @param y Y координата проецируемой точки
     * @return Проекцию точки
     */
    public Point2D pointToScreen(double x, double y) {
        return new Point2D(XToScreen(x), YToScreen(y));
    }

    /**
     * Инициализация переменных
     *
     * @param width  Ширина панели для рисования
     * @param height Высота панели для рисования
     */
    public void initialize(int width, int height) {
        setDimensions(width, height);
        aspect_ratio = (double) width / (double) height;
        h = sheet.getHeight();
        w = h * aspect_ratio;
        default_scale = width / w;
        scale = default_scale;
        x = getDrawingArea().getSheet().getWidth() / 2;
        y = getDrawingArea().getSheet().getHeight() / 2;
        initialized = true;
        onScaleChange();
    }

    /**
     * Вызывается при изменении маштаба
     */
    public void onScaleChange() {
        getToolBar().setFieldScale(getScale());
    }
}
