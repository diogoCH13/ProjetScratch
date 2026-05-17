package scratch.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import scratch.model.ActionType;
import scratch.model.ActionsCatalog;
import scratch.model.Program;

public class PalettListViewModel {
    private final IntegerProperty catalogIndex = new SimpleIntegerProperty(-1);

    private final Program program;
    private final IntegerProperty actionIndex;
    private final BooleanProperty programLoaded;
    private final BooleanProperty running;

    public PalettListViewModel(Program program,
                               IntegerProperty actionIndex,
                               BooleanProperty programLoaded,
                               BooleanProperty running) {
        this.program = program;
        this.actionIndex = actionIndex;
        this.programLoaded = programLoaded;
        this.running = running;
    }

    public IntegerProperty catalogIndexProperty() {
        return catalogIndex;
    }

    public ObservableList<ActionType> getPalettAction() {
        return ActionsCatalog.getPalettActions();
    }

    public void addAction() {
        int destinationIndex = actionIndex.get();
        int i = catalogIndex.get();
        if (destinationIndex >= 0) {
            program.addAction(i, destinationIndex + 1);
        }
        else {
            program.addAction(i, program.getActions().size());
        }
        programLoaded.set(false);
        running.set(false);
    }
}
