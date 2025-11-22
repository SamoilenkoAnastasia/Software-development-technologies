package ua.kpi.personal.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ua.kpi.personal.model.User;
import ua.kpi.personal.service.AuthService;
import ua.kpi.personal.state.ApplicationSession;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label messageLabel;

    private final AuthService authService = new AuthService();

    
    private Stage stage;

    
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        messageLabel.setText("Будь ласка, увійдіть або зареєструйтеся");
    }

    @FXML
    private void onLogin() {
        String u = usernameField.getText();
        String p = passwordField.getText();

        boolean loginSuccessful = authService.login(u, p);

        if (loginSuccessful) {
            User user = authService.getCurrentUser();

            
            ApplicationSession.getInstance().login(user);

            try {
                goToMainScreen(user);
            } catch (Exception e) {
                messageLabel.setText("Критична помилка переходу: " + e.getLocalizedMessage());
                e.printStackTrace();
            }

        } else {
            messageLabel.setText("Невірні облікові дані або користувача не знайдено");
        }
    }

    @FXML
    private void onRegister() {
        if (stage == null) {
            messageLabel.setText("Помилка: Stage не ініціалізовано");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Parent registerPane = loader.load();

            Scene scene = new Scene(registerPane);
            stage.setScene(scene);
            stage.setTitle("Реєстрація");

        } catch (IOException ex) {
            ex.printStackTrace();
            messageLabel.setText("Помилка відкриття вікна реєстрації");
        }
    }

    
    private void goToMainScreen(User user) throws IOException {
        if (stage == null) {
            messageLabel.setText("Помилка: Stage не ініціалізовано");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent mainPane = loader.load();

        MainController mainController = loader.getController();
        if (mainController != null) {
            mainController.initData(user);
        }

        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Персональний Облік - Головна");
    }
}
