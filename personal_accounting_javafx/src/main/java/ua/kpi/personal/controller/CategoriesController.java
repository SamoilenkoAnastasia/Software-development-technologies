package ua.kpi.personal.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ua.kpi.personal.model.Category;
import ua.kpi.personal.repo.CategoryDao;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import ua.kpi.personal.model.User;
import java.io.IOException;

public class CategoriesController {
    @FXML private ListView<Category> listView;
    @FXML private TextField nameField;
    @FXML private ChoiceBox<String> typeChoice; // <-- ДОДАНО: Вибір типу (EXPENSE/INCOME)
    @FXML private Label messageLabel;
    @FXML private Button backBtn;

    private final CategoryDao categoryDao = new CategoryDao();
    private User user;

    @FXML
    private void initialize(){
        // Додаємо варіанти типу
        typeChoice.getItems().addAll("EXPENSE", "INCOME"); 
        typeChoice.setValue("EXPENSE"); // Встановлюємо значення за замовчуванням
        refresh(); // Викликається, але не завантажить дані, поки не буде setUser
    }

    // ДОДАНО: Метод для передачі користувача
    public void setUser(User user) {
        this.user = user;
        refresh(); // Оновлюємо список після встановлення користувача
    }

    private void refresh(){
        if (user != null) {
            // ЗМІНА: Завантажуємо категорії, прив'язані до користувача
            listView.setItems(FXCollections.observableArrayList(categoryDao.findByUserId(user.getId())));
        } else {
            listView.setItems(FXCollections.emptyObservableList());
            System.err.println("User object is null in CategoriesController. Cannot refresh.");
        }
    }

    @FXML
    private void onAdd(){
        String name = nameField.getText();
        String type = typeChoice.getValue(); // <-- ДОДАНО: Отримання типу
        
        if(name==null || name.isBlank()){ messageLabel.setText("Name required"); return; }
        if(type==null){ messageLabel.setText("Type required"); return; } // Перевірка на тип
        
        Category c = new Category();
        c.setName(name);
        c.setType(type);    // <-- ВАЖЛИВО: Встановлюємо тип
        c.setUser(user);    // <-- ВАЖЛИВО: Встановлюємо користувача

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
    private void onBack() throws IOException { // Використовуйте IOException, якщо це можливо
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        javafx.stage.Stage stage = (javafx.stage.Stage) backBtn.getScene().getWindow();
        Scene scene = new Scene(loader.load());
        
        // ВАЖЛИВО: Передаємо User назад у MainController
        MainController ctrl = loader.getController();
        ctrl.setUser(user);
        
        stage.setScene(scene);
    }
}