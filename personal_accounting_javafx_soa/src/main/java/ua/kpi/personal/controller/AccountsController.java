package ua.kpi.personal.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ua.kpi.personal.model.Account;
import ua.kpi.personal.repo.AccountClient; 
import ua.kpi.personal.model.User;
import ua.kpi.personal.state.ApplicationSession; 
import java.io.IOException;
import java.util.List;

public class AccountsController {
    @FXML private ListView<Account> listView;
    @FXML private TextField nameField;
    @FXML private TextField balanceField;
    @FXML private ChoiceBox<String> currencyChoice; 
    @FXML private Label messageLabel;
    @FXML private Button backBtn;

    private final AccountClient accountClient = new AccountClient(); 
    private User user;

    @FXML
    private void initialize(){
        this.user = ApplicationSession.getInstance().getCurrentUser();
        currencyChoice.getItems().addAll("UAH", "USD", "EUR"); 
        
        refresh(); 
    }

    
    private void refresh(){
        if (user != null) {
          
            List<Account> accounts = accountClient.findByUserId(user.getId()); 
            listView.setItems(FXCollections.observableArrayList(accounts));
        } else {
            listView.setItems(FXCollections.emptyObservableList());
            System.err.println("User object is null in AccountsController. Cannot refresh.");
        }
    }

        @FXML
    private void onAdd(){
         String name = nameField.getText();
         String currency = currencyChoice.getValue();
         String balanceText = balanceField.getText(); 

      
         if(name==null || name.isBlank()){ messageLabel.setText("Name required"); return; }
         if(currency==null){ messageLabel.setText("Currency required"); return; } 

         double bal = 0.0; 

         
         try{ 
             
             if (!balanceText.isBlank()) {
                 
                 String normalizedBalance = balanceText.trim().replace(',', '.'); 

               
                 if (!normalizedBalance.isBlank()) {
                      bal = Double.parseDouble(normalizedBalance);
                 }
             }

            
             if (bal < 0) {
                  messageLabel.setText("Balance must be positive or zero.");
                  return;
             }

         } catch(NumberFormatException e){ 
             
             messageLabel.setText("Невірний формат балансу. Використовуйте числа (наприклад, 10000.00)."); 
             return;
         }

        
         Account a = new Account();
         a.setName(name);
         a.setBalance(bal); 
         a.setUser(user);    
         a.setCurrency(currency); 
         a.setType("CASH"); 

       
         Account created = accountClient.create(a);
         if(created!=null){ 
             messageLabel.setText("Added"); 
             nameField.clear(); 
             balanceField.clear(); 
             refresh(); 
         } else {
             
             messageLabel.setText("Error saving to API. Check server logs."); 
         }
     }

    @FXML
    private void onBack() throws IOException {   
        ApplicationSession.getInstance().login(user);
    }
}