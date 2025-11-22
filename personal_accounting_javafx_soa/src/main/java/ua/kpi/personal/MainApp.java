package ua.kpi.personal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import ua.kpi.personal.state.ApplicationSession;
import java.io.IOException;
import ua.kpi.personal.controller.LoginController;

public class MainApp extends Application {
    
@Override
public void start(Stage stage) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
    Parent root = loader.load();

    Scene scene = new Scene(root);
    stage.setScene(scene);

    
    LoginController controller = loader.getController();
    controller.setStage(stage);

    stage.setTitle("Особиста бухгалтерія");
    stage.setResizable(false);
    stage.show();

    ApplicationSession.initialize(stage);
}


    public static void main(String[] args) {
        launch();
    }
}