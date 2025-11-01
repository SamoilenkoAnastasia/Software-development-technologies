package ua.kpi.personal.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import ua.kpi.personal.service.AuthService;
import ua.kpi.personal.model.User;

import java.io.IOException;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullnameField;
    @FXML private Label messageLabel;
    @FXML private Button createButton; 
    
   
    private final AuthService authService = new AuthService();

    @FXML
    private void onCreate() {
        String u = usernameField.getText();
        String p = passwordField.getText();
        String fn = fullnameField.getText();
        
        if(u.isBlank() || p.isBlank()){ 
            messageLabel.setText("Необхідно вказати ім'я користувача та пароль"); 
            return; 
        }
        
        User user = authService.register(u,p,fn);
        
        if(user != null){
            messageLabel.setText("Успішно зареєстровано. Будь ласка, увійдіть.");
            
            
            goToLoginScreen();
            
        } else {
            messageLabel.setText("Користувач вже існує або сталася помилка");
        }
    }
    
    
    @FXML
    private void onBackToLogin() {
        goToLoginScreen();
    }
    
    
    private void goToLoginScreen() {
         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            
            
            Stage stage = (Stage) createButton.getScene().getWindow(); 
            
            Scene scene = new Scene(loader.load());
            
            stage.setTitle("Вхід"); 
            stage.setScene(scene);
            
        } catch(IOException ex){ 
             System.err.println("Помилка при завантаженні екрану входу: " + ex.getMessage());
             ex.printStackTrace(); 
        }
    }
}