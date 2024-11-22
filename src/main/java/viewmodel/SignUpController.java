package viewmodel;

import dao.DbConnectivityClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.UserSession;

import java.io.IOException;

import static viewmodel.MainApplication.ChangeScreen;

public class SignUpController {
    @FXML
    public Button goBackBtn;
    @FXML
    public Text status;
    @FXML
    private TextField regUsername;
    @FXML
    private TextField regPassword;
    @FXML
    private TextField regEmail;
    @FXML
    private Button registerbttn;

   // private final DbConnectivityClass cnUtil = new DbConnectivityClass();

    public void initialize() {
        // Disable the login button, and attach our validation methods
        registerbttn.setDisable(true);

        // Add listeners to trigger validation
        regUsername.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());
        regPassword.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());
        regEmail.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());
    }

    private void validateInputs() {
        boolean isUsernameValid = isUsernameValid();
        boolean isPasswordValid = isPasswordValid();
        boolean isEmailValid = isEmailValid();

        // Enable login button only if all fields are valid
        registerbttn.setDisable(!(isUsernameValid && isPasswordValid && isEmailValid));
    }

    private boolean isEmailValid() {
        String email = regEmail.getText();
        return email != null && !email.trim().isEmpty() && email.contains("@") && email.contains(".");
    }

    private boolean isUsernameValid() {
        String username = regUsername.getText();
        return username != null && !username.trim().isEmpty() && username.length() >= 5;
    }

    /**
     * Validates the password field
     * @return true if the password is not empty and has at least 5 characters
     */
    private boolean isPasswordValid() {
        String password = regPassword.getText();

        return password != null && !password.trim().isEmpty() && password.length() >= 5;
    }


    //TODO Registration CSS page
    @FXML
    private void Register(ActionEvent event) throws IOException {
        if(isUsernameValid() && isPasswordValid() && isEmailValid()) {
            MainApplication.cnUtil.insertRegisteredUser(regUsername.getText(),regPassword.getText(),regEmail.getText());
            //ChangeScreen("login.fxml", 600, 400, registerbttn);
            status.setText("Success");
            status.setFill(Color.GREEN);
        }

        MainApplication.cnUtil.printRegisteredUsersTable();
    }

    public void goBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
