package scratch;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import scratch.view.MainView;
import scratch.viewmodel.MainViewModel;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {

        MainViewModel vm = new MainViewModel();
        MainView view = new MainView(vm);

        Scene scene = new Scene(view, 1000, 650);
        primaryStage.setTitle("Scratch");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}