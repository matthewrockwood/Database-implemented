package viewmodel;

import dao.DbConnectivityClass;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import service.UserSession;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class LoginController {

    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    @FXML
    public TextField usernameTextField;
    @FXML
    public PasswordField passwordTextField;
    @FXML
    public Text logintext;
    @FXML
    private GridPane rootpane;
    public void initialize() {
        rootpane.setBackground(new Background(
                        createImage("https://edencoding.com/wp-content/uploads/2021/03/layer_06_1920x1080.png"),
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );


        rootpane.setOpacity(0);
        FadeTransition fadeOut2 = new FadeTransition(Duration.seconds(10), rootpane);
        fadeOut2.setFromValue(0);
        fadeOut2.setToValue(1);
        fadeOut2.play();
    }
    private static BackgroundImage createImage(String url) {
        return new BackgroundImage(
                new Image(url),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0, true, Side.BOTTOM, 0, true),
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true));
    }

    @FXML
    public void login(ActionEvent actionEvent) {
        String usernameInput = usernameTextField.getText();
        String passwordInput = passwordTextField.getText();

        if (validateCredentials(usernameInput, passwordInput)) {
            UserSession userSession = UserSession.getInstance(usernameInput, passwordInput);
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/db_interface_gui.fxml"));
                Scene scene = new Scene(root, 900, 600);
                scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
                Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logintext.setText("Invalid");

            System.out.println("Invalid username or password. Please try again.");
        }
    }


    private boolean validateCredentials(String username, String password) {
        String filePath = "preference.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentUsername = null;
            String currentPassword = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Username:")) {
                    currentUsername = line.substring(9).trim();
                } else if (line.startsWith("Password:")) {
                    currentPassword = line.substring(9).trim();
                } else if (line.equals("--------------------------")) {

                    if (username.equals(currentUsername) && password.equals(currentPassword)) {
                        return true;
                    }

                    currentUsername = null;
                    currentPassword = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void signUp(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/signUp.fxml"));
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
