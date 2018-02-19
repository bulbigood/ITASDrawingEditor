package ui;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import core.DrawingArea;
import core.Log;
import graphic_objects.GraphicObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.FileSystem;

import static core.DrawingArea.getSelection;
import static ui.MainWindow.getDrawingArea;

/**
 * Панель свойств
 */

public class PropertyBox extends JScrollPane {
    private JPanel panel = new JPanel();
    /**
     * Панель с кнопками
     */
    private JPanel buttonsPanel = new JPanel();

    private GraphicObject selectedGraphicObject;

    /**
     * Инициализирует панель свойств
     */
    public PropertyBox() {
        // запрещаем полосы прокрутки
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // AS_NEEDED
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        setViewportView(panel);
        initButtonsPanel();
        onSelectionChanged();
    }

    /**
     * Инциализирует панель кнопок
     */
    private void initButtonsPanel() {
        buttonsPanel.setLayout(new FormLayout(
                "center:32px, center:28px, 2*(default)",
                "pref:grow"));

        JButton applyButton = new JButton(new ImageIcon(FileSystem.class.getResource("/res/apply.png")));
        applyButton.setToolTipText("Применить");
        buttonsPanel.add(setButtonSize(applyButton, 25, 20), CC.xywh(1, 1, 1, 1));

        JButton stopButton = new JButton(new ImageIcon(FileSystem.class.getResource("/res/stop.gif")));
        stopButton.setToolTipText("Прервать");
        buttonsPanel.add(setButtonSize(stopButton, 25, 20), CC.xywh(2, 1, 1, 1));

        applyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedGraphicObject != null)
                    selectedGraphicObject.getProperties().onApply();
            }
        });

        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                GraphicObject graphicObject = GraphicObject.getBuildingGraphicObject();
                if (graphicObject != null) {
                    Log.add("Прервано: " + graphicObject);
                    GraphicObject.finishBuildingFigure();
                    DrawingArea.getData().removeFigure(graphicObject);
                    getDrawingArea().repaint();
                }
                getSelection().unselectAll();
                getDrawingArea().repaint();
                panel.setVisible(false);
            }
        });
    }

    /**
     * Вызывается, когда произошло выделение
     */
    public void onSelectionChanged() {
        panel.setVisible(false);
        panel.removeAll();

        if (getSelection().getCountSelected() == 1) {
            panel.setLayout(new FormLayout(
                    "3*(default)",
                    "pref:grow"));

            panel.add(buttonsPanel, CC.xy(1, 1));

            selectedGraphicObject = getSelection().getSelected().get(0);
            if(selectedGraphicObject.getProperties() != null) {
                JPanel figurePanel = selectedGraphicObject.getProperties().getPanel();
                if (figurePanel != null)
                    panel.add(figurePanel, CC.xy(3, 1));

                panel.setVisible(true);
            }
        }
    }


    /**
     * Заменяет данные в панели свойств
     *
     * @param jp Swing-панель
     */
    public void setPanel(JPanel jp) {
        panel.setVisible(false);
        panel.removeAll();
        if (jp != null) {
            panel.add(jp, CC.xy(1, 1));
            panel.setVisible(true);
        }
    }

    /**
     * Устанавливает размер кнопки
     *
     * @param b кнопка
     * @param w ширина
     * @param h высота
     * @return кнопка с новыми размерами
     */
    private AbstractButton setButtonSize(AbstractButton b, int w, int h) {
        Dimension d = new Dimension(w, h);
        b.setPreferredSize(d);
        b.setMinimumSize(d);
        b.setMaximumSize(d);
        return b;
    }
}
