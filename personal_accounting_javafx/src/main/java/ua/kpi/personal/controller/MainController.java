package ua.kpi.personal.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ua.kpi.personal.model.User;
import javafx.scene.control.Button;
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

    private User user;

    public void setUser(User user){
        this.user = user;
        // ЗМІНА: УКРАЇНІЗАЦІЯ привітання
        welcomeLabel.setText("Ласкаво просимо, " + (user.getFullName()!=null?user.getFullName():user.getUsername()));
    }

    // Метод переходу на сторінку Транзакцій
    @FXML
    private void onTransactions() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transactions.fxml"));
        Stage stage = (Stage) transactionsBtn.getScene().getWindow();
        Scene scene = new Scene(loader.load());
        
        TransactionsController ctrl = loader.getController();
        ctrl.setUser(user);
        
        stage.setScene(scene);
    }
    
    // ДОДАНО: Метод переходу на сторінку Рахунків
    @FXML
    private void onAccounts() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/accounts.fxml"));
        Stage stage = (Stage) accountsBtn.getScene().getWindow();
        Scene scene = new Scene(loader.load());
        
        AccountsController ctrl = loader.getController();
        ctrl.setUser(user);
        
        stage.setScene(scene);
    }
    
    // ДОДАНО: Метод переходу на сторінку Категорій
    @FXML
    private void onCategories() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/categories.fxml"));
        Stage stage = (Stage) categoriesBtn.getScene().getWindow();
        Scene scene = new Scene(loader.load());
        
        CategoriesController ctrl = loader.getController();
        ctrl.setUser(user);
        
        stage.setScene(scene);
    }
}