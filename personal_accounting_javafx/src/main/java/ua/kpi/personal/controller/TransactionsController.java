package ua.kpi.personal.controller;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ua.kpi.personal.model.*;
import ua.kpi.personal.repo.*;
import java.time.LocalDateTime;
import ua.kpi.personal.state.ApplicationSession; 

public class TransactionsController {

    @FXML private TableView<Transaction> table;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, Double> colAmount;
    @FXML private TableColumn<Transaction, String> colCategory;
    @FXML private TableColumn<Transaction, String> colAccount;
    @FXML private TableColumn<Transaction, LocalDateTime> colDate;
    @FXML private TableColumn<Transaction, String> colDesc;

    @FXML private ChoiceBox<String> typeChoice;
    @FXML private TextField amountField;
    @FXML private ChoiceBox<Category> categoryChoice;
    @FXML private ChoiceBox<Account> accountChoice;
    @FXML private TextField descField;
    @FXML private Label messageLabel;
    @FXML private Button backBtn;
    
    private final TransactionDao transactionDao = new TransactionDao();
    private final CategoryDao categoryDao = new CategoryDao();
    private final AccountDao accountDao = new AccountDao();
    private User user;

    @FXML
    private void initialize(){

        this.user = ApplicationSession.getInstance().getCurrentUser();
        

        typeChoice.getItems().addAll("EXPENSE", "INCOME");
        typeChoice.setValue("EXPENSE"); 

        colType.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getType()));
        colAmount.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAmount()));
        colCategory.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getCategory() != null ? data.getValue().getCategory().getName() : ""
        ));
        colAccount.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getAccount() != null ? data.getValue().getAccount().getName() : ""
        ));
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getCreatedAt()));
        colDesc.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));

        refresh(); 
    }

  

    private void refresh(){
      
        if (user == null) return;
        
        // Оновлення таблиці
        ObservableList<Transaction> items = FXCollections.observableArrayList(
            transactionDao.findByUserId(user.getId())
        );
        table.setItems(items);
        
        
        categoryChoice.setItems(
            FXCollections.observableArrayList(categoryDao.findByUserId(user.getId()))
        );
        accountChoice.setItems(
            FXCollections.observableArrayList(accountDao.findByUserId(user.getId()))
        );
    }

    @FXML
    private void onAdd(){
        
        String amountText = amountField.getText();
        String type = typeChoice.getValue();
        Category cat = categoryChoice.getValue();
        Account acc = accountChoice.getValue();
        
        
        if (type == null) { 
            messageLabel.setText("Виберіть тип транзакції."); 
            return; 
        }
        if (acc == null) { 
            messageLabel.setText("Виберіть Рахунок (Account)."); 
            return; 
        }
        if (cat == null) { 
            messageLabel.setText("Виберіть Категорію."); 
            return; 
        }
        

        try {
            double amount = Double.parseDouble(amountText);
            
            if (amount <= 0) {
                messageLabel.setText("Сума має бути додатною.");
                return;
            }
            
            Transaction tx = new Transaction();
            tx.setAmount(amount);
            tx.setType(type);
            tx.setCategory(cat);
            tx.setAccount(acc);
            tx.setDescription(descField.getText());
            tx.setCreatedAt(LocalDateTime.now());
            tx.setUser(user);
            
        
            Transaction created = transactionDao.create(tx);
            
            if(created != null){
                messageLabel.setText("? Транзакцію успішно додано.");
                amountField.clear(); 
                descField.clear();
                refresh();
            } else {
                messageLabel.setText("Помилка при додаванні транзакції. Перевірте консоль.");
            }
        } catch(NumberFormatException ex){ 
            messageLabel.setText("Некоректна сума (потрібне число, наприклад: 100.50)"); 
        }
    }

    @FXML
    private void onBack() throws IOException {

        ApplicationSession.getInstance().login(user);
    }
}