package scratch;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import scratch.model.Action;
import scratch.model.Cursor;
import scratch.model.Point;
import scratch.view.MainView;
import scratch.viewmodel.MainViewModel;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        Cursor cursor = new Cursor(new Point(250, 250));
        MainViewModel vm = new MainViewModel(cursor);
        MainView view = new MainView(vm);

        Scene scene = new Scene(view, 1300, 700);
        primaryStage.setTitle("Scratch");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}