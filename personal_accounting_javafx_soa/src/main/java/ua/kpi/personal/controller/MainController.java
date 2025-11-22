package ua.kpi.personal.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ua.kpi.personal.model.User;
import ua.kpi.personal.repo.TransactionClient;
import ua.kpi.personal.state.ApplicationSession;

import java.io.IOException;

public class MainController {
    @FXML private Label welcomeLabel;
    @FXML private Button transactionsBtn;
    @FXML private Button categoriesBtn;
    @FXML private Button accountsBtn;
    @FXML private Button reportsBtn;
    @FXML private Button logoutBtn;
    
    
    private User currentUser; 

    @FXML
    private void initialize() {
       
        if (currentUser == null) {
            currentUser = ApplicationSession.getInstance().getCurrentUser();
        }
        
        if (currentUser != null) {
            welcomeLabel.setText("Ласкаво просимо, " + (currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername()));
        }
    }
    
    
    public void initData(User user) {
        this.currentUser = user;  
        if (welcomeLabel != null) {
             welcomeLabel.setText("Ласкаво просимо, " + (user.getFullName() != null ? user.getFullName() : user.getUsername()));
        }
    }


    @FXML
    private void onLogout() {
        ApplicationSession.getInstance().logout();
       
        try {
            goToLoginScreen();
        } catch (IOException e) {
            System.err.println("Помилка переходу на екран входу: " + e.getMessage());
            e.printStackTrace();
        }
    }

   
    private void changeScene(String fxmlPath, Button sourceButton, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        
       
        Stage stage = (Stage) sourceButton.getScene().getWindow();
        Scene scene = new Scene(loader.load());
        
        stage.setScene(scene);
        stage.setTitle(title);
    }
    
    @FXML
    private void onTransactions() throws IOException {
        changeScene("/fxml/transactions.fxml", transactionsBtn, "Транзакції");
    }

    @FXML
    private void onAccounts() throws IOException {
        changeScene("/fxml/accounts.fxml", accountsBtn, "Рахунки");
    }

    @FXML
    private void onCategories() throws IOException {
        changeScene("/fxml/categories.fxml", categoriesBtn, "Категорії");
    }


    @FXML
    public void onBack() throws IOException {
        
        changeScene("/fxml/main.fxml", welcomeLabel.getScene().getWindow() instanceof Stage ? (Button) welcomeLabel.getScene().getWindow().getScene().getRoot().lookup("#transactionsBtn") : transactionsBtn, "Персональний Облік - Головна");
        
    }


    @FXML
    private void onReports() throws IOException {
        
        TransactionClient transactionClient = new TransactionClient();
        ApplicationSession session = ApplicationSession.getInstance();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reports.fxml"));

     
        MainController currentController = this;

        loader.setControllerFactory(c -> {
            if (c.equals(ReportsController.class)) {
                
                return new ReportsController(transactionClient, session, currentController);
            } else {
                try {
                    return c.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Не вдалося створити контролер " + c.getName(), e);
                }
            }
        });

        
        Stage stage = (Stage) reportsBtn.getScene().getWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Звіти та Аналітика");
    }
    
    
    private void goToLoginScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Stage stage = (Stage) logoutBtn.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Персональний Облік - Вхід");
    }
}