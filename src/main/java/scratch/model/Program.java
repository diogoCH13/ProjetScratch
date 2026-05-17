package scratch.model;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.*;

public class Program {

    private final ObservableList<Action> actions = FXCollections.observableArrayList( action -> action.observables());
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
            Expression value = parseExpression(action.parameterValue(0));

            int cpt = value.valueOfExpression(this);
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
                declarsVars.add(action.parameterValue(0));
            }

            else if (action.actionType() == ActionType.VAR_ASSIGNMENT || action.actionType() == ActionType.INCREMENT_VARIABLE){
                String var = action.parameterValue(0);
                if (!declarsVars.contains(var)){
                    return false;
                }
            }

            else if (action.parameterCount() > 0) {
                String var = action.parameterValue(0);
                if (!isInteger(var) && !declarsVars.contains(var)) {
                    return false;
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
                String counter = action.parameterValue(0);

                if (!isInteger(counter)) {
                    repeatCounters.push(counter);
                } else {
                    repeatCounters.push("#LITERAL#");
                }
            }
            if (action.actionType() == ActionType.INCREMENT_VARIABLE
                    || action.actionType() == ActionType.VAR_ASSIGNMENT) {
                String targetName = action.parameterValue(0);

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
            actions.remove(index);
//            Action action = getActions().get(index);
//            for (int i = 0; i < action.parameterCount(); ++i){
//
//            }
//            switch(action.actionType()){
//                case VAR_ASSIGNMENT:
//                    declareVariable(action.variableTargetName());
//                    break;
//                case VAR_DECLARATION:
//                    declarationList.remove(action.getExpression);
//                    break;
//            }

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
        actions.add(destinationIndex, action.copy());
    }

    public void save(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (Action action : actions) {
                writer.print(action.actionType().name());

                for (int i = 0; i < action.parameterCount(); ++i) {
                    writer.print(";" + action.parameterValue(i));
                }

                writer.println();
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

                for (int i = 0; i < action.parameterCount() && i + 1 < parts.length; i++) {
                    action.setParameterValue(i, parts[i + 1], this);
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

    public void incrementDuringCompilation(Action currentAction, int parameterIndex, int count) {
        if (parameterIndex < 0 || parameterIndex >= currentAction.parameterCount()) {
            return;
        }

        String value = currentAction.parameterValue(parameterIndex);

        if (isInteger(value)) {
            int number = Integer.parseInt(value);
            currentAction.setParameterValue(parameterIndex, String.valueOf(number + count), this);
        }
    }

    public void clearAll(){
        actions.clear();
        declarationList.clear();
        executionStack.clear();

    }

    public boolean actionsNotEmpty() {
        boolean isNotEmpty = true;

        for (Action action : actions) {
            for (int i = 0; i < action.parameterCount() && isNotEmpty; ++i) {
                String value = action.parameterValue(i);

                if (value.trim().isEmpty() || value.equals("0")) {
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
        String firstNameDeclare = action.parameterValue(0);

        for (int i = index + 1; i < getActions().size() && !notUnique && !otherAction; ++i){
            Action actionList = getActions().get(i);
            otherAction = actionList.actionType() != ActionType.VAR_DECLARATION;
            if (actionList.actionType() == ActionType.VAR_DECLARATION){
                String secondNameDeclare = actionList.parameterValue(0);
                notUnique = firstNameDeclare.equals(secondNameDeclare);
            }
        }
        return notUnique;
    }
}
