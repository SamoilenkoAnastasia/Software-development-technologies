package ua.kpi.personal.controller;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // Додано для завантаження нового FXML
import javafx.scene.Parent;    // Додано
import javafx.scene.Scene;     // Додано
import javafx.scene.control.*;
import javafx.stage.Modality;  // Додано для модального вікна
import javafx.stage.Stage;     // Додано
import ua.kpi.personal.model.*;
import ua.kpi.personal.repo.*;
import java.time.LocalDateTime;
import java.util.Optional; 
import ua.kpi.personal.state.ApplicationSession;

public class TransactionsController {

    // --- Існуючі FXML Елементи ---
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

    // --- ВИДАЛЕНО: @FXML private ChoiceBox<TransactionTemplate> templateChoice; ---

    // --- DAO та Змінні ---
    private final TransactionDao transactionDao = new TransactionDao();
    private final CategoryDao categoryDao = new CategoryDao();
    private final AccountDao accountDao = new AccountDao();
    private final TemplateDao templateDao = new TemplateDao(); 
    private User user;

    @FXML
    private void initialize(){
        this.user = ApplicationSession.getInstance().getCurrentUser();
        
        // --- Існуюча логіка налаштування UI ---
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
        
        // --- ВИДАЛЕНО: templateChoice.getSelectionModel().addListener(...) ---
        // Логіка вибору шаблону тепер в TemplateManagerController
        
        refresh(); 
    }

    // Додаємо публічний геттер для User, щоб дочірній контролер міг отримати ID
    public User getUser() {
        return user;
    }

    void refresh(){
        if (user == null) return;
        
        // Оновлення таблиці
        table.setItems(
            FXCollections.observableArrayList(transactionDao.findByUserId(user.getId()))
        );
        
        // Оновлення Category та Account
        categoryChoice.setItems(
            FXCollections.observableArrayList(categoryDao.findByUserId(user.getId()))
        );
        accountChoice.setItems(
            FXCollections.observableArrayList(accountDao.findByUserId(user.getId()))
        );

        // --- ВИДАЛЕНО: Оновлення списку шаблонів (templateChoice) ---
    }

    // ===============================================
    //           МЕТОДИ ПАТЕРНУ ПРОТОТИП
    // ===============================================
    
    /**
     * Заповнює поля форми даними з обраного шаблону (прототипу).
     * Викликається з TemplateManagerController.
     * @param template Прототип TransactionTemplate.
     */
    public void fillFormWithTemplate(TransactionTemplate template) {
        // КЛЮЧОВИЙ МОМЕНТ: Створюємо нову транзакцію клонуванням!
        Transaction clonedTx = template.createTransactionFromTemplate(); 
        
        // Заповнюємо поля форми даними з клону
        typeChoice.setValue(clonedTx.getType());
        amountField.setText(clonedTx.getAmount() != 0.0 ? String.format("%.2f", clonedTx.getAmount()) : ""); 
        descField.setText(clonedTx.getDescription());
        
        // Встановлення об'єктів у ChoiceBox
        categoryChoice.getSelectionModel().select(clonedTx.getCategory());
        accountChoice.getSelectionModel().select(clonedTx.getAccount());

        messageLabel.setText("? Форма заповнена шаблоном '" + template.getName() + "'. Змініть суму та додайте.");
    }
    
    /**
     * Відкриває модальне вікно для вибору, пошуку та видалення шаблонів.
     */
    @FXML
    private void onManageTemplates() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/template_manager.fxml"));
            Parent root = loader.load();
            
            TemplateManagerController controller = loader.getController();
            controller.setParentController(this); // Передаємо посилання на себе
            
            Stage stage = new Stage();
            stage.setTitle("Управління Шаблонами Транзакцій");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Блокуємо головне вікно
            stage.showAndWait();
        } catch (IOException e) {
            messageLabel.setText("Помилка завантаження вікна шаблонів: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Відкриває діалогове вікно для збереження поточної транзакції як нового шаблону.
     */
    @FXML
    private void onSaveAsTemplate() {
        // ... (Логіка onSaveAsTemplate залишається без змін) ...
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
            
            templateDao.create(t);
            messageLabel.setText("? Шаблон '" + t.getName() + "' успішно збережено.");
            refresh(); 
        }
    }
    
    // Допоміжний метод для отримання назви шаблону через діалог
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


    // ===============================================
    //           ІСНУЮЧА ЛОГІКА КОНТРОЛЕРА
    // ===============================================

    @FXML
    private void onAdd(){
        String amountText = amountField.getText();
        String type = typeChoice.getValue();
        Category cat = categoryChoice.getValue();
        Account acc = accountChoice.getValue();
        
        // Перевірки
        if (type == null) { messageLabel.setText("Виберіть тип транзакції."); return; }
        if (acc == null) { messageLabel.setText("Виберіть Рахунок (Account)."); return; }
        if (cat == null) { messageLabel.setText("Виберіть Категорію."); return; }
        
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
                messageLabel.setText("Помилка при додаванні транзакції.");
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