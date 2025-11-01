package ua.kpi.personal.state;

import ua.kpi.personal.model.User;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;

public class ApplicationSession {
    
    private static ApplicationSession instance; // Єдиний екземпляр
    
    private SessionState currentState;
    private User currentUser;
    private Stage primaryStage; 

    // *** 1. ПРИВАТНИЙ КОНСТРУКТОР ***
    private ApplicationSession(Stage primaryStage) {
        this.primaryStage = primaryStage;
        // Початковий стан: Вийшов із системи
        this.currentState = new LoggedOutState(); 
        // Завантажуємо перший екран (login.fxml)
        loadView(); 
    }
    
    // *** 2. ВИДАЛЕНО: public static ApplicationSession getInstance(Stage stage) ***
    // Ця логіка перенесена в initialize().

    // *** 3. МЕТОД ДЛЯ ПЕРШОЇ СТАТИЧНОЇ ІНІЦІАЛІЗАЦІЇ ***
    public static void initialize(Stage stage) {
        if (instance != null) {
            throw new IllegalStateException("ApplicationSession вже ініціалізовано.");
        }
        instance = new ApplicationSession(stage);
    }
    
    // *** 4. МЕТОД ДЛЯ ОТРИМАННЯ ЄДИНОГО ЕКЗЕМПЛЯРА ***
    public static ApplicationSession getInstance() {
        if (instance == null) {
            // Це те, що викликало попередню помилку, коли initialize() не був викликаний.
            throw new IllegalStateException("ApplicationSession не ініціалізовано. Викличте initialize(Stage) у методі start().");
        }
        return instance;
    }

    // --- Методи State ---

    public void changeState(SessionState newState) {
        this.currentState = newState;
        loadView(); 
    }

    public void login(User user) {
        currentState.handleLogin(this, user);
    }
    
    public void logout() {
        currentState.handleLogout(this);
    }
    
    // --- loadView() ---
    
    private void loadView() {
        try {
            String fxmlPath = currentState.getFxmlView();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            
            // Встановлення заголовка
            if (fxmlPath.equals("/fxml/login.fxml")) {
                primaryStage.setTitle("Вхід / Реєстрація");
            } else if (fxmlPath.equals("/fxml/main.fxml")) {
                primaryStage.setTitle("Головне меню");
            }
            
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (IOException e) {
            System.err.println("Помилка завантаження FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Геттери та Сеттери ---
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}