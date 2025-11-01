package ua.kpi.personal.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import ua.kpi.personal.service.AuthService;
import ua.kpi.personal.model.User;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void initialize(){
        // ПЕРЕКЛАД: "Будь ласка, увійдіть або зареєструйтеся"
        messageLabel.setText("Будь ласка, увійдіть або зареєструйтеся");
    }

    @FXML
    private void onLogin() {
        String u = usernameField.getText();
        String p = passwordField.getText();
        User user = authService.login(u,p);
        if(user != null){
            // open main window
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene scene = new Scene(loader.load());
                MainController ctrl = loader.getController();
                ctrl.setUser(user);
                
                // ВИПРАВЛЕННЯ КОДУВАННЯ: "Особиста бухгалтерія — " + ім'я користувача
                stage.setTitle("Особиста бухгалтерія — " + user.getUsername());
                
                stage.setScene(scene);
            } catch(IOException ex){ 
                ex.printStackTrace(); 
                // ПЕРЕКЛАД: "Помилка відкриття головного вікна"
                messageLabel.setText("Помилка відкриття головного вікна"); 
            }
        } else {
            // ПЕРЕКЛАД: "Невірні облікові дані або користувача не знайдено"
            messageLabel.setText("Невірні облікові дані або користувача не знайдено");
        }
    }

    @FXML
    private void onRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Stage stage = (Stage) registerButton.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            // ПЕРЕКЛАД: "Реєстрація"
            stage.setTitle("Реєстрація"); 
            stage.setScene(scene);
        } catch(IOException ex){ 
            ex.printStackTrace(); 
            // ПЕРЕКЛАД: "Помилка відкриття вікна реєстрації"
            messageLabel.setText("Помилка відкриття вікна реєстрації"); 
        }
    }
}