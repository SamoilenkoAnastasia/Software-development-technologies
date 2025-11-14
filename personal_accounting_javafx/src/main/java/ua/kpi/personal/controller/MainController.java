package ua.kpi.personal.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ua.kpi.personal.model.User;
import javafx.scene.control.Button;
import ua.kpi.personal.state.ApplicationSession;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import ua.kpi.personal.repo.TransactionDao;

public class MainController {
    @FXML private Label welcomeLabel;
    @FXML private Button transactionsBtn;
    @FXML private Button categoriesBtn;
    @FXML private Button accountsBtn;
    @FXML private Button reportsBtn;
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transactions.fxml"));
        Stage stage = (Stage) transactionsBtn.getScene().getWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }


    @FXML
    private void onAccounts() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/accounts.fxml"));
        Stage stage = (Stage) accountsBtn.getScene().getWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }


    @FXML
    private void onCategories() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/categories.fxml"));
        Stage stage = (Stage) categoriesBtn.getScene().getWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }


    @FXML
    public void onBack() throws IOException {
  
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();

        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }
    
    @FXML
    private void onReports() throws IOException {

        TransactionDao transactionDao = new TransactionDao();
        ApplicationSession session = ApplicationSession.getInstance();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reports.fxml"));

        // !!! ВИПРАВЛЕННЯ: Передаємо посилання на MainController до ReportsController !!!
        MainController currentController = this;

        loader.setControllerFactory(c -> {
            if (c.equals(ReportsController.class)) {
                // Конструктор ReportsController повинен тепер приймати MainController
                return new ReportsController(transactionDao, session, currentController); 
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
    }
}