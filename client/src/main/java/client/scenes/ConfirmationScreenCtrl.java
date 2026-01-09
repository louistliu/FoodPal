package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ConfirmationScreenCtrl {
    @FXML private Label warningLabel;
    private Stage stage;
    private boolean confirmed = false;

    public void setStage(Stage stage) { this.stage = stage; }
    public void setMessage(String msg) { warningLabel.setText(msg); }
    public boolean isConfirmed() { return confirmed; }

    @FXML public void handleYes() { confirmed = true; stage.close(); }
    @FXML public void handleNo() { confirmed = false; stage.close(); }
}
