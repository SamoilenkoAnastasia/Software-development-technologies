package ua.kpi.personal.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ua.kpi.personal.model.Account;
import ua.kpi.personal.repo.AccountDao;
import ua.kpi.personal.model.User;
import ua.kpi.personal.state.ApplicationSession; 
import java.io.IOException;


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
        this.user = ApplicationSession.getInstance().getCurrentUser();
        currencyChoice.getItems().addAll("UAH", "USD", "EUR"); 
        
        refresh(); 
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
        ApplicationSession.getInstance().login(user);
    }
}