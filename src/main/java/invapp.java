package scenebuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class invapp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("invapp.fxml"));
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.setTitle("Inventory Management");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}