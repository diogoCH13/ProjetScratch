package scratch.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scratch.model.ActionType;
import scratch.model.ActionsCatalog;
import scratch.model.Program;

public class PalettListViewModel {
    private final IntegerProperty catalogIndex = new SimpleIntegerProperty(-1);
    private final BooleanProperty advancedMode = new SimpleBooleanProperty(false);
    private final ObservableList<ActionType> palettActions = FXCollections.observableArrayList();

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
        refreshPalettActions();
        configureBindingsAndListeners();
    }

    public IntegerProperty catalogIndexProperty() {
        return catalogIndex;
    }

    public BooleanProperty advancedModeProperty() {
        return advancedMode;
    }

    public ObservableList<ActionType> getPalettAction() {
        return palettActions;
    }

    public void addAction() {
        int destinationIndex = actionIndex.get();
        int i = catalogIndex.get();
        if (i < 0 || i >= palettActions.size()) {
            return;
        }

        ActionType type = palettActions.get(i);
        if (destinationIndex >= 0) {
            program.addAction(type, destinationIndex + 1);
        }
        else {
            program.addAction(type, program.getActionsList().size());
        }
        programLoaded.set(false);
        running.set(false);
    }

    private void configureBindingsAndListeners() {
        advancedMode.addListener((obs, oldValue, newValue) -> {
            refreshPalettActions();
        });
    }

    private void refreshPalettActions() {
        palettActions.setAll(ActionsCatalog.getBasicActions());

        if (advancedMode.get()) {
            palettActions.addAll(ActionsCatalog.getAdvancedActions());
        }

        catalogIndex.set(-1);
    }
}
