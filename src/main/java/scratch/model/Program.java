package scratch.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;

public class Program {

    private final ObservableList<Action> actions = FXCollections.observableArrayList();

    public ObservableList<Action> getActions() {
        return FXCollections.unmodifiableObservableList(actions);
    }

    public void addAction(int index){
        ActionType type = ActionsCatalog.getAction(index);
        if (type != null){
            Action action = ActionFactory.createAction(type);
            actions.add(action);
        }
    }

    public void removeAction(int index){
        actions.remove(index);
    }

    public void removeAll(){
        actions.clear();
    }
}
