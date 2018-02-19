package graphic_objects.figures.properties;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import graphic_objects.GraphicObject;
import graphic_objects.figures.Segment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static core.DrawingArea.getSelection;
import static core.DrawingArea.getStates;
import static ui.MainWindow.getDrawingArea;
import static ui.MainWindow.getPropertyBox;

/**
 * Панель свойств фигуры "отрезок".
 */
public class SegmentProperties extends Properties {
    private Segment figure;

    private JTextField x0Field;
    private JTextField y0Field;
    private JTextField x1Field;
    private JTextField y1Field;

    public SegmentProperties(Segment f) {
        this.figure = f;
        panel = new JPanel();
        panel.setLayout(new FormLayout(
                "10*(default, $lcgap), default",
                "default"));

        Dimension d = new Dimension(60, 20);

        panel.add(new JLabel("т1"), CC.xy(3, 1));
        x0Field = new JTextField();
        x0Field.setPreferredSize(d);
        panel.add(x0Field, CC.xy(5, 1));

        y0Field = new JTextField();
        y0Field.setPreferredSize(d);
        panel.add(y0Field, CC.xy(7, 1));

        panel.add(new JLabel("т2"), CC.xy(9, 1));
        x1Field = new JTextField();
        x1Field.setPreferredSize(d);
        panel.add(x1Field, CC.xy(11, 1));

        y1Field = new JTextField();
        y1Field.setPreferredSize(d);
        panel.add(y1Field, CC.xy(13, 1));

        onChange();

        x0Field.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    y0Field.requestFocus();
                    y0Field.selectAll();
                }
            }
        });

        y0Field.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    x1Field.requestFocus();
                    x1Field.selectAll();
                }
            }
        });

        x1Field.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    y1Field.requestFocus();
                    y1Field.selectAll();
                }
            }
        });

        y1Field.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onApply();
                }
            }
        });
    }

    public void onApply() {
        boolean changed = false;
        if (!toDoubleOutFormat(figure.getPoints().get(0).getX()).equals(x0Field.getText())
                || !toDoubleOutFormat(figure.getPoints().get(0).getY()).equals(y0Field.getText())
                || !toDoubleOutFormat(figure.getPoints().get(1).getX()).equals(x1Field.getText())
                || !toDoubleOutFormat(figure.getPoints().get(1).getY()).equals(y1Field.getText())
                || GraphicObject.figureIsBuilding())
            changed = true;
        try {
            if (!x0Field.getText().isEmpty())
                figure.getPoints().get(0).setX(Double.parseDouble(x0Field.getText()));

            if (!y0Field.getText().isEmpty())
                figure.getPoints().get(0).setY(Double.parseDouble(y0Field.getText()));

            if (!x1Field.getText().isEmpty())
                figure.getPoints().get(1).setX(Double.parseDouble(x1Field.getText()));

            if (!y1Field.getText().isEmpty())
                figure.getPoints().get(1).setY(Double.parseDouble(y1Field.getText()));
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }

        //Делаем координты центра отрезка актуальными
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

    public void onChange() {
        if (panel != null && getPropertyBox().isVisible()) {
            x0Field.setText(toDoubleOutFormat(figure.getPoints().get(0).getX()));
            y0Field.setText(toDoubleOutFormat(figure.getPoints().get(0).getY()));
            x1Field.setText(toDoubleOutFormat(figure.getPoints().get(1).getX()));
            y1Field.setText(toDoubleOutFormat(figure.getPoints().get(1).getY()));
        }
    }
}