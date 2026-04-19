package scratch.model;

import javafx.collections.ObservableList;

public class ExecutionContext {

    private ObservableList<Action> currentActions;
    private int currentIndex;

    // pour Repeat
    private int remainingIterations;
    private int repeatStartIndex;
    private int repeatEndIndex;

    // constructeur pour contexte normal (hors boucle)
    public ExecutionContext(ObservableList<Action> currentActions, int currentIndex) {
        this.currentActions = currentActions;
        this.currentIndex = currentIndex;
        this.remainingIterations = -1;
        this.repeatStartIndex = -1;
        this.repeatEndIndex = -1;
    }

    // constructeur pour Repeat
    public ExecutionContext(ObservableList<Action> currentActions,
                            int currentIndex,
                            int remainingIterations,
                            int repeatStartIndex,
                            int repeatEndIndex) {

        this.currentActions = currentActions;
        this.currentIndex = currentIndex;
        this.remainingIterations = remainingIterations;
        this.repeatStartIndex = repeatStartIndex;
        this.repeatEndIndex = repeatEndIndex;
    }

    // getters / setters

    public ObservableList<Action> getCurrentActions() {
        return currentActions;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public int getRemainingIterations() {
        return remainingIterations;
    }

    public void setRemainingIterations(int remainingIterations) {
        this.remainingIterations = remainingIterations;
    }

    public int getRepeatStartIndex() {
        return repeatStartIndex;
    }

    public int getRepeatEndIndex() {
        return repeatEndIndex;
    }
}