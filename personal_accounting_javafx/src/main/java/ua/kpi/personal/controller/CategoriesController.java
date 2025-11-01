package ua.kpi.personal.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ua.kpi.personal.model.Category;
import ua.kpi.personal.repo.CategoryDao;
import ua.kpi.personal.model.User;
import ua.kpi.personal.state.ApplicationSession; // <--- НОВИЙ ІМПОРТ ДЛЯ PATTERN
import java.io.IOException;

// Видаляємо непотрібні імпорти FXMLLoader, Scene
// import javafx.fxml.FXMLLoader;
// import javafx.scene.Scene;

public class CategoriesController {
    @FXML private ListView<Category> listView;
    @FXML private TextField nameField;
    @FXML private ChoiceBox<String> typeChoice;
    @FXML private Label messageLabel;
    @FXML private Button backBtn;

    private final CategoryDao categoryDao = new CategoryDao();
    private User user; // Зберігаємо поле для бізнес-логіки

    @FXML
    private void initialize(){
        // *** ЗМІНА 1: Отримуємо користувача із сесії при ініціалізації ***
        this.user = ApplicationSession.getInstance().getCurrentUser();
        
        // Збережена логіка: Додаємо варіанти типу
        typeChoice.getItems().addAll("EXPENSE", "INCOME"); 
        typeChoice.setValue("EXPENSE"); // Встановлюємо значення за замовчуванням
        
        refresh(); // Тепер refresh спрацює, оскільки user встановлений
    }

    // *** ЗМІНА 2: ВИДАЛЯЄМО setUser() ***
    // public void setUser(User user) { ... }

    private void refresh(){
        // Збережена логіка: оновлення
        if (user != null) {
            // Завантажуємо категорії, прив'язані до користувача
            listView.setItems(FXCollections.observableArrayList(categoryDao.findByUserId(user.getId())));
        } else {
            listView.setItems(FXCollections.emptyObservableList());
            System.err.println("User object is null in CategoriesController. Cannot refresh.");
        }
    }

    @FXML
    private void onAdd(){
        // Збережена логіка: додавання категорії
        String name = nameField.getText();
        String type = typeChoice.getValue(); 
        
        if(name==null || name.isBlank()){ messageLabel.setText("Name required"); return; }
        if(type==null){ messageLabel.setText("Type required"); return; } 
        
        Category c = new Category();
        c.setName(name);
        c.setType(type);   
        c.setUser(user);   

        Category created = categoryDao.create(c);
        
        if(created!=null){ 
            messageLabel.setText("Added category: " + created.getName()); 
            nameField.clear(); 
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