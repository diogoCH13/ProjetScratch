package scratch.model;

import java.util.ArrayList;
import java.util.List;

public class CursorWorld {

    private final List<Segment>segments = new ArrayList<>();
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

    public void addSegment(Segment segment){
        segments.add(segment);
    }

    public void reset(){
        segments.clear();
        cursor.reset();
    }



}
