package scratch.model;

import javafx.beans.Observable;

public abstract class NoExpressionAction extends Action{

    NoExpressionAction(){}

    @Override
    public Observable[] observables() {
        return new Observable[]{};
    }
}
