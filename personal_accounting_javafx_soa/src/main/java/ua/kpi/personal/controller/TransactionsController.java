package ua.kpi.personal.controller;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ua.kpi.personal.model.*;
import ua.kpi.personal.repo.*; 
import ua.kpi.personal.processor.*;
import java.time.LocalDateTime;
import java.util.Optional;
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
    
    
    @FXML private ChoiceBox<String> currencyChoice; 
    
    @FXML private ChoiceBox<Category> categoryChoice;
    @FXML private ChoiceBox<Account> accountChoice;
    @FXML private TextField descField;
    @FXML private Label messageLabel; 
    @FXML private Button backBtn;


    
    private TransactionProcessor transactionProcessor; 
    
    
    private final TransactionClient transactionClient = new TransactionClient(); 
    private final CategoryClient categoryClient = new CategoryClient();
    private final AccountClient accountClient = new AccountClient();
    private final TemplateClient templateClient = new TemplateClient(); 
    private User user;

    @FXML
    private void initialize(){
        this.user = ApplicationSession.getInstance().getCurrentUser();
        
        
        setupProcessor(); 
        
        
        typeChoice.getItems().addAll("EXPENSE", "INCOME");
        typeChoice.setValue("EXPENSE"); 
        
        
        currencyChoice.getItems().addAll("UAH", "USD", "EUR");
        currencyChoice.setValue("UAH"); 
        
        
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
    
    
   
    private void setupProcessor() {
        
        TransactionProcessor baseProcessor = transactionClient; 
        TransactionProcessor currencyProcessor = new CurrencyDecorator(baseProcessor); 
        TransactionProcessor balanceProcessor = new BalanceCheckDecorator(currencyProcessor); 
        this.transactionProcessor = new UiNotificationDecorator(balanceProcessor, this); 
    }
    

    
    public void displaySuccessDialog(String message) {
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("Операція Успішна");
          alert.setHeaderText(null); 
          alert.setContentText(message);
          alert.showAndWait();
      }


      public void displayErrorDialog(String message) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Помилка Обробки Транзакції");
          alert.setHeaderText("Операцію не вдалося виконати.");
          alert.setContentText(message);
          alert.showAndWait();
      }
    public User getUser() {
        return user;
    }

    void refresh(){
        if (user == null) return;
        
        
        table.setItems(
            FXCollections.observableArrayList(transactionClient.findByUserId(user.getId()))
        );
        
        categoryChoice.setItems(
            FXCollections.observableArrayList(categoryClient.findByUserId(user.getId())) 
        );
        accountChoice.setItems(
            FXCollections.observableArrayList(accountClient.findByUserId(user.getId()))
        );
    }

    
    public void fillFormWithTemplate(TransactionTemplate template) {
        Transaction clonedTx = template.createTransactionFromTemplate(); 
        
        
        typeChoice.setValue(clonedTx.getType());
        amountField.setText(clonedTx.getAmount() != 0.0 ? String.format("%.2f", clonedTx.getAmount()) : ""); 
        descField.setText(clonedTx.getDescription());
        
        currencyChoice.setValue("UAH"); 
        
        
        categoryChoice.getSelectionModel().select(clonedTx.getCategory());
        accountChoice.getSelectionModel().select(clonedTx.getAccount());

        messageLabel.setText("Форма заповнена шаблоном '" + template.getName() + "'. Змініть суму та додайте.");
    }
    
    
    @FXML
    private void onManageTemplates() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/template_manager.fxml"));
            Parent root = loader.load();
            
            TemplateManagerController controller = loader.getController();
            controller.setParentController(this); 
            Stage stage = new Stage();
            stage.setTitle("Управління Шаблонами Транзакцій");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.showAndWait();
        } catch (IOException e) {
            messageLabel.setText("Помилка завантаження вікна шаблонів: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void onSaveAsTemplate() {
        Optional<String> result = showTemplateNameDialog();
        
        if (result.isPresent() && !result.get().isBlank()) {
            TransactionTemplate t = new TransactionTemplate();
            
            t.setName(result.get());
            t.setType(typeChoice.getValue());
            t.setDefaultAmount(getDoubleFromField(amountField.getText())); 
            t.setCategory(categoryChoice.getValue());
            t.setAccount(accountChoice.getValue());
            t.setDescription(descField.getText());
            t.setUser(user); 
            
        
            templateClient.create(t); 
            messageLabel.setText("Шаблон '" + t.getName() + "' успішно збережено.");
            refresh(); 
        }
    }
    
    private Optional<String> showTemplateNameDialog() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Зберегти Шаблон");
        dialog.setHeaderText("Введіть назву для цього шаблону:");
        dialog.setContentText("Назва:");
        return dialog.showAndWait();
    }
    
    private double getDoubleFromField(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    
    @FXML
    private void onAdd(){
        String amountText = amountField.getText();
        String type = typeChoice.getValue();
        Category cat = categoryChoice.getValue();
        Account acc = accountChoice.getValue();
        
        
        String currency = currencyChoice.getValue(); 
        
        
        if (type == null) { messageLabel.setText("Виберіть тип транзакції."); return; }
        if (acc == null) { messageLabel.setText("Виберіть Рахунок (Account)."); return; }
        if (cat == null) { messageLabel.setText("Виберіть Категорію."); return; }
        if (currency == null) { messageLabel.setText("Виберіть Валюту."); return; } 
        
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
            
            
            tx.setCurrency(currency); 
            
            
            transactionProcessor.create(tx); 

            amountField.clear(); 
            descField.clear();
            refresh();

        } catch(NumberFormatException ex){ 
            messageLabel.setText("Некоректна сума (потрібне число, наприклад: 100.50)"); 
        } catch (RuntimeException ex) {
            System.err.println("Transaction failed: " + ex.getMessage());
        }
    }

    @FXML
    private void onBack() throws IOException {
        ApplicationSession.getInstance().login(user);
    }
}