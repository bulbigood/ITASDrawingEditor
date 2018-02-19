package graphic_objects.figures.properties;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import core.Settings;
import graphic_objects.GraphicObject;
import graphic_objects.figures.Arc;
import graphic_objects.figures.Point2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static core.DrawingArea.getSelection;
import static core.DrawingArea.getStates;
import static ui.MainWindow.getDrawingArea;
import static ui.MainWindow.getPropertyBox;

/**
 * Панель свойств фигуры "дуга".
 */
public class ArcProperties extends Properties {

    private Arc figure;
    private JTextField xField;
    private JTextField yField;
    private JTextField radiusField;
    private JTextField startingAngleField;
    private JTextField angularExtentField;

    public ArcProperties(Arc f) {
        this.figure = f;
        panel = new JPanel();
        panel.setLayout(new FormLayout(
                "12*(default, $lcgap), default",
                "default"));

        Dimension d = new Dimension(60, 20);

        panel.add(new JLabel("Центр: "), CC.xy(3, 1));
        xField = new JTextField();
        xField.setPreferredSize(d);
        panel.add(xField, CC.xy(5, 1));

        yField = new JTextField();
        yField.setPreferredSize(d);
        panel.add(yField, CC.xy(7, 1));

        panel.add(new JLabel("Радиус: "), CC.xy(9, 1));
        radiusField = new JTextField();
        radiusField.setPreferredSize(d);
        panel.add(radiusField, CC.xy(11, 1));

        panel.add(new JLabel("Начальный угол: "), CC.xy(13, 1));
        startingAngleField = new JTextField();
        startingAngleField.setPreferredSize(d);
        panel.add(startingAngleField, CC.xy(15, 1));

        panel.add(new JLabel("Угол дуги: "), CC.xy(17, 1));
        angularExtentField = new JTextField();
        angularExtentField.setPreferredSize(d);
        panel.add(angularExtentField, CC.xy(19, 1));
        onChange();

        xField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    yField.requestFocus();
                    yField.selectAll();
                }
            }
        });

        yField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    radiusField.requestFocus();
                    radiusField.selectAll();
                }
            }
        });

        radiusField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startingAngleField.requestFocus();
                    startingAngleField.selectAll();
                }
            }
        });

        startingAngleField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    angularExtentField.requestFocus();
                    angularExtentField.selectAll();
                }
            }
        });

        angularExtentField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onApply();
                }
            }
        });

    }

    public void onApply() {
        boolean changed = false;
        if (!toDoubleOutFormat(figure.getCenter().getX()).equals(xField.getText())
                || !toDoubleOutFormat(figure.getCenter().getY()).equals(yField.getText())
                || !toDoubleOutFormat(figure.getRadius()).equals(radiusField.getText())
                || !toDoubleOutFormat(figure.getStartingAngle()).equals(startingAngleField.getText())
                || !toDoubleOutFormat(figure.getAngularExtent()).equals(angularExtentField.getText())
                || GraphicObject.figureIsBuilding())
            changed = true;
        try {
            if (!xField.getText().isEmpty())
                figure.getCenter().setX(Double.parseDouble(xField.getText()));

            if (!yField.getText().isEmpty())
                figure.getCenter().setY(Double.parseDouble(yField.getText()));

            if (!radiusField.getText().isEmpty())
                figure.setRadius(Double.parseDouble(radiusField.getText()));

            if (!startingAngleField.getText().isEmpty())
                figure.setStartingAngle(Double.parseDouble(startingAngleField.getText()));

            if (!angularExtentField.getText().isEmpty())
                figure.setAngularExtent(Double.parseDouble(angularExtentField.getText()));

            figure.buildWithProperties();
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        figure.refreshPoints();

        GraphicObject ins = GraphicObject.getBuildingGraphicObject();
        if (ins != null)
            GraphicObject.finishBuildingFigure();
        getSelection().select(figure, false);
        getSelection().onSelectionChanged();
        if (changed)
            getStates().fixState();
        getDrawingArea().repaint();
    }

    @Override
    public void onChange() {
        if (panel != null && getPropertyBox().isVisible()) {
            //Округление координат точек к точности 0.0001
            if (!figure.isBuilding()) {
                for (Point2D p : figure.getPoints()) {
                    p.setX(Math.rint(p.getX() * Settings.ACCURACY) / Settings.ACCURACY);
                    p.setY(Math.rint(p.getY() * Settings.ACCURACY) / Settings.ACCURACY);
                }
            }

            xField.setText(String.format("%.4f", figure.getCenter().getX()).replace(',', '.'));
            yField.setText(String.format("%.4f", figure.getCenter().getY()).replace(',', '.'));
            radiusField.setText(String.format("%.4f", figure.getRadius()).replace(',', '.'));
            startingAngleField.setText(String.format("%.4f", figure.getStartingAngle()).replace(',', '.'));
            angularExtentField.setText(String.format("%.4f", figure.getAngularExtent()).replace(',', '.'));
        }
    }
}