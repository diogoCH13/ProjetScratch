package scratch.viewmodel;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import scratch.model.Cursor;
import scratch.model.CursorWorld;
import scratch.model.Point;
import scratch.model.Program;
import scratch.model.Action;
import scratch.model.VariableRow;

import java.util.Map;

public class SceneTurtleViewModel {

    private Timeline timeline;

    private final Cursor cursor;
    private final Program program;
    private final ObservableList<Action> programActions;
    private final CursorWorld world;
    private final IntegerProperty actionIndex;
    private final IntegerProperty catalogIndex;

    private final BooleanBinding actionsValide;
    private final BooleanProperty programLoaded;
    private final BooleanProperty running;
    private final BooleanProperty executionFinished;
    private final BooleanProperty runtimeValue;
    private final BooleanProperty actionError;
    private final BooleanProperty autoMode;
    private final DoubleProperty speed = new SimpleDoubleProperty(1);

    private final IntegerProperty sceneVersion = new SimpleIntegerProperty(0);

    private final ObservableList<VariableRow> variableValueList = FXCollections.observableArrayList();

    public SceneTurtleViewModel(Cursor cursor, Program program, IntegerProperty actionIndex, IntegerProperty catalogIndex,
                                BooleanProperty programLoaded, BooleanProperty running, BooleanProperty executionFinished,
                                BooleanProperty runtimeValue, BooleanProperty actionError, BooleanProperty autoMode) {
        this.cursor = cursor;
        this.program = program;
        this.programActions = program.getActionsList();
        this.world = cursor.getWorld();
        this.actionIndex = actionIndex;
        this.catalogIndex = catalogIndex;
        this.programLoaded = programLoaded;
        this.running = running;
        this.executionFinished = executionFinished;
        this.runtimeValue = runtimeValue;
        this.actionError = actionError;
        this.autoMode = autoMode;
        this.actionsValide = Bindings.createBooleanBinding(
                () -> program.isPenStateValid(),
                programActions
        );
        configureBindingsAndListeners();
    }

    public IntegerProperty sceneVersionProperty() {
        return sceneVersion;
    }

    public ObservableList<VariableRow> getVariableValueList() {
        return variableValueList;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public Point getPoint() {
        return cursor.getPosition();
    }

    public CursorWorld getWorld() {
        return world;
    }

    public BooleanProperty runningProperty() {
        return running;
    }

    public BooleanProperty autoModeProperty() {
        return autoMode;
    }

    public BooleanProperty runtimeValueProperty() {
        return runtimeValue;
    }

    public boolean isProgramLoaded() {
        return programLoaded.get();
    }

    public boolean isRunning() {
        return running.get();
    }

    public boolean isAutoMode() {
        return autoMode.get();
    }

    public BooleanProperty programLoadedProperty() {
        return programLoaded;
    }

    public BooleanBinding canLoad() {
        return Bindings.createBooleanBinding(() -> {
            return !programActions.isEmpty() &&
                    actionsValide.get() &&
                    program.declarationIsFirstAction() &&
                    program.isRepeatCounterModificationValid() &&
                    program.isEndRepeatExist() &&
                    program.actionsNotEmpty() &&
                    program.varIsExist() &&
                    program.declareIsUnique() &&
                    program.parametersAreCorrect() &&
                    program.lastActionIsCorrect() &&
                    !actionError.get();
        }, programActions, actionIndex, actionError);
    }

    public BooleanProperty canExecute() {
        return programLoaded;
    }

    public BooleanProperty executionFinishedProperty() {
        return executionFinished;
    }

    public void configureBindingsAndListeners() {
        speedProperty().addListener((obs, oldVal, newVal) -> {
            if (timeline != null) {
                timeline.setRate(1 / newVal.doubleValue());
            }
        });

        programActions.addListener((InvalidationListener) observable -> {
            invalidateExecutionDisplay();
        });
    }

    public void reset() {
        clearExecutionDisplay();
        actionIndex.set(-1);
        catalogIndex.set(0);
    }

    public void loadProgram() {
        reset();
        programLoaded.set(true);
        executionFinished.set(false);
    }

    public void execute() {
        clearExecutionDisplay();
        running.set(true);
        executionFinished.set(false);
        program.startExecution();
        actionIndex.set(program.getCurrentIndex());
    }

    public void next() {
        try {
            int index = program.getCurrentIndex();

            if (index >= 0 && index < programActions.size()) {
                actionIndex.set(index);
            }

            program.executeNext(cursor);
            //permet de redessiner la scene à chaque nouvelle action
            sceneVersion.set(sceneVersion.get() + 1);
            runtimeValue.set(false);
            updateVariablesList();

            if (program.isExecutionFinished()) {
                running.set(false);
                executionFinished.set(true);
                stopTimeline();
            }
        } catch (RuntimeException exception) {
            runtimeValue.set(true);
            running.set(false);
            executionFinished.set(true);
            stopTimeline();
        }
    }

    public DoubleProperty cursorXProperty() {
        return cursor.getPosition().xProperty();
    }

    public DoubleProperty cursorYProperty() {
        return cursor.getPosition().yProperty();
    }

    public DoubleProperty cursorAngelProperty() {
        return cursor.angleProperty();
    }

    public DoubleProperty speedProperty() {
        return speed;
    }

    public void newFile() {
        program.clearAll();
        clearExecutionDisplay();
        catalogIndex.set(-1);
        actionIndex.set(-1);
        programLoaded.set(false);
    }

    public void autoNext() {
        stopTimeline();

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            if (!runtimeValueProperty().get()) {
                next();
            }
            if (runtimeValue.get() || program.isExecutionFinished()) {
                stopTimeline();
                running.set(false);
                executionFinished.set(true);
            }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        running.set(true);
        executionFinished.set(false);
        timeline.setRate(1 / speed.get());
        timeline.play();
    }

    public void stopAutoMode() {
        stopTimeline();
        running.set(false);
    }

    public void updateVariablesList() {
        variableValueList.clear();
        for (Map.Entry<String, Integer> entry : program.getDeclarationList().entrySet()) {
            variableValueList.add(new VariableRow(entry.getKey(), entry.getValue()));
        }
    }

    private void stopTimeline() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    private void clearExecutionDisplay() {
        world.clearSegments();
        sceneVersion.set(sceneVersion.get() + 1);
        variableValueList.clear();
        program.clearDeclarationList();
        cursor.reset();
        runtimeValue.set(false);
        running.set(false);
        executionFinished.set(false);
        program.resetStack();
        stopTimeline();
    }

    private void invalidateExecutionDisplay() {
        clearExecutionDisplay();
        programLoaded.set(false);
    }
}
