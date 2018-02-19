package core;

import graphic_objects.GraphicObject;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import static ui.MainWindow.getDrawingArea;

/**
 * Created by Никита on 25.10.2017.
 */
public class Morpher implements Runnable {
    public static final int SEGMENTATION_LEVEL = 100;
    public static final int COMPUTE_DELTA_TIME = 33;

    public static double ANIMATION_SPEED = 1;

    private ArrayList<ArrayList<Point2D>> points;
    private ArrayList<MorphingPair> pairs;

    private long start_time = 0;
    private long delta_time = 0;
    private boolean working = false;
    private boolean pause = false;

    private Group starting_group = null;
    private Group ending_group = null;
    private boolean is_creating = false;

    public Morpher(){
        pairs = new ArrayList();
    }

    public Iterator iterator(){
        return pairs.iterator();
    }

    public void setStartingPairGroup(Group start){
        if(start != null) {
            is_creating = true;
            starting_group = start;
        }
    }

    public void setEndingPairGroup(Group end){
        if(end != null) {
            is_creating = true;
            ending_group = end;
        }
    }

    public Group getStartingPairGroup(){
        return starting_group;
    }

    public Group getEndingPairGroup(){
        return ending_group;
    }

    public boolean finalizeMorphingPair(){
        if(starting_group != null && starting_group != null) {
            Iterator iterator = starting_group.iterator();
            while(iterator.hasNext())
                ((GraphicObject)iterator.next()).setMorphed(true);
            iterator = ending_group.iterator();
            while(iterator.hasNext())
                ((GraphicObject)iterator.next()).setMorphed(true);
            MorphingPair mp = new MorphingPair(starting_group, ending_group, 1, 1000);

            int segments = SEGMENTATION_LEVEL * (int)lcm(ending_group.size(), starting_group.size());
            mp.segmentateStartingGroup(segments / starting_group.size());
            mp.segmentateEndingGroup(segments / ending_group.size());

            pairs.add(mp);
            removeCreatingMorphingPair();
            return true;
        }
        return false;
    }

    public void removeCreatingMorphingPair(){
        is_creating = false;
        starting_group = null;
        ending_group = null;
    }

    public boolean isCreatingMorphingPair(){
        return is_creating;
    }

    public void removeMorphingPair(Group group){
        Iterator iterator = pairs.iterator();
        while(iterator.hasNext()){
            MorphingPair mp = (MorphingPair)iterator.next();
            Group start = mp.getStartingGroup();
            Group end = mp.getEndingGroup();

            Iterator group_iterator = start.iterator();
            while(group_iterator.hasNext())
                ((GraphicObject)group_iterator.next()).setMorphed(false);
            group_iterator = end.iterator();
            while(group_iterator.hasNext())
                ((GraphicObject)group_iterator.next()).setMorphed(false);

            if(start.equalTo(group) || end.equalTo(group)){
                iterator.remove();
            }
        }
    }

    public void removeAll(){
        Iterator iterator = pairs.iterator();
        while(iterator.hasNext()){
            MorphingPair mp = (MorphingPair)iterator.next();
            Group start = mp.getStartingGroup();
            Group end = mp.getEndingGroup();

            Iterator group_iterator = start.iterator();
            while(group_iterator.hasNext())
                ((GraphicObject)group_iterator.next()).setMorphed(false);
            group_iterator = end.iterator();
            while(group_iterator.hasNext())
                ((GraphicObject)group_iterator.next()).setMorphed(false);

            iterator.remove();
        }

        points = null;
    }

    public boolean contains(Group group){
        for(MorphingPair mp : pairs) {
            Group start = mp.getStartingGroup();
            Group end = mp.getEndingGroup();
            if (start.equalTo(group) || end.equalTo(group)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<ArrayList<Point2D>> getPoints(){
        return points;
    }

    @Override
    public void run() {
        working = true;
        start_time = System.currentTimeMillis();
        while(working){
            if(!pause) {
                delta_time = System.currentTimeMillis() - start_time;
                ArrayList<ArrayList<Point2D>> vr_points = new ArrayList<>();
                for (int a = 0; a < pairs.size(); a++) {
                    MorphingPair mp = pairs.get(a);
                    ArrayList<ArrayList<Point2D>> start_points = mp.getStartPoints();
                    ArrayList<ArrayList<Point2D>> end_points = mp.getEndPoints();

                    int seg_num = vr_points.size();
                    vr_points.add(new ArrayList<Point2D>());
                    ArrayList<Point2D> cur_seg_group = vr_points.get(seg_num);
                    int size1 = start_points.get(0).size();
                    int size2 = end_points.get(0).size();
                    boolean s1_bigger = start_points.size() > end_points.size();
                    int s1 = 0, s2 = 0;
                    for(int p = 0; p < mp.getSegmentsNum(); p++){
                        if(p >= size1 * (s1 + 1)) {
                            s1++;
                            if(s1_bigger) {
                                seg_num++;
                                vr_points.add(new ArrayList<Point2D>());
                                cur_seg_group = vr_points.get(seg_num);
                            }
                        }
                        if(p >= size2 * (s2 + 1)) {
                            s2++;
                            if(!s1_bigger) {
                                seg_num++;
                                vr_points.add(new ArrayList<Point2D>());
                                cur_seg_group = vr_points.get(seg_num);
                            }
                        }

                        Point2D p1 = start_points.get(s1).get(p - s1 * size1);
                        Point2D p2 = end_points.get(s2).get(p - s2 * size2);
                        cur_seg_group.add(getIntermediatePoint(p1, p2, mp.getMorphingFactor(), mp.getMorphingTime()));
                    }
                }

                points = vr_points;
                getDrawingArea().repaint();
            }
            try {
                Thread.sleep(COMPUTE_DELTA_TIME);
            } catch(Exception e){
                System.out.println(e.getStackTrace());
            }
        }
    }

    public boolean isRunning(){
        return working;
    }

    public boolean isPaused(){
        return pause;
    }

    public void stop(){
        working = false;
        points = null;
    }

    public void pause(boolean p){
        pause = p;
        start_time = System.currentTimeMillis() - delta_time;
    }

    private Point2D getIntermediatePoint(Point2D a, Point2D b, double factor, double anim_time){
        double k = (delta_time % anim_time) / anim_time;
        if(delta_time % (2*anim_time) < anim_time)
            k = 1 - k;
        k *= factor;

        return new Point2D.Double(k * a.getX() + (1-k) * b.getX(), k * a.getY() + (1-k) * b.getY());
    }

    public long gcd(long a,long b){
        return b == 0 ? a : gcd(b,a % b);
    }

    public long lcm(long a,long b){
        return a / gcd(a,b) * b;
    }
}
