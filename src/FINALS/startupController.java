package FINALS;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class startupController{
    @FXML private TextField nameField;

    private EdiTORRController ec;

    private Alert info = new Alert(Alert.AlertType.INFORMATION);
    private Alert warning = new Alert(Alert.AlertType.WARNING);

    public void getName(String name){ nameField.setText(name); }

    public void nextScene(ActionEvent e) throws IOException {
        if(nameField.getLength() >= 1){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("File EdiTORR.fxml"));
            Parent next = (Parent) loader.load();

            Scene scene = new Scene(next);

            EdiTORRController ec = loader.getController();
            ec.getName(nameField.getText());

            info.setContentText("Welcome to the program!");
            info.setTitle("WELCOME");
            info.setHeaderText(null);
            info.showAndWait();

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        else{
            warning.setTitle("ERROR");
            warning.setHeaderText(null);
            warning.setContentText("ERROR 404: Name not found.");
            warning.showAndWait();
        }
    }

}
