package ua.kpi.personal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ua.kpi.personal.util.Db;
import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Db.init(); // initialize DB (create tables if not exist)
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("ќсобиста бухгалтер≥€ Ч Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
