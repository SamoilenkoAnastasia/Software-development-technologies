package ua.kpi.personal.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ua.kpi.personal.model.Account;
import ua.kpi.personal.repo.AccountDao;
import ua.kpi.personal.model.User;
import ua.kpi.personal.state.ApplicationSession; // <--- НОВИЙ ІМПОРТ ДЛЯ PATTERN
import java.io.IOException;

// Видаляємо непотрібні імпорти FXMLLoader, Scene
// import javafx.fxml.FXMLLoader;
// import javafx.scene.Scene; 

public class AccountsController {
    @FXML private ListView<Account> listView;
    @FXML private TextField nameField;
    @FXML private TextField balanceField;
    @FXML private ChoiceBox<String> currencyChoice; 
    @FXML private Label messageLabel;
    @FXML private Button backBtn;

    private final AccountDao accountDao = new AccountDao();
    private User user;

    @FXML
    private void initialize(){
        // *** ЗМІНА 1: Отримуємо користувача із сесії при ініціалізації ***
        this.user = ApplicationSession.getInstance().getCurrentUser();
        
        // Збережена логіка: Ініціалізуємо ChoiceBox для валют
        currencyChoice.getItems().addAll("UAH", "USD", "EUR"); 
        
        refresh(); // Тепер refresh спрацює, оскільки user встановлений
    }

    // *** ЗМІНА 2: ВИДАЛЯЄМО setUser() ***
    /*
    public void setUser(User user) {
        this.user = user;
        refresh();
    }
    */

    private void refresh(){
        // Збережена логіка: оновлення
        if (user != null) {
            listView.setItems(FXCollections.observableArrayList(accountDao.findByUserId(user.getId())));
        } else {
            listView.setItems(FXCollections.emptyObservableList());
            System.err.println("User object is null in AccountsController. Cannot refresh.");
        }
    }

    @FXML
    private void onAdd(){
        // Збережена логіка: додавання рахунку
        String name = nameField.getText();
        String currency = currencyChoice.getValue(); 
        
        if(name==null || name.isBlank()){ messageLabel.setText("Name required"); return; }
        if(currency==null){ messageLabel.setText("Currency required"); return; } 

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
        a.setUser(user);    
        a.setCurrency(currency); 
        a.setType("CASH"); 
        
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
    private void onBack() throws IOException { 
        // *** ЗМІНА 3: ВИКОРИСТОВУЄМО ПАТЕРН STATE ДЛЯ ПЕРЕХОДУ НАЗАД ***
        // Видаляємо ручне завантаження FXML та виклик ctrl.setUser()
        ApplicationSession.getInstance().login(user);
    }
}