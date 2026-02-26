package scratch.model;

import java.util.ArrayList;
import java.util.List;

public class CursorWorld {

    private Cursor cursor;

    public Cursor getCursor() {
        return cursor;
    }
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    private final List<Segment>segments = new ArrayList<>();


    public void addSegment(Segment segment){
        segments.add(segment);
    }
}
