package FINALS;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.fxml.FXMLLoader.load;

public class MainProject extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = load(getClass().getResource("startup.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("BIBBO-NAZI SIMULATOR by Zero One Intelligence");
        primaryStage.show();
    }


    public static void main(String[] args) { launch(args); }
}
