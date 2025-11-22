package ua.kpi.personal.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; 
import javafx.scene.Scene; 
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage; 
import ua.kpi.personal.analytics.output.ExcelRenderer;
import ua.kpi.personal.analytics.output.JavaFxScreenRenderer;
import ua.kpi.personal.analytics.output.OutputRenderer;
import ua.kpi.personal.analytics.report.AllTransactionsReport;
import ua.kpi.personal.analytics.report.FinancialReport;
import ua.kpi.personal.model.analytics.ReportDataPoint;
import ua.kpi.personal.model.analytics.ReportParams;
import ua.kpi.personal.repo.TransactionClient; 
import ua.kpi.personal.processor.TransactionProcessor; 
import ua.kpi.personal.state.ApplicationSession;
import ua.kpi.personal.model.User;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import ua.kpi.personal.analytics.output.PdfFileWriter;
import ua.kpi.personal.analytics.report.MonthlyDynamicsReport;
import javafx.scene.Parent;

public class ReportsController {

    
    private final TransactionProcessor transactionClient; 
    private final ApplicationSession session;
    private final MainController mainController;


    @FXML private ComboBox<String> reportTypeCombo;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button generateButton;
    @FXML private Button exportExcelButton;
    @FXML private Button exportPdfButton;
    @FXML private Button backButton;
    @FXML private TabPane visualizationTabPane;
    @FXML private Label summaryLabel;

    @FXML private TableView<ReportDataPoint> reportTableView;
    @FXML private AnchorPane chartContainer;

    private FinancialReport currentReportLogic;
    private JavaFxScreenRenderer screenRenderer;


    
    public ReportsController(TransactionProcessor client, ApplicationSession session, MainController mainController) {
        this.transactionClient = client;
        this.session = session;
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        
        reportTypeCombo.getItems().addAll("Загальний звіт доходів і витрат", "Динаміка по Місяцях");
        reportTypeCombo.getSelectionModel().selectFirst();
        startDatePicker.setValue(LocalDate.now().minusMonths(1));
        endDatePicker.setValue(LocalDate.now());

        generateButton.setOnAction(event -> generateReport());
        exportExcelButton.setOnAction(event -> exportReport("Excel"));
        exportPdfButton.setOnAction(event -> exportReport("PDF"));
        
        screenRenderer = new JavaFxScreenRenderer(reportTableView, summaryLabel, chartContainer);

        
        if (backButton != null) {
            backButton.setOnAction(event -> onBack());
        }

        generateReport();
    }
    
    
    @FXML
    private void onBack() {
        try {
           
            Stage stage = (Stage) backButton.getScene().getWindow();
            
           
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent mainPane = loader.load(); 

           
            MainController controller = loader.getController();
            if (controller != null) {
                User user = session.getCurrentUser();
                if (user != null) {
                    controller.initData(user);
                }
            }

            
            Scene scene = new Scene(mainPane);
            stage.setScene(scene);
            stage.setTitle("Персональний Облік - Головна");

        } catch (IOException e) {
            System.err.println("Помилка при поверненні до головного меню: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void generateReport() {
        if (session.getCurrentUser() == null) {
            summaryLabel.setText("Помилка: Немає активного користувача.");
            return;
        }

        ReportParams params = createReportParams();
        currentReportLogic = createReportLogic(reportTypeCombo.getValue());

        currentReportLogic.setOutputRenderer(screenRenderer);
        currentReportLogic.generate(params, session.getCurrentUser());

        summaryLabel.setText("Звіт готовий. Виберіть вкладку для перегляду.");
    }

   private void exportReport(String format) {
        if (currentReportLogic == null) {
            summaryLabel.setText("Спочатку побудуйте звіт.");
            return;
        }

        OutputRenderer fileWriter;
        var ownerWindow = generateButton.getScene().getWindow();

        String reportName = reportTypeCombo.getValue().replace(" ", "_");
       

        if ("Excel".equals(format)) {

            fileWriter = new ExcelRenderer(ownerWindow);
        } else if ("PDF".equals(format)) {

            fileWriter = new PdfFileWriter(ownerWindow);
        } else {
            return;
        }

        currentReportLogic.setOutputRenderer(fileWriter);
        currentReportLogic.generate(createReportParams(), session.getCurrentUser());

        summaryLabel.setText("Експорт у " + format + " завершено.");
    }

    private ReportParams createReportParams() {
        return new ReportParams(
            startDatePicker.getValue(),
            endDatePicker.getValue(),
            Collections.emptyList(),
            Collections.emptyList(),
            "ALL"
        );
    }

    private FinancialReport createReportLogic(String type) {
        switch (type) {
            
            case "Загальний звіт доходів і витрат": 
                return new AllTransactionsReport(transactionClient); 
         
            case "Динаміка по Місяцях":
                return new MonthlyDynamicsReport(transactionClient); 
            default:
                
                throw new IllegalArgumentException("Невідомий тип звіту.");
        }
    }
}