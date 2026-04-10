package scratch.model;

import javafx.beans.DefaultProperty;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.io.*;
import java.util.*;

public class Program {

    private final ObservableList<Action> actions = FXCollections.observableArrayList( action -> new Observable[]{ action.valueProperty() });
    private final Deque<ExecutionContext> executionStack = new ArrayDeque<>();
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

    public void startExecution(){
        executionStack.clear();
        ExecutionContext context = new ExecutionContext(actions, 0);
        executionStack.push(context);
    }

    public void executeNext(Cursor cursor){
        if (executionStack.isEmpty()){
            return;
        }
        ExecutionContext context = executionStack.peek();
        int index = context.getCurrentIndex();
        ObservableList<Action>list = context.getCurrentActions();
        if (index >= list.size()){
            executionStack.pop();
            return;
        }
        Action action = list.get(index);
        if (action.actionType() != ActionType.REPEAT && action.actionType() != ActionType.END_REPEAT){
            action.execute(cursor);
            context.setCurrentIndex(index + 1);

            if (context.getCurrentIndex() >= list.size()) {
                executionStack.pop();
            }
            return;
        }

        if (action.actionType() == ActionType.REPEAT){
            int cpt = (int)action.getValue();
            if (cpt <= 0){
                int endIndex = findMatchingEndRepeat(index);
                context.setCurrentIndex(endIndex + 1);
            }
            else {
                int endIndex = findMatchingEndRepeat(index);
                executionStack.push(
                        new ExecutionContext(
                                list,
                                index + 1,
                                cpt,
                                index,
                                endIndex
                        )
                );
                return;
            }
        } else if (action.actionType() == ActionType.END_REPEAT) {
            ExecutionContext ctx = executionStack.peek();
            int cpt = ctx.getRemainingIterations();
            if (cpt > 1){
                ctx.setRemainingIterations(cpt - 1);
                ctx.setCurrentIndex(ctx.getRepeatStartIndex() + 1);
            }else {
                executionStack.pop();
                if (!executionStack.isEmpty()) {
                    ExecutionContext parent = executionStack.peek();
                    parent.setCurrentIndex(ctx.getRepeatEndIndex() + 1);
                }
            }
            return;
        }


    }

    public int findMatchingEndRepeat(int index){
        int cpt =  1;
        for (int i = index + 1; i < actions.size(); ++i){
            Action action = actions.get(i);
            if (action.actionType() == ActionType.REPEAT){
                ++cpt;
            }
            if (action.actionType() == ActionType.END_REPEAT){
                --cpt;
                if (cpt == 0){
                    return i;
                }
            }
        }
        return -1;
    }

    public int getCurrentIndex(){
        if (!executionStack.isEmpty()){
            return executionStack.peek().getCurrentIndex();
        }
        return -1;
    }

    public boolean isExecutionFinished(){
        return executionStack.isEmpty();
    }

    public void resetStack(){
        executionStack.clear();
    }


    public void deleteAction(int index){
        actions.remove(index);
    }

    public void removeAll(){
        actions.clear();
    }

    public void up(int index){
        if (index > 0 && index < actions.size()){
//            Action action = actions.set(index, actions.get(index - 1));
//            actions.set(index - 1, action);
            Collections.swap(actions, index, index - 1);
        }
    }

    public void down(int index){
        if (index >= 0 && index < actions.size() - 1){
            Action action = actions.set(index, actions.get(index + 1));
            actions.set(index + 1, action);
        }
    }

    public void duplicate(Action action) {

        switch (action.actionType()) {

            case PEN_UP -> actions.add(new Pen(false));

            case PEN_DOWN -> actions.add(new Pen(true));

            case TURN_LEFT, TURN_RIGHT -> actions.add(new Turn(action.getValue()));

            default -> actions.add(new MoveForward(action.getValue()));
        };
    }

//    public void executeNext(int index, Cursor cursor){
//        if (index < actions.size()){
//            Action action = actions.get(index);
//            action.execute(cursor);
//        }
//    }

    public void save(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (Action action : actions) {
                writer.println(action.actionType().name() + ";" + action.getValue());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(File file) {

        actions.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");

                ActionType type = ActionType.valueOf(parts[0]);
                double value = Double.parseDouble(parts[1]);

                Action action = ActionFactory.createAction(type);
                action.setValue(value);
                actions.add(action);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean isPenStateValid() {
        boolean penDown = true;
        for (Action action : actions) {
            if (action.actionType() == ActionType.PEN_DOWN) {
                if (penDown) {
                    return false;
                }
                penDown = true;
            }
            else if (action.actionType() == ActionType.PEN_UP) {
                if (!penDown) {
                    return false;
                }
                penDown = false;
            }
        }
        return true;
    }
}
