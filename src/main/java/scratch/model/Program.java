package scratch.model;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.*;

public class Program {

    private final ObservableList<Action> actions = FXCollections.observableArrayList( action -> new Observable[]{ action.expressionProperty(), action.VariableTargetNameProperty() });
    private final Map<String, Integer> declarationList = new HashMap<>();
    private final Deque<ExecutionContext> executionStack = new ArrayDeque<>();
    public ObservableList<Action> getActions() {
        return FXCollections.unmodifiableObservableList(actions);
    }


    public Map<String, Integer> getDeclarationList() {
        return declarationList;
    }

    public void clearDeclarationList(){
        declarationList.clear();
    }

    public void addAction(int index, int destinationIndex){
        ActionType type = ActionsCatalog.getAction(index);
        if (type != null){
            Action action = ActionFactory.createAction(type);
            if(destinationIndex <= actions.size()){
                actions.add(destinationIndex, action);
            }
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
            action.execute(cursor, this);
            context.setCurrentIndex(index + 1);

            if (context.getCurrentIndex() >= list.size()) {
                executionStack.pop();
            }
            return;
        }

        if (action.actionType() == ActionType.REPEAT){
            Expression value = action.getExpression();
            int cpt = value.valueOfVariableOrLitteral(this);
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

    public boolean varIsExist(){
        Set<String>declarsVars = new HashSet<>();
        for (Action action: actions){

            if (action.actionType() == ActionType.VAR_DECLARATION){
                declarsVars.add(action.getExpression().toString());
            }

            else if (action.actionType() == ActionType.VAR_ASSIGNMENT || action.actionType() == ActionType.INCREMENT_VARIABLE){
                String var = action.getVariableTargetName();
                if (!declarsVars.contains(var)){
                    return false;
                }
            }

            else {
                String var = action.getExpression().toString();
                if (!isInteger(var)){
                    if (!declarsVars.contains(var)){
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public boolean isRepeatCounterModificationValid() {
        Deque<String> repeatCounters = new ArrayDeque<>();

        for (int i = 0; i < actions.size(); ++i) {
            Action action = actions.get(i);

            if (action.actionType() == ActionType.REPEAT) {
                String counter = action.getExpression().toString();

                if (!isInteger(counter)) {
                    repeatCounters.push(counter);
                } else {
                    repeatCounters.push("#LITERAL#");
                }
            }
            if (action.actionType() == ActionType.INCREMENT_VARIABLE
                    || action.actionType() == ActionType.VAR_ASSIGNMENT) {
                String targetName = action.getVariableTargetName();

                for (String var : repeatCounters) {
                    if (var.equals(targetName)) {
                        return false;
                    }
                }
            }
            if (action.actionType() == ActionType.END_REPEAT) {
                if (!repeatCounters.isEmpty()) {
                    repeatCounters.pop();
                }
            }
        }

        return true;
    }

    public boolean isEndRepeatExist(){
        Deque<ActionType> endRepeatStack = new ArrayDeque<>();
        for (int i = 0; i < actions.size(); ++i) {
            Action action = actions.get(i);

            if (action.actionType() == ActionType.REPEAT) {
                endRepeatStack.push(action.actionType());
            }

            if (action.actionType() == ActionType.END_REPEAT){
                if (!endRepeatStack.isEmpty()){
                    endRepeatStack.pop();
                }else {
                    return false;
                }
            }

        }return endRepeatStack.isEmpty();

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
        if (index >= 0 && index < actions.size()){
            Action action = getActions().get(index);
            switch(action.actionType()){
                case VAR_ASSIGNMENT:
                    declareVariable(action.getVariableTargetName());
                    break;
                case VAR_DECLARATION:
                    declarationList.remove(action.getExpression().toString());
                    break;
            }
            actions.remove(index);
        }
    }

    public void removeAll(){
        actions.clear();
        declarationList.clear();
    }

    public void up(int index){
        if (index > 0 && index < actions.size()){
            Action action = actions.remove(index);
            actions.add(index - 1, action);
        }
    }

    public void down(int index){
        if (index >= 0 && index < actions.size() - 1){
            Action action = actions.remove(index);
            actions.add(index + 1, action);
        }
    }

    public void duplicate(int destinationIndex, Action action) {
        Expression value = action.getExpression();

        if(destinationIndex <= actions.size()){
            switch (action.actionType()) {

                case PEN_UP -> actions.add(destinationIndex, new Pen(false));

                case PEN_DOWN -> actions.add(destinationIndex, new Pen(true));

                case TURN_LEFT -> actions.add(destinationIndex, new Turn(ActionType.TURN_LEFT, value));

                case TURN_RIGHT -> actions.add(destinationIndex, new Turn(ActionType.TURN_RIGHT, value));

                case VAR_DECLARATION -> actions.add(destinationIndex, new VarDeclaration(value));

                case VAR_ASSIGNMENT -> actions.add(destinationIndex, new VarAssignment(value, action.getVariableTargetName()));

                case INCREMENT_VARIABLE -> actions.add(destinationIndex, new IncrementVariable(value, action.getVariableTargetName()));

                case REPEAT -> actions.add(destinationIndex, new Repeat(value));

                case END_REPEAT -> actions.add(destinationIndex, new EndRepeat(value));

                default -> actions.add(destinationIndex, new MoveForward(value));
            };
        }
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
                if (action.actionType() == ActionType.PEN_UP || action.actionType() == ActionType.PEN_DOWN || action.actionType() == ActionType.END_REPEAT){
                    writer.println(action.actionType().name() + ";");
                } else if (action.actionType() == ActionType.VAR_ASSIGNMENT || action.actionType() == ActionType.INCREMENT_VARIABLE) {
                    writer.println(action.actionType().name() + ";" + action.getVariableTargetName() +";" + action.getExpression());
                } else {
                    writer.println(action.actionType().name() + ";" + action.getExpression());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(File file) {
        declarationList.clear();
        actions.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split(";");

                ActionType type = ActionType.valueOf(parts[0]);

                Action action = ActionFactory.createAction(type);

                switch (type) {
                    case VAR_ASSIGNMENT, INCREMENT_VARIABLE -> {
                        if (parts.length >= 3) {
                            action.setVariableTargetName(parts[1]);
                            action.setExpression(parseExpression(parts[2]));
                        }
                    }
                    case MOVE_FORWARD, TURN_LEFT, TURN_RIGHT, REPEAT, VAR_DECLARATION -> {
                        if (parts.length >= 2 && !parts[1].isEmpty()) {
                            String val = parts[1];
                            if (isInteger(val)) {
                                int value = Integer.parseInt(val);
                                if (type == ActionType.MOVE_FORWARD || type == ActionType.TURN_RIGHT) {
                                    value = -value;
                                }
                                action.setExpression(new LitteralExpression(value));
                            } else {
                                action.setExpression(parseExpression(val));
                            }
                        }
                    }
                }
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

    public Expression parseExpression(String name){
        return isInteger(name) ? new LitteralExpression(Integer.valueOf(name)) : new VariableExpression(name);
    }

    public boolean isInteger(String name){
        boolean isInteger = false;
        if (!(name == null || name.isBlank())) {
            try {
                Integer.valueOf(name.trim());
                isInteger = true;
            } catch (NumberFormatException e) {

            }
        }
        return isInteger;
    }

    public int getVariableValueByName(String name){
        int val = -1;
        for (String key : declarationList.keySet()){
            if (key.equals(name)){
                val = declarationList.get(key);
            }
        }
        return val;
    }

    public void setVariableValueByName(String name, int value) {
        if (declarationList.containsKey(name)){
            declarationList.put(name, value);
        }
    }

    //permet de vérifier que declaration est avant toutes les autres actions
    public boolean declarationIsFirstAction(){
        boolean isBefore = true;
        for(Action action : actions){
            if (action.actionType() == ActionType.VAR_DECLARATION && isBefore){
                int index = actions.indexOf(action);
                for (int i = 0; i < index && isBefore; ++i){
                    ActionType type = actions.get(i).actionType();
                    if (type != ActionType.VAR_DECLARATION){
                        isBefore = false;
                    }
                }
            }
        }
        return isBefore;
    }

    public void declareVariable(String name){
        declarationList.put(name, 0);
    }

    public void incrementDuringExecution(String targetName, int value){
        if (declarationList.containsKey(targetName)){
            int oldValue = declarationList.get(targetName);
            declarationList.put(targetName, oldValue + value);
        }
    }

    public void incrementDuringCompilation(Action currentAction, int count){
        String expression = currentAction.getExpression().toString();
        if (isInteger(expression)){
            int value = Integer.valueOf(currentAction.getExpression().toString());
            currentAction.expressionProperty().set(new LitteralExpression(value + count));
        }
    }

    public void clearAll(){
        actions.clear();
        declarationList.clear();
        executionStack.clear();

    }

    //permet de vérifier que assignation ou déclaration ou incrémente n'est pas vide
    public boolean actionsNotEmpty(){
        boolean isNotEmpty = true;
        for (Action action : actions){
            if (action.actionType() == ActionType.VAR_ASSIGNMENT || action.actionType() == ActionType.INCREMENT_VARIABLE){
                if (action.getVariableTargetName().trim().isEmpty() || action.getVariableTargetName().equals("0")){
                    isNotEmpty = false;
                }
            }
            else if (action.actionType() == ActionType.VAR_DECLARATION){
                if (action.getExpression().toString().isEmpty() || action.getExpression().toString().equals("0")){
                    isNotEmpty = false;
                }
            }
        }
        return isNotEmpty;
    }

    //permet de verifier que le nom de la déclaration est unique
    public boolean declareIsUnique(){
        boolean isUnique = true;

        if (!actions.isEmpty()){
            boolean otherAction = false;

            for (int i = 0 ; i < actions.size() && isUnique && !otherAction; ++i){
                Action action = actions.get(i);
                /*si on tombe sur une autre action que déclaration alors on arrête la recherche
                car declaration doit etre avant toutes les autres actions*/
                otherAction = action.actionType() != ActionType.VAR_DECLARATION;
                if (action.actionType() == ActionType.VAR_DECLARATION){
                    if (declareNotUnique(action, i)){
                        isUnique = false;
                    }
                }
            }

        }
        return isUnique;
    }

    public boolean declareNotUnique(Action action, int index){
        boolean notUnique = false;
        boolean otherAction = false;
        String firstNameDeclare = action.getExpression().toString();

        for (int i = index + 1; i < getActions().size() && !notUnique && !otherAction; ++i){
            Action actionList = getActions().get(i);
            otherAction = actionList.actionType() != ActionType.VAR_DECLARATION;
            if (actionList.actionType() == ActionType.VAR_DECLARATION){
                String secondNameDeclare = actionList.getExpression().toString();
                notUnique = firstNameDeclare.equals(secondNameDeclare);
            }
        }
        return notUnique;
    }
}
