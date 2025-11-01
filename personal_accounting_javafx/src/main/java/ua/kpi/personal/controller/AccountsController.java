package ua.kpi.personal.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ua.kpi.personal.model.Account;
import ua.kpi.personal.repo.AccountDao;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import ua.kpi.personal.model.User;
import java.io.IOException;

public class AccountsController {
    @FXML private ListView<Account> listView;
    @FXML private TextField nameField;
    @FXML private TextField balanceField;
    @FXML private ChoiceBox<String> currencyChoice; // <-- ДОДАНО: Вибір валюти
    @FXML private Label messageLabel;
    @FXML private Button backBtn;

    private final AccountDao accountDao = new AccountDao();
    private User user;

    @FXML
    private void initialize(){
        // Ініціалізуємо ChoiceBox для валют
        currencyChoice.getItems().addAll("UAH", "USD", "EUR"); 
        refresh(); // Викликається, але не завантажить дані, поки не буде setUser
    }

    // ДОДАНО: Метод для передачі користувача
    public void setUser(User user) {
        this.user = user;
        refresh(); // Оновлюємо список після встановлення користувача
    }

    private void refresh(){
        if (user != null) {
            listView.setItems(FXCollections.observableArrayList(accountDao.findByUserId(user.getId())));
        } else {
            listView.setItems(FXCollections.emptyObservableList());
            System.err.println("User object is null in AccountsController. Cannot refresh.");
        }
    }

    @FXML
    private void onAdd(){
        String name = nameField.getText();
        String currency = currencyChoice.getValue(); // <-- ДОДАНО: Отримання валюти
        
        if(name==null || name.isBlank()){ messageLabel.setText("Name required"); return; }
        if(currency==null){ messageLabel.setText("Currency required"); return; } // Перевірка на валюту

        double bal = 0;
        try{ 
            bal = Double.parseDouble(balanceField.getText()); 
        } catch(Exception e){ 
            messageLabel.setText("Invalid balance format"); 
            return;
        }
        
        Account a = new Account();
        a.setName(name);
        a.setBalance(bal);
        a.setUser(user);    // <-- ВАЖЛИВО: Встановлюємо користувача
        a.setCurrency(currency); // <-- ВАЖЛИВО: Встановлюємо валюту
        a.setType("CASH"); // Приклад типу рахунку
        
        Account created = accountDao.create(a);
        if(created!=null){ 
            messageLabel.setText("Added"); 
            nameField.clear(); 
            balanceField.clear(); 
            refresh(); 
        } else {
            messageLabel.setText("Error saving to database");
        }
    }

    @FXML
    private void onBack() throws IOException { // Використовуйте IOException, якщо це можливо
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        javafx.stage.Stage stage = (javafx.stage.Stage) backBtn.getScene().getWindow();
        Scene scene = new Scene(loader.load());
        
        // ВАЖЛИВО: Передаємо User назад у MainController
        MainController ctrl = loader.getController();
        ctrl.setUser(user);
        
        stage.setScene(scene);
    }
}