package core;

import graphic_objects.GraphicObject;
import graphic_objects.figures.FigureInterface;
import graphic_objects.figures.Point2D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Никита on 08.10.2017.
 */
public class Group implements FigureInterface {

    private ArrayList<GraphicObject> content;

    public Group(){
        content = new ArrayList();
    }

    public Group(ArrayList<GraphicObject> figures){
        content = new ArrayList(figures);
    }

    public Iterator iterator(){
        return content.iterator();
    }

    public void add(GraphicObject go){
        content.add(go);
    }

    public void remove(GraphicObject go){
        content.remove(go);
    }

    public int size(){
        return content.size();
    }

    public boolean contains(GraphicObject go){
        return content.contains(go);
    }

    public int compareTo(Group group){
        if(content.size() != group.content.size())
            return ((Integer)content.size()).compareTo(group.content.size());

        if(this.equalTo(group))
            return 0;
        else
            return 1;
    }

    @Override
    public boolean contain(Point2D p) {
        return false;
    }

    @Override
    public boolean equalTo(FigureInterface fi) {
        if(!(fi instanceof Group))
            return false;
        ArrayList<GraphicObject> list = ((Group) fi).content;
        return !(list.size() != content.size() || !list.containsAll(content) || !content.containsAll(list));
    }

    @Override
    public Point2D getPoint(int id) {
        return null;
    }

    @Override
    public List<Point2D> getPoints() {
        return null;
    }
}
