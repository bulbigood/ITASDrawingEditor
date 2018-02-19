package core;

import graphic_objects.GraphicObject;
import graphic_objects.figures.Arc;
import graphic_objects.figures.Circle;
import graphic_objects.figures.FractalTree;
import graphic_objects.figures.Segment;
import graphic_objects.meters.Protractor;
import graphic_objects.meters.Ruler;

/**
 * Хранит и адресует режимы
 */
public enum ToolMode {
    CURSOR,
    DRAW_SEGMENT,
    DRAW_CYCLE_SEGMENT,
    DRAW_CIRCLE,
    DRAW_ARC,
    DRAW_TREE,
    RULER,
    PROTRACTOR;

    public Class<? extends GraphicObject> getDrawClass() {
        switch (this) {
            case DRAW_CIRCLE:
                return Circle.class;
            case DRAW_SEGMENT:
                return Segment.class;
            case DRAW_CYCLE_SEGMENT:
                return Segment.class;
            case DRAW_ARC:
                return Arc.class;
            case DRAW_TREE:
                return FractalTree.class;
            case RULER:
                return Ruler.class;
            case PROTRACTOR:
                return Protractor.class;
        }
        return null;
    }
}