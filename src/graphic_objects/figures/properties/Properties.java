package graphic_objects.figures.properties;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Абстрактный класс для свойств фигур
 */
public abstract class Properties {

    static final String DOUBLE_OUT_FORMAT = "%.4f";

    protected JPanel panel = null;

    public JPanel getPanel() {
        return panel;
    }

    public abstract void onChange();

    public abstract void onApply();

    protected void update(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                onApply();
                break;
            case KeyEvent.VK_ESCAPE:
                onChange();
                break;
        }
    }

    protected String toDoubleOutFormat(double d) {
        return String.format(DOUBLE_OUT_FORMAT, d).replace(',', '.');
    }
}