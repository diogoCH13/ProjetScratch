package scratch.model;

import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;

public class Program {

    private final List<Action> actionList = new ArrayList<>();

    public void addAction(Action action){
        actionList.add(action);
    }

    public void removeAction(int index){
        actionList.remove(index);
    }

    public void removeAll(){
        actionList.clear();
    }
}
