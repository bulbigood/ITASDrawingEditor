package graphic_objects.figures.properties;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import graphic_objects.GraphicObject;
import graphic_objects.figures.Circle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static core.DrawingArea.getSelection;
import static core.DrawingArea.getStates;
import static ui.MainWindow.getDrawingArea;
import static ui.MainWindow.getPropertyBox;

/**
 * Панель свойств фигуры "окружность".
 */
public class CircleProperties extends Properties {
    private Circle figure;

    private JTextField xField;
    private JTextField yField;
    private JTextField radiusField;

    public CircleProperties(Circle f) {
        this.figure = f;
        panel = new JPanel();
        panel.setLayout(new FormLayout(
                "10*(default, $lcgap), default",
                "default"));

        Dimension d = new Dimension(60, 20);

//        panel.add(new JLabel(figure.getType()), CC.xy(1, 1));

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
                || GraphicObject.figureIsBuilding())
            changed = true;
        try {
            if (!xField.getText().isEmpty())
                figure.getPoints().get(0).setX(Double.parseDouble(xField.getText()));

            if (!yField.getText().isEmpty())
                figure.getPoints().get(0).setY(Double.parseDouble(yField.getText()));

            if (!radiusField.getText().isEmpty()) {
                double rad = Double.parseDouble(radiusField.getText());
                if (rad > 0)
                    figure.setRadius(rad);
            }
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }

        GraphicObject ins = GraphicObject.getBuildingGraphicObject();
        if (ins != null)
            GraphicObject.finishBuildingFigure();
        getSelection().select(figure, false);
        getSelection().onSelectionChanged();
        if (changed)
            getStates().fixState();
        getDrawingArea().repaint();
    }

    public void onChange() {
        if (panel != null && getPropertyBox().isVisible()) {
            xField.setText(String.format("%.4f", figure.getCenter().getX()).replace(',', '.'));
            yField.setText(String.format("%.4f", figure.getCenter().getY()).replace(',', '.'));
            radiusField.setText(String.format("%.4f", figure.getRadius()).replace(',', '.'));
        }
    }
}