/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kpi.pa.client.app;

/**
 *
 * @author ANAST
 */
import javafx.application.Application;

/**
 * Клас-обгортка для коректного запуску JavaFX-додатка з NetBeans/Maven.
 */
public class Launcher {
    public static void main(String[] args) {
        // Викликаємо метод launch() головного JavaFX-класу
        Application.launch(PersonalAccountingApp.class, args);
    }
}