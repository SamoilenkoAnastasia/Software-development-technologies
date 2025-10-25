/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kpi.pa.client.controller;

import com.kpi.pa.service.api.IFinanceService;
import com.kpi.pa.service.api.impl.FinanceServiceImpl;
import com.kpi.pa.service.dto.TransactionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDateTime;

public class TransactionController {

    // FXML елементи, визначені у transaction-form.fxml
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> accountComboBox;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField amountField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker datePicker;
    @FXML private Label messageLabel;

    // Зв'язок із сервісним рівнем
    private final IFinanceService financeService;

    public TransactionController() {
        this.financeService = new FinanceServiceImpl();
    }

    /**
     * Ініціалізація контролера. Викликається після завантаження FXML.
     */
    @FXML
    public void initialize() {
        // Наповнюємо ComboBox'и даними
        typeComboBox.getItems().addAll("Дохід", "Витрата");
        accountComboBox.getItems().addAll(financeService.getAllAccountNames());
        categoryComboBox.getItems().addAll(financeService.getAllCategoryNames());

        // Встановлюємо значення за замовчуванням
        typeComboBox.getSelectionModel().select("Витрата");
        datePicker.setValue(java.time.LocalDate.now());
    }

    /**
     * Обробка натискання кнопки "Зберегти" [cite: 115]
     */
    @FXML
    private void handleSaveTransaction() {
        messageLabel.setText(""); // Очищаємо попередні повідомлення

        try {
            // 1. Збір даних з форми
            String type = typeComboBox.getValue();
            String accountName = accountComboBox.getValue();
            String categoryName = categoryComboBox.getValue();
            double amount = Double.parseDouble(amountField.getText().replace(',', '.')); // Для коректної обробки коми
            String description = descriptionArea.getText();
            LocalDateTime date = datePicker.getValue() != null ? datePicker.getValue().atStartOfDay() : LocalDateTime.now();

            // 2. Валідація на рівні контролера (частково)
            if (accountName == null || categoryName == null) {
                messageLabel.setText("❌ Виберіть рахунок та категорію."); // Виняток [cite: 122]
                return;
            }
            if (amount <= 0) {
                 messageLabel.setText("❌ Сума некоректна. Введіть позитивне число."); // Виняток [cite: 121]
                 return;
            }

            // 3. Передача даних до сервісного рівня [cite: 115]
            TransactionDTO dto = new TransactionDTO(type, amount, date, description, accountName, categoryName);
            boolean success = financeService.saveTransaction(dto);

            // 4. Відображення результату [cite: 119]
            if (success) {
                messageLabel.setText("✅ Транзакцію успішно додано!");
                // Очистити поля після успішного збереження
                amountField.clear();
                descriptionArea.clear();
            } else {
                messageLabel.setText("❌ Помилка збереження даних. Перевірте лог.");
            }

        } catch (NumberFormatException e) {
            messageLabel.setText("❌ Некоректний формат суми.");
        } catch (Exception e) {
            messageLabel.setText("❌ Виникла невідома помилка: " + e.getMessage());
        }
    }
}