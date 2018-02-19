package ui;

import graphic_objects.figures.FractalTree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.FileSystem;
import java.util.Hashtable;

/**
 * Created by Никита on 07.12.2017.
 */
public class FractalTreePanel extends JPanel {

    private static final int PANEL_SIZE_X = 150;
    private static final int PANEL_SIZE_Y = 140;

    public FractalTreePanel(){
        setSize(PANEL_SIZE_X, PANEL_SIZE_Y);
        setLayout(new GridLayout(4,2,0,0));

        JLabel angleLabel = new JLabel("Угол ветки");
        JLabel distanceLabel = new JLabel("Дистанция");
        JLabel iterationsLabel = new JLabel("Итерации");

        final JSlider angleSlider = new JSlider(JSlider.HORIZONTAL,
                5, 30, (int)FractalTree.branchAngle);
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 5 ), new JLabel("5") );
        labelTable.put( new Integer( 15 ), new JLabel("15") );
        labelTable.put( new Integer( 30 ), new JLabel("30") );
        angleSlider.setLabelTable( labelTable );
        angleSlider.setPaintLabels(true);

        final JSlider distanceSlider = new JSlider(JSlider.HORIZONTAL,
                10, 100, (int)FractalTree.branchDistance);
        labelTable = new Hashtable();
        labelTable.put( new Integer( 10 ), new JLabel("0") );
        labelTable.put( new Integer( 55 ), new JLabel("55") );
        labelTable.put( new Integer( 100 ), new JLabel("100") );
        distanceSlider.setLabelTable( labelTable );
        distanceSlider.setPaintLabels(true);

        final JSlider iterSlider = new JSlider(JSlider.HORIZONTAL,
                1, 10, (int)FractalTree.iterations);
        labelTable = new Hashtable();
        labelTable.put( new Integer( 1 ), new JLabel("1") );
        labelTable.put( new Integer( 5 ), new JLabel("5") );
        labelTable.put( new Integer( 10 ), new JLabel("10") );
        iterSlider.setLabelTable( labelTable );
        iterSlider.setPaintLabels(true);

        JButton applyButton = new JButton(new ImageIcon(FileSystem.class.getResource("/res/apply.png")));
        applyButton.setToolTipText("Применить");
        applyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                FractalTree.branchAngle = angleSlider.getValue();
                FractalTree.branchDistance = distanceSlider.getValue();
                FractalTree.iterations = iterSlider.getValue();
                setVisible(false);
            }
        });

        add(angleLabel);
        add(angleSlider);

        add(distanceLabel);
        add(distanceSlider);

        add(iterationsLabel);
        add(iterSlider);

        add(applyButton);
    }
}
