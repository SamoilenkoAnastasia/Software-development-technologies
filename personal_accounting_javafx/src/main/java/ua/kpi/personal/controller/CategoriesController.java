package ua.kpi.personal.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ua.kpi.personal.model.Category;
import ua.kpi.personal.repo.CategoryDao;
import ua.kpi.personal.model.User;
import ua.kpi.personal.state.ApplicationSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class CategoriesController {
    @FXML private ListView<Category> listView;
    @FXML private TextField nameField;
    @FXML private ChoiceBox<String> typeChoice;
    @FXML private ChoiceBox<Category> parentChoice; 
    @FXML private Label messageLabel;
    @FXML private Button backBtn;
    @FXML private Button addButton; 
    @FXML private Button cancelEditBtn; 
    
    private Category editingCategory = null; 

    private final CategoryDao categoryDao = new CategoryDao();
    private User user;
    private Map<Long, Category> categoryMap = new HashMap<>(); 

    @FXML
    private void initialize(){
        this.user = ApplicationSession.getInstance().getCurrentUser();
        typeChoice.getItems().addAll("EXPENSE", "INCOME");
        typeChoice.setValue("EXPENSE");
        
        listView.setCellFactory(lv -> new CategoryListCell(categoryMap));
        
        refresh();
        
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null && editingCategory != null) {
                onCancelEdit();
            }
        });
    }

    private void refresh(){
        if (user != null) {
            List<Category> allCategories = categoryDao.findByUserId(user.getId());
            
            categoryMap = allCategories.stream()
                .collect(Collectors.toMap(Category::getId, category -> category));

            listView.setItems(FXCollections.observableArrayList(allCategories));
            
            Category selectedParent = parentChoice.getValue(); 
            parentChoice.getItems().clear();
            parentChoice.getItems().add(0, null); 
            parentChoice.getItems().addAll(allCategories);
            
            if (selectedParent != null && parentChoice.getItems().contains(selectedParent)) {
                parentChoice.setValue(selectedParent);
            } else {
                parentChoice.getSelectionModel().select(0);
            }
            
        } else {
            listView.setItems(FXCollections.emptyObservableList());
            System.err.println("User object is null in CategoriesController. Cannot refresh.");
        }
        
        onCancelEdit();
    }

    @FXML
    private void onAdd(){
        String name = nameField.getText();
        String type = typeChoice.getValue();
        Category parent = parentChoice.getValue();

        if(name==null || name.isBlank()){ messageLabel.setText("Назва обов'язкова"); return; }
        if(type==null){ messageLabel.setText("Тип обов'язковий"); return; }

        Long parentId = (parent != null) ? parent.getId() : null;
        
        if (editingCategory != null) {
            // РЕЖИМ ОНОВЛЕННЯ
            Category updatedCategory = editingCategory.withUpdate(name, type, parentId);

            if (categoryDao.update(updatedCategory)) {
                messageLabel.setText("Категорія оновлена: " + updatedCategory.getName());
                refresh();
            } else {
                messageLabel.setText("Помилка оновлення категорії.");
            }
        } else {
            // РЕЖИМ ДОДАВАННЯ
            Category newCategory = new Category(user.getId(), name, type, parentId);
        
            Category created = categoryDao.create(newCategory);
            
            if(created!=null){
                messageLabel.setText("Додана категорія: " + created.getName());
                refresh();
            } else {
                messageLabel.setText("Помилка збереження в базі даних.");
            }
        }
    }
    
    @FXML
    private void onEdit() {
        Category selectedCategory = listView.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            messageLabel.setText("Виберіть категорію для редагування.");
            return;
        }
        
        if (selectedCategory.getUserId() == null) {
            messageLabel.setText("Системні категорії не можна редагувати.");
            return;
        }

        editingCategory = selectedCategory;
        
        nameField.setText(selectedCategory.getName());
        typeChoice.setValue(selectedCategory.getType());
        
        if (selectedCategory.getParentId() != null) {
            Category parent = categoryMap.get(selectedCategory.getParentId());
            if (parent != null && parentChoice.getItems().contains(parent)) {
                parentChoice.setValue(parent);
            } else {
                parentChoice.getSelectionModel().select(0);
            }
        } else {
            parentChoice.getSelectionModel().select(0); // null
        }
        
        if (addButton != null) addButton.setText("Оновити");
        messageLabel.setText("Редагування категорії: " + selectedCategory.getName() + ". Натисніть 'Оновити' для збереження.");
    }

    @FXML
    private void onCancelEdit() {
        editingCategory = null;
        nameField.clear();
        
        if (typeChoice != null && typeChoice.getItems().contains("EXPENSE")) {
            typeChoice.setValue("EXPENSE");
        } else if (typeChoice != null) {
            typeChoice.getSelectionModel().selectFirst();
        }
        
        if (parentChoice != null) {
            parentChoice.getSelectionModel().select(0); 
        }
        
        if (addButton != null) addButton.setText("Додати");
        messageLabel.setText("Готовий до додавання нової категорії.");
        if (listView != null) {
            listView.getSelectionModel().clearSelection();
        }
    }


    @FXML
    private void onDelete() {
        Category selectedCategory = listView.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            messageLabel.setText("Виберіть категорію для видалення.");
            return;
        }
        
        if (selectedCategory.getUserId() == null) {
            messageLabel.setText("Системні категорії не можна видаляти.");
            return;
        }

        boolean hasChildren = listView.getItems().stream()
                                     .anyMatch(c -> c.getParentId() != null && c.getParentId().equals(selectedCategory.getId()));
        if (hasChildren) {
             messageLabel.setText("Спочатку видаліть підкатегорії.");
             return;
        }

        if (categoryDao.delete(selectedCategory.getId())) {
            messageLabel.setText("Категорія '" + selectedCategory.getName() + "' видалена.");
            refresh();
        } else {
            messageLabel.setText("Помилка видалення категорії. Перевірте, чи немає пов'язаних транзакцій.");
        }
    }


    @FXML
    private void onBack() throws IOException {
        ApplicationSession.getInstance().login(user);
    }
    
    private class CategoryListCell extends ListCell<Category> {
        private final Map<Long, Category> categoryMap;

        public CategoryListCell(Map<Long, Category> categoryMap) {
            this.categoryMap = categoryMap;
        }
        
        @Override
        protected void updateItem(Category category, boolean empty) {
            super.updateItem(category, empty);
            
            if (empty || category == null) {
                setText(null);
                setGraphic(null);
            } else {
                String textToDisplay = category.getName();
                
                if (category.getParentId() != null) {
                    Category parent = categoryMap.get(category.getParentId());
                    if (parent != null) {
                        textToDisplay = parent.getName() + " > " + category.getName();
                    } else {
                        textToDisplay = "(Невідома Категорія): " + category.getName(); 
                    }
                }

                setText(textToDisplay);
                setGraphic(null); 
            }
        }
    }
}