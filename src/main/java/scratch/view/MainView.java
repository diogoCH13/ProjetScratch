package scratch.view;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import scratch.model.*;
import scratch.viewmodel.MainViewModel;

import java.io.File;


public class MainView extends BorderPane /*VBox*/ {
    private final MainViewModel vm;

    private final MenuItem newItem  = new MenuItem("New...");
    private final MenuItem openItem = new MenuItem("Open...");
    private final MenuItem saveItem = new MenuItem("Save As...");
    private final MenuItem exitItem = new MenuItem("Exit");

    private final VBox menuBox = new VBox(10);

    public MainView(MainViewModel vm) {
        this.vm = vm;

        configureMenuBox();

        this.setTop(menuBox);
        this.setLeft(new PalettActionsView(vm));
        this.setCenter(new ActionsListView(vm));
        this.setRight(new SceneTurtleView(vm));

        configActions();
    }

    private void configureMenuBox(){
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(newItem, openItem, saveItem, exitItem);

        MenuBar menuBar = new MenuBar(fileMenu);
        menuBox.getChildren().add(menuBar);
    }

    private void configActions() {
        newItem.setOnAction(e->{
            vm.newFile();
        });

        exitItem.setOnAction(e->{
            Platform.exit();
        });

        openItem.setOnAction(e->{
            openFile();
        });

        saveItem.setOnAction(e->{
            saveFile();
        });
    }

    private void openFile(){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir");
        fileChooser.setInitialDirectory(new File("scratch"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Scratch files (*.scr)", "*.scr")
        );

        File file = fileChooser.showOpenDialog(this.getScene().getWindow());

        if (file != null){
            vm.openFile(file);
        }
    }


    private void saveFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer sous");
        fileChooser.setInitialDirectory(new File("scratch"));

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Scratch files (*.scr)", "*.scr")
        );

        File file = fileChooser.showSaveDialog(this.getScene().getWindow());

        if (file != null) {
            vm.saveFile(file);
        }
    }

}
