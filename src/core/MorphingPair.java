package core;

import graphic_objects.GraphicObject;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Никита on 26.10.2017.
 */
public class MorphingPair {
    private Group starting_group = null;
    private Group ending_group = null;
    private ArrayList<ArrayList<Point2D>> start_points;
    private ArrayList<ArrayList<Point2D>> end_points;

    private boolean start_segmentated = false;
    private boolean end_segmentated = false;
    private boolean segmentated = false;

    private int start_segments_num;
    private int end_segments_num;

    private double morphing_factor;
    private double morphing_time;

    public MorphingPair(Group start, Group end, double factor, double time){
        starting_group = start;
        ending_group = end;
        morphing_factor = factor;
        morphing_time = time;
    }

    public void segmentateStartingGroup(int n){
        start_segments_num = n;
        start_points = new ArrayList<>();
        Iterator iterator = starting_group.iterator();
        while(iterator.hasNext())
            start_points.add(((GraphicObject) iterator.next()).getSegmentatedObject(n));

        start_segmentated = true;
        if(start_segmentated && end_segmentated)
            segmentated = true;
    }

    public void segmentateEndingGroup(int n){
        end_segments_num = n;
        end_points = new ArrayList<>();
        Iterator iterator = ending_group.iterator();
        while(iterator.hasNext())
            end_points.add(((GraphicObject) iterator.next()).getSegmentatedObject(n));

        end_segmentated = true;
        if(start_segmentated && end_segmentated)
            segmentated = true;
    }

    public int getSegmentsNum() {
        int a = 0, b = 0;
        for(int i = 0; i < start_points.size(); i++)
            a += start_points.get(i).size();
        for(int i = 0; i < end_points.size(); i++)
            b += end_points.get(i).size();
        return Math.max(a, b);
    }

    public void refreshPoints(){
        start_points.clear();
        end_points.clear();

        Iterator iterator = starting_group.iterator();
        while(iterator.hasNext())
            start_points.add(((GraphicObject) iterator.next()).getSegmentatedObject(start_segments_num));

        iterator = ending_group.iterator();
        while(iterator.hasNext())
            end_points.add(((GraphicObject) iterator.next()).getSegmentatedObject(end_segments_num));
    }

    public boolean isSegmentated(){
        return segmentated;
    }

    public void setMorphingFactor(double factor){
        morphing_factor = factor;
    }

    public void setMorphingTime(double time){
        morphing_time = time;
    }

    public Group getStartingGroup(){
        return starting_group;
    }

    public Group getEndingGroup(){
        return ending_group;
    }

    public ArrayList<ArrayList<Point2D>> getStartPoints(){
        return start_points;
    }

    public ArrayList<ArrayList<Point2D>> getEndPoints(){
        return end_points;
    }

    public double getMorphingFactor(){
        return morphing_factor;
    }

    public double getMorphingTime(){
        return morphing_time;
    }
}
