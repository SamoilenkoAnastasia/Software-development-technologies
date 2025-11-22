package ua.kpi.personal.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ua.kpi.personal.model.TransactionTemplate;
import ua.kpi.personal.repo.TemplateClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TemplateManagerController {
    @FXML private ListView<TransactionTemplate> templateListView;
    @FXML private TextField searchField; 
    
    
    private final TemplateClient templateClient = new TemplateClient(); 
    private TransactionsController parentController; 
    private ObservableList<TransactionTemplate> masterList; 

    public void setParentController(TransactionsController controller) {
        this.parentController = controller;
        loadMasterList(); 
        refreshList();   
    }

    
    private void loadMasterList() {
        
        List<TransactionTemplate> templates = templateClient.findByUserId(parentController.getUser().getId()); 
        this.masterList = FXCollections.observableArrayList(templates);
    }

    
    private void refreshList() {
        String searchText = searchField.getText().toLowerCase();

        if (searchText.isEmpty()) {
            templateListView.getItems().setAll(masterList);
        } else {
            List<TransactionTemplate> filtered = masterList.stream()
                .filter(t -> t.getName().toLowerCase().contains(searchText) || 
                             (t.getDescription() != null && t.getDescription().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());
            templateListView.getItems().setAll(filtered);
        }
    }
    
    @FXML
    private void initialize() {

        templateListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !templateListView.getSelectionModel().isEmpty()) {
                onSelectTemplate();
            }
        });

        
        searchField.textProperty().addListener((obs, oldText, newText) -> refreshList());
        
        
        templateListView.setCellFactory(lv -> new ListCell<TransactionTemplate>() {
            @Override
            protected void updateItem(TransactionTemplate template, boolean empty) {
                super.updateItem(template, empty);
                if (empty || template == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String amount = template.getDefaultAmount() != null && template.getDefaultAmount() != 0.0 ? 
                                     String.format(" (%.2f)", template.getDefaultAmount()) : "";
                    String type = template.getType().equals("EXPENSE") ? "Витрата" : "Дохід";
                    
                    setText(String.format("? %s %s%s\n  Категорія: %s, Рахунок: %s",
                                          template.getName(), 
                                          amount,
                                          type,
                                          template.getCategory() != null ? template.getCategory().getName() : "Не задано",
                                          template.getAccount() != null ? template.getAccount().getName() : "Не задано"));
                }
            }
        });
    }

    @FXML
    private void onSelectTemplate() {
        TransactionTemplate selected = templateListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            parentController.fillFormWithTemplate(selected); 
            ((Stage) templateListView.getScene().getWindow()).close(); 
        } else {
            showAlert("Помилка", "Виберіть шаблон для використання.");
        }
    }

    @FXML
    private void onDeleteTemplate() {
        TransactionTemplate selected = templateListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Optional<ButtonType> result = showConfirmationDialog(
                "Підтвердження", 
                "Ви впевнені, що хочете видалити шаблон '" + selected.getName() + "'. Це не можна скасувати."
            );
            
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // ЗМІНА: Викликаємо метод клієнта
                if (templateClient.delete(selected.getId())) { 
                    showAlert("Успіх", "Шаблон видалено.");
                    loadMasterList(); 
                    refreshList(); 
                    parentController.refresh(); 
                } else {
                    showAlert("Помилка", "Не вдалося видалити шаблон.");
                }
            }
        } else {
            showAlert("Помилка", "Виберіть шаблон для видалення.");
        }
    }

    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private Optional<ButtonType> showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();
    }
    
   
    @FXML
    private void onCancel() {
        ((Stage) templateListView.getScene().getWindow()).close(); 
    }
}