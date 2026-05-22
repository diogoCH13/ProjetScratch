package scratch.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.*;

public class Program {

    private final ObservableList<Action> actionsList = FXCollections.observableArrayList(action -> action.observables());
    private final Map<String, Integer> declarationList = new HashMap<>();
    private final Deque<ExecutionContext> executionStack = new ArrayDeque<>();

    public ObservableList<Action> getActionsList() {
        return FXCollections.unmodifiableObservableList(actionsList);
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
            if(destinationIndex <= actionsList.size()){
                actionsList.add(destinationIndex, action);
            }
        }
    }

    public void startExecution(){
        executionStack.clear();
        ExecutionContext context = new ExecutionContext(actionsList, 0);
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
        for (int i = index + 1; i < actionsList.size(); ++i){
            Action action = actionsList.get(i);
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

    public boolean varIsExist() {
        Set<String> declarsVars = new HashSet<>();

        for (Action action : actionsList) {
            for (int i = 0; i < action.parameterCount(); i++) {
                String value = action.parameterValue(i);

                if (action.parameterDeclareVariable(i)) {
                    declarsVars.add(value);
                }
                else if (action.parameterUseVariable(i)) {
                    if (!isInteger(value) && !declarsVars.contains(value)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public boolean isRepeatCounterModificationValid() {
        Deque<String> repeatCounters = new ArrayDeque<>();

        for (int i = 0; i < actionsList.size(); ++i) {
            Action action = actionsList.get(i);

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
        for (int i = 0; i < actionsList.size(); ++i) {
            Action action = actionsList.get(i);

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
        if (index >= 0 && index < actionsList.size()){
            actionsList.remove(index);
//            Action action = getActionsList().get(index);
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
        actionsList.clear();
        declarationList.clear();
    }

    public void up(int index){
        if (index > 0 && index < actionsList.size()){
            Action action = actionsList.remove(index);
            actionsList.add(index - 1, action);
        }
    }

    public void down(int index){
        if (index >= 0 && index < actionsList.size() - 1){
            Action action = actionsList.remove(index);
            actionsList.add(index + 1, action);
        }
    }

    public void duplicate(int destinationIndex, Action action) {
        actionsList.add(destinationIndex, action.copy());
    }

    public void save(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (Action action : actionsList) {
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
        actionsList.clear();

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

                actionsList.add(action);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean isPenStateValid() {
        boolean penDown = true;
        for (Action action : actionsList) {
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

    //permet de vérifier que declaration est avant toutes les autres actionsList
    public boolean declarationIsFirstAction(){
        boolean isBefore = true;
        for(Action action : actionsList){
            if (action.actionType() == ActionType.VAR_DECLARATION && isBefore){
                int index = actionsList.indexOf(action);
                for (int i = 0; i < index && isBefore; ++i){
                    ActionType type = actionsList.get(i).actionType();
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
        actionsList.clear();
        declarationList.clear();
        executionStack.clear();

    }

    public boolean actionsNotEmpty() {
        boolean isNotEmpty = true;

        for (Action action : actionsList) {
            for (int i = 0; i < action.parameterCount() && isNotEmpty; ++i) {
                String value = action.parameterValue(i);

                if (value.trim().isEmpty() || value.equals("0")) {
                    isNotEmpty = false;
                }
            }
        }

        return isNotEmpty;
    }

    public boolean declareIsUnique() {
        boolean isUnique = true;
        Set<String> declaredNames = new HashSet<>();

        for (Action action : actionsList) {
            for (int i = 0; i < action.parameterCount(); i++) {
                if (action.parameterDeclareVariable(i)) {
                    String name = action.parameterValue(i);

                    if (declaredNames.contains(name)) {
                        isUnique = false;
                    }

                    declaredNames.add(name);
                }
            }
        }
        return isUnique;
    }
}
