package ui;

import core.geometry_algorithms.MatrixTransformer;
import graphic_objects.figures.Point2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.FileSystem;

import static core.DrawingArea.getSelection;
import static ui.MainWindow.getDrawingArea;

/**
 * Created by Никита on 12.11.2017.
 */
public class MatrixPanel extends JPanel {

    private static final int PANEL_SIZE_X = 100;
    private static final int PANEL_SIZE_Y = 100;
    private static final int MATRIX_SIZE = 9;

    private JTextField[] fields_matrix = new JTextField[MATRIX_SIZE];
    //private JLabel[] labels_matrix = new JLabel[MATRIX_SIZE];

    public MatrixPanel(){
        setSize(PANEL_SIZE_X, PANEL_SIZE_Y);
        setLayout(new GridLayout(4,3,5,5));
        for(int i = 0; i < MATRIX_SIZE; i++){
            fields_matrix[i] = new JTextField("0");
            add(fields_matrix[i]);
        }
        fields_matrix[0].setText("1");
        fields_matrix[4].setText("1");
        fields_matrix[8].setText("1");

        JButton applyButton = new JButton(new ImageIcon(FileSystem.class.getResource("/res/apply.png")));
        applyButton.setToolTipText("Применить");
        applyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                double[][] matrix = new double[3][3];
                try {
                    matrix[0][0] = Double.parseDouble(fields_matrix[0].getText());
                    matrix[0][1] = Double.parseDouble(fields_matrix[1].getText());
                    matrix[0][2] = Double.parseDouble(fields_matrix[2].getText());
                    matrix[1][0] = Double.parseDouble(fields_matrix[3].getText());
                    matrix[1][1] = Double.parseDouble(fields_matrix[4].getText());
                    matrix[1][2] = Double.parseDouble(fields_matrix[5].getText());
                    matrix[2][0] = Double.parseDouble(fields_matrix[6].getText());
                    matrix[2][1] = Double.parseDouble(fields_matrix[7].getText());
                    matrix[2][2] = Double.parseDouble(fields_matrix[8].getText());

                    if(matrix[2][2] == 0 || matrix[0][0] == 0 && matrix[0][1] == 0 && matrix[1][0] == 0 && matrix[1][1] == 0)
                        JOptionPane.showMessageDialog(new JFrame(), "Неправильно задана матрица!");
                    else
                        MatrixTransformer.transformObjects(getSelection().getSelected(), matrix, getDrawingArea().getDraggableOXY().getCoords());
                } catch(NullPointerException | NumberFormatException ne) {
                    JOptionPane.showMessageDialog(new JFrame(), "Ошибка чтения данных из матрицы. Поля могут содержать только цифры и точку для десятичных дробей");
                }
            }
        });
        add(applyButton);
    }
}
