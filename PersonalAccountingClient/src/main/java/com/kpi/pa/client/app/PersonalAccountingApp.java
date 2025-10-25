/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kpi.pa.client.app;

/**
 *
 * @author ANAST
 */
import com.kpi.pa.data.DbConfig; // Імпортуємо наш клас конфігурації
import javafx.application.Application;
import javafx.stage.Stage;

public class PersonalAccountingApp extends Application {

    @Override
    public void start(Stage stage) {
        // ТЕСТ З'ЄДНАННЯ ПЕРЕД ЗАПУСКОМ UI
        DbConfig.testConnection(); 

        // Запуск UI-частини (будемо реалізовувати пізніше)
        // System.out.println("Запуск користувацького інтерфейсу...");
        // ... (код для завантаження FXML)
    }

    public static void main(String[] args) {
        launch();
    }
}