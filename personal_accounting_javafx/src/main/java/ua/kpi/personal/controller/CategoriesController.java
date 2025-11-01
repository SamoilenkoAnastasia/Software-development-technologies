package ua.kpi.personal.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ua.kpi.personal.model.Category;
import ua.kpi.personal.repo.CategoryDao;
import ua.kpi.personal.model.User;
import ua.kpi.personal.state.ApplicationSession; 
import java.io.IOException;



public class CategoriesController {
    @FXML private ListView<Category> listView;
    @FXML private TextField nameField;
    @FXML private ChoiceBox<String> typeChoice;
    @FXML private Label messageLabel;
    @FXML private Button backBtn;

    private final CategoryDao categoryDao = new CategoryDao();
    private User user; 

    @FXML
    private void initialize(){
        this.user = ApplicationSession.getInstance().getCurrentUser();
        typeChoice.getItems().addAll("EXPENSE", "INCOME"); 
        typeChoice.setValue("EXPENSE"); 
        
        refresh(); 
    }

    

    private void refresh(){
        
        if (user != null) {
            listView.setItems(FXCollections.observableArrayList(categoryDao.findByUserId(user.getId())));
        } else {
            listView.setItems(FXCollections.emptyObservableList());
            System.err.println("User object is null in CategoriesController. Cannot refresh.");
        }
    }

    @FXML
    private void onAdd(){
        
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
        
        ApplicationSession.getInstance().login(user);
    }
}