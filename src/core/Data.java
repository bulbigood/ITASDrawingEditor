package core;

import graphic_objects.GraphicObject;
import graphic_objects.figures.Arc;
import graphic_objects.figures.Circle;
import graphic_objects.figures.Point2D;
import graphic_objects.figures.Segment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

import static core.DrawingArea.getSelection;
import static ui.MainWindow.getDrawingArea;

/**
 * Данные о фигурах
 */
public class Data {
    /**
     * Список фигур
     */
    private ArrayList<GraphicObject> figures = new ArrayList<>();

    /**
     * Список групп
     */
    private TreeSet<Group> groups = new TreeSet<>(new GroupComparator());

    /**
     * Список измерений
     */
    private ArrayList<GraphicObject> meterage = new ArrayList<>();

    /**
     * Возврощает список фигур
     *
     * @return список фигур
     */
    public List<GraphicObject> getFigures() {
        return figures;
    }

    /**
     * Добавляет фигуру
     *
     * @param f добавляемая фигура
     */
    public void addFigure(GraphicObject f) {
        figures.add(f);
    }

    /**
     * Удаляет заданную фигуру
     *
     * @param graphicObject фигура
     */
    public void removeFigure(GraphicObject graphicObject) {
        getSelection().select(graphicObject, false);
        getSelection().onSelectionChanged();
        getFigures().remove(graphicObject);
        getDrawingArea().repaint();
    }

    /**
     * Очищает список фигур
     */
    public void removeAllFigures() {
        getSelection().unselectAll();
        figures = new ArrayList<>();
    }

    /**
     * Устанавливает список фикур
     *
     * @param list список фигур
     * @throws CloneNotSupportedException
     */
    public void setFigures(ArrayList<GraphicObject> list) {
        removeAllFigures();
        for (GraphicObject f : list)
            figures.add(f.clone());
    }

    /////////////////////////////////////////////////////////////////

    public Group getNextGroup(GraphicObject fig, Group prev_group){
        Iterator iterator = groups.iterator();
        boolean next_group = prev_group == null;
        while(iterator.hasNext()){
            Group g = (Group)iterator.next();
            if(next_group && g.contains(fig))
                return g;
            if(!next_group && g.equals(prev_group))
                next_group = true;
        }
        return null;
    }

    public Group getGroup(ArrayList<GraphicObject> list){
        Group search = new Group(list);
        Iterator iterator = groups.iterator();

        while(iterator.hasNext()){
            Group g = (Group)iterator.next();
            if(g.equalTo(search))
                return g;
        }

        return null;
    }

    public void addGrope(Group group){
        groups.add(group);
    }

    public void removeGroup(Group group){
        groups.remove(group);
    }

    public void removeAllGroups(){
        groups.clear();
    }

    /////////////////////////////////////////////////////////////////

    /**
     * Возврощает список фигур
     *
     * @return список фигур
     */
    public List<GraphicObject> getMeters() {
        return meterage;
    }

    /**
     * Добавляет фигуру
     *
     * @param f добавляемая фигура
     */
    public void addMeter(GraphicObject f) {
        meterage.add(f);
    }

    /**
     * Удаляет заданную фигуру
     *
     * @param graphicObject фигура
     */
    public void removeMeter(GraphicObject graphicObject) {
        getSelection().select(graphicObject, false);
        getSelection().onSelectionChanged();
        getMeters().remove(graphicObject);
        getDrawingArea().repaint();
    }

    /**
     * Удаляет все измерители
     */
    public void removeAllMeters() {
        getSelection().unselectAll();
        meterage = new ArrayList<>();
    }


    /**
     * Возвращает JSON объект из фигур
     *
     * @return JSONObject
     */
    public JSONObject getJson() {
        JSONArray arcs = new JSONArray();
        JSONArray segments = new JSONArray();
        JSONArray circles = new JSONArray();
        JSONArray groups = new JSONArray();

        for (GraphicObject f : figures) {
            if (f instanceof Arc) {
                JSONObject obj = new JSONObject();
                obj.put("hash", f.hashCode());
                obj.put("r", ((Arc) f).getRadius());
                obj.put("startAngle", ((Arc) f).getStartingAngle());
                JSONObject center = new JSONObject();
                center.put("x", f.getCenter().getX());
                center.put("y", f.getCenter().getY());
                obj.put("center", center);
                obj.put("endAngle", ((Arc) f).getStartingAngle() + ((Arc) f).getAngularExtent());
                arcs.add(obj);
            } else if (f instanceof Segment) {
                JSONObject obj = new JSONObject();
                obj.put("hash", f.hashCode());
                JSONObject p1 = new JSONObject();
                p1.put("x", f.getPoint(0).getX());
                p1.put("y", f.getPoint(0).getY());
                obj.put("p1", p1);
                JSONObject p2 = new JSONObject();
                p2.put("x", f.getPoint(1).getX());
                p2.put("y", f.getPoint(1).getY());
                obj.put("p2", p2);
                segments.add(obj);
            } else if (f instanceof Circle) {
                JSONObject obj = new JSONObject();
                obj.put("hash", f.hashCode());
                JSONObject center = new JSONObject();
                center.put("x", f.getCenter().getX());
                center.put("y", f.getCenter().getY());
                obj.put("center", center);
                obj.put("r", ((Circle) f).getRadius());
                circles.add(obj);
            }
        }

        for(Group g : this.groups){
            JSONArray arr = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("hash", g.hashCode());
            Iterator iterator = g.iterator();
            while(iterator.hasNext()) {
                JSONObject fig = new JSONObject();
                fig.put("figure", iterator.next().hashCode());
                arr.add(fig);
            }
            obj.put("elements", arr);
            groups.add(obj);
        }

        JSONArray morphingPairs_json = new JSONArray();
        Iterator iterator = getDrawingArea().getMorpher().iterator();
        while(iterator.hasNext()){
            MorphingPair mp = (MorphingPair) iterator.next();

            JSONObject obj = new JSONObject();
            obj.put("hash", mp.hashCode());
            obj.put("start_group", mp.getStartingGroup().hashCode());
            obj.put("end_group", mp.getEndingGroup().hashCode());

            morphingPairs_json.add(obj);
        }

        JSONObject figures = new JSONObject();
        figures.put("arcs", arcs);
        figures.put("segments", segments);
        figures.put("circles", circles);

        JSONObject result = new JSONObject();
        result.put("figures", figures);
        result.put("groups", groups);
        result.put("morphing_pairs", morphingPairs_json);

        return result;
    }

    /**
     * Строит фигуры из JSON
     *
     * @param input строка со свойствами фигур в JSON
     * @return список фигур
     */
    public void loadJson(String input) {
        ArrayList<Integer> figuresHashes = new ArrayList<>();
        ArrayList<Integer> groupsHashes = new ArrayList<>();
        ArrayList<Group> unsortedGroups = new ArrayList<>();

        JSONParser parser = new JSONParser();
        JSONObject mainObject = null;
        try {
            mainObject = (JSONObject) parser.parse(input);

            JSONObject figures_json = (JSONObject) mainObject.get("figures");
            JSONArray groups_json = (JSONArray) mainObject.get("groups");
            JSONArray morphs_json = (JSONArray) mainObject.get("morphing_pairs");

            JSONArray segments = (JSONArray) figures_json.get("segments");
            JSONArray arcs = (JSONArray) figures_json.get("arcs");
            JSONArray circles = (JSONArray) figures_json.get("circles");


            for (Object segment : segments) {
                JSONObject obj = (JSONObject) segment;
                int hash = Integer.parseInt(obj.get("hash").toString());
                JSONObject p1 = (JSONObject) (obj).get("p1");
                JSONObject p2 = (JSONObject) (obj).get("p2");
                double p1x = Double.parseDouble(p1.get("x").toString());
                double p1y = Double.parseDouble(p1.get("y").toString());
                double p2x = Double.parseDouble(p2.get("x").toString());
                double p2y = Double.parseDouble(p2.get("y").toString());

                Segment newFigure = new Segment();
                newFigure.setSegment(new Point2D(p1x, p1y), new Point2D(p2x, p2y));
                //figures.add(newFigure);
                figuresHashes.add(hash);
            }

            for (Object arc : arcs) {
                JSONObject obj = (JSONObject) arc;
                int hash = Integer.parseInt(obj.get("hash").toString());
                JSONObject center = (JSONObject) obj.get("center");
                double x = Double.parseDouble(center.get("x").toString());
                double y = Double.parseDouble(center.get("y").toString());
                double r = Double.parseDouble(obj.get("r").toString());
                double startAngle = Double.parseDouble(obj.get("startAngle").toString());
                double endAngle = Double.parseDouble(obj.get("endAngle").toString());

                Arc newFigure = new Arc();
                newFigure.setArc(new Point2D(x, y), r, startAngle, endAngle);
                //figures.add(newFigure);
                figuresHashes.add(hash);
            }

            for (Object circle : circles) {
                JSONObject obj = (JSONObject) circle;
                int hash = Integer.parseInt(obj.get("hash").toString());
                JSONObject center = (JSONObject) obj.get("center");
                double x = Double.parseDouble(center.get("x").toString());
                double y = Double.parseDouble(center.get("y").toString());
                double r = Double.parseDouble(obj.get("r").toString());

                Circle newFigure = new Circle();
                newFigure.setCircle(new Point2D(x, y), r);
                //figures.add(newFigure);
                figuresHashes.add(hash);
            }

            //Чтение групп
            for (Object group : groups_json) {
                JSONObject group_json = (JSONObject) group;
                JSONArray group_elems_json = (JSONArray) group_json.get("elements");

                Group new_group = new Group();

                for(Object fig : group_elems_json){
                    JSONObject fig_json = (JSONObject) fig;
                    int hash_in_group = Integer.parseInt(fig_json.get("figure").toString());
                    int fig_index = figuresHashes.indexOf(hash_in_group);
                    GraphicObject go = figures.get(fig_index);
                    go.setGrouped(true);
                    new_group.add(go);
                }

                int group_hash = Integer.parseInt(group_json.get("hash").toString());
                groupsHashes.add(group_hash);
                unsortedGroups.add(new_group);
            }
            groups.addAll(unsortedGroups);

            //Чтение морфинга
            for(Object mp : morphs_json){
                JSONObject mp_json = (JSONObject) mp;
                int mp_hash = Integer.parseInt(mp_json.get("start_group").toString());
                Group start_group = unsortedGroups.get(groupsHashes.indexOf(mp_hash));
                mp_hash = Integer.parseInt(mp_json.get("end_group").toString());
                Group end_group = unsortedGroups.get(groupsHashes.indexOf(mp_hash));

                getDrawingArea().getMorpher().setStartingPairGroup(start_group);
                getDrawingArea().getMorpher().setEndingPairGroup(end_group);
                getDrawingArea().getMorpher().finalizeMorphingPair();
            }
        } catch (ParseException e) {
            Log.add("Файл поврежден или имеет другой формат!");
            e.printStackTrace();
        }
    }

    private class GroupComparator implements Comparator<Group> {
        @Override
        public int compare(Group o1, Group o2) {
            return o2.compareTo(o1);
        }
    }
}
