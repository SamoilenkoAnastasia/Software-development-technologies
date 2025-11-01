package ua.kpi.personal.controller;

import javafx.fxml.FXML; // Залишаємо один раз
import javafx.scene.control.Label;
import ua.kpi.personal.model.User;
import javafx.scene.control.Button;
import ua.kpi.personal.state.ApplicationSession; 
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {
    @FXML private Label welcomeLabel;
    @FXML private Button transactionsBtn;
    @FXML private Button categoriesBtn;
    @FXML private Button accountsBtn;
    @FXML private Button logoutBtn; 

    @FXML
    private void initialize() {
        User user = ApplicationSession.getInstance().getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Ласкаво просимо, " + (user.getFullName() != null ? user.getFullName() : user.getUsername()));
        }
    }

    @FXML
    private void onLogout() {
        ApplicationSession.getInstance().logout();
    }
    

 @FXML
    private void onTransactions() throws IOException {
        User user = ApplicationSession.getInstance().getCurrentUser(); // Користувач все ще потрібен тут
                                                                       // але ми його не передаємо.

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transactions.fxml"));
        Stage stage = (Stage) transactionsBtn.getScene().getWindow();
        Scene scene = new Scene(loader.load());
        

        stage.setScene(scene);
    }
    

    @FXML
    private void onAccounts() throws IOException {
        User user = ApplicationSession.getInstance().getCurrentUser(); 
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/accounts.fxml"));
        Stage stage = (Stage) accountsBtn.getScene().getWindow();
        Scene scene = new Scene(loader.load());

        
        stage.setScene(scene);
    }
    
    
    @FXML
    private void onCategories() throws IOException {
        User user = ApplicationSession.getInstance().getCurrentUser();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/categories.fxml"));
        Stage stage = (Stage) categoriesBtn.getScene().getWindow();
        Scene scene = new Scene(loader.load());

        
        stage.setScene(scene);
    }
}