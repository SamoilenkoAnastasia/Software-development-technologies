package ua.kpi.personal.state;

import ua.kpi.personal.model.User;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;

public class ApplicationSession {
    
    private static ApplicationSession instance; 
    
    private SessionState currentState;
    private User currentUser;
    private Stage primaryStage; 

    
    private ApplicationSession(Stage primaryStage) {
        this.primaryStage = primaryStage;   
        this.currentState = new LoggedOutState(); 
        loadView(); 
    }
    
   
    public static void initialize(Stage stage) {
        if (instance != null) {
            throw new IllegalStateException("ApplicationSession вже ініціалізовано.");
        }
        instance = new ApplicationSession(stage);
    }
    
    
    public static ApplicationSession getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ApplicationSession не ініціалізовано. Викличте initialize(Stage) у методі start().");
        }
        return instance;
    }

    

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
    
  
    private void loadView() {
        try {
            String fxmlPath = currentState.getFxmlView();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            
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

   
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}