package scratch.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import scratch.model.Cursor;
import scratch.model.Program;

import java.io.File;

public class MainViewModel {

    private final Program program;
    private final ActionListViewModel actionListViewModel;
    private final PalettListViewModel palettListViewModel;
    private final SceneTurtleViewModel sceneTurtleViewModel;

    private final BooleanProperty programLoaded = new SimpleBooleanProperty(false);
    private final BooleanProperty running = new SimpleBooleanProperty(false);
    private final BooleanProperty executionFinished = new SimpleBooleanProperty(false);
    private final BooleanProperty runtimeValue = new SimpleBooleanProperty(false);
    private final BooleanProperty autoMode = new SimpleBooleanProperty(false);

    public MainViewModel(Cursor cursor) {
        this.program = new Program();
        this.actionListViewModel = new ActionListViewModel(program, programLoaded, running, executionFinished, runtimeValue);
        this.palettListViewModel = new PalettListViewModel(program, actionListViewModel.actionIndexProperty(), programLoaded, running);
        this.sceneTurtleViewModel = new SceneTurtleViewModel(cursor, program, actionListViewModel.actionIndexProperty(),
                palettListViewModel.catalogIndexProperty(), programLoaded, running, executionFinished, runtimeValue,
                actionListViewModel.errorValueProperty(), autoMode);
    }

    public ActionListViewModel getActionListViewModel() {
        return actionListViewModel;
    }

    public PalettListViewModel getPalettListViewModel() {
        return palettListViewModel;
    }

    public SceneTurtleViewModel getSceneTurtleViewModel() {
        return sceneTurtleViewModel;
    }

    public void newFile() {
        sceneTurtleViewModel.newFile();
    }

    public void saveFile(File file) {
        program.save(file);
    }

    public void openFile(File file) {
        sceneTurtleViewModel.reset();
        program.load(file);
        programLoaded.set(false);

    }
}
