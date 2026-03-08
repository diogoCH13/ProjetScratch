package scratch.model;

import java.util.ArrayList;
import java.util.List;

public class CursorWorld {

    private final List<Segment> segments = new ArrayList<>();

    public List<Segment> getSegments() {
        return segments;
    }

    public void addSegment(Segment segment){
        segments.add(segment);
    }

    public void clearSegments(){
        segments.clear();
    }



}
