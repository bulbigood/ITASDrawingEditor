package ui;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.nio.file.FileSystem;

/**
 * Created by Никита on 26.10.2017.
 */
public class ThreeStatesJButton extends JButton {
    private final int width = 36;
    private final int height = 36;
    private Color state1ButtonColor = new ColorUIResource(238, 238, 238);
    private Color state2ButtonColor = new ColorUIResource(255, 105, 98);
    private Color state3ButtonColor = new ColorUIResource(168, 228, 255);
    private int state = 1;

    public ThreeStatesJButton(String icon, String toolTipText) {
        super(new ImageIcon(FileSystem.class.getResource("/res/" + icon)));
        setToolTipText(toolTipText);

        Dimension d = new Dimension(width, height);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
    }

    void setState1(){
        state = 1;
        setBackground(state1ButtonColor);
    }

    void setState2(){
        state = 2;
        setBackground(state2ButtonColor);
    }

    void setState3(){
        state = 3;
        setBackground(state3ButtonColor);
    }

    int getState(){
        return state;
    }
}
