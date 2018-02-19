package core.geometry_algorithms;

import graphic_objects.GraphicObject;
import graphic_objects.figures.Point2D;
import graphic_objects.figures.Segment;

import java.util.ArrayList;

/**
 * Created by Никита on 14.11.2017.
 */
public class MatrixTransformer {

    public static boolean transformObjects(ArrayList<GraphicObject> objects, double[][] matrix, Point2D oxy){
        double[][] trans_mat = new double[3][3];
        trans_mat[0][0] = 1;
        trans_mat[1][1] = 1;
        trans_mat[2][2] = 1;
        trans_mat[2][0] = -oxy.getX();
        trans_mat[2][1] = -oxy.getY();
        matrix = multiplyByMatrix(trans_mat, matrix);

        trans_mat[2][0] = -trans_mat[2][0];
        trans_mat[2][1] = -trans_mat[2][1];
        matrix = multiplyByMatrix(matrix, trans_mat);

        for(GraphicObject obj : objects){
            if(obj instanceof Segment){
                double[][] seg_mat = new double[2][3];
                seg_mat[0][0] = obj.getPoint(0).getX();
                seg_mat[0][1] = obj.getPoint(0).getY();
                seg_mat[0][2] = 1;
                seg_mat[1][0] = obj.getPoint(1).getX();
                seg_mat[1][1] = obj.getPoint(1).getY();
                seg_mat[1][2] = 1;

                seg_mat = multiplyByMatrix(seg_mat, matrix);

                seg_mat[0][0] /= seg_mat[0][2];
                seg_mat[0][1] /= seg_mat[0][2];
                seg_mat[1][0] /= seg_mat[1][2];
                seg_mat[1][1] /= seg_mat[1][2];

                obj.getPoint(0).setX(seg_mat[0][0]);
                obj.getPoint(0).setY(seg_mat[0][1]);
                obj.getPoint(1).setX(seg_mat[1][0]);
                obj.getPoint(1).setY(seg_mat[1][1]);
                obj.refreshPoints();
            }
        }
        return true;
    }

    /**
     * Matrix multiplication method.
     * @param m1 Multiplicand
     * @param m2 Multiplier
     * @return Product
     */
    public static double[][] multiplyByMatrix(double[][] m1, double[][] m2) {
        int m1ColLength = m1[0].length; // m1 columns length
        int m2RowLength = m2.length;    // m2 rows length
        if(m1ColLength != m2RowLength) return null; // matrix multiplication is not possible
        int mRRowLength = m1.length;    // m result rows length
        int mRColLength = m2[0].length; // m result columns length
        double[][] mResult = new double[mRRowLength][mRColLength];
        for(int i = 0; i < mRRowLength; i++) {         // rows from m1
            for(int j = 0; j < mRColLength; j++) {     // columns from m2
                for(int k = 0; k < m1ColLength; k++) { // columns from m1
                    mResult[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }
        return mResult;
    }
}
