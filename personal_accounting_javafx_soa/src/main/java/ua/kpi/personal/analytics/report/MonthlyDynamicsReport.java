package ua.kpi.personal.analytics.report;

import ua.kpi.personal.model.User;
import ua.kpi.personal.model.analytics.ReportDataSet;
import ua.kpi.personal.model.analytics.ReportParams;
import ua.kpi.personal.repo.TransactionClient; // <<< ЗМІНА: Імпорт клієнта
import ua.kpi.personal.processor.TransactionProcessor; // <<< Імпорт інтерфейсу
import ua.kpi.personal.model.analytics.ReportDataPoint; 


public class MonthlyDynamicsReport extends FinancialReport {

    // ЗМІНА: Конструктор приймає TransactionProcessor (TransactionClient)
    public MonthlyDynamicsReport(TransactionProcessor client) { 
        super(client);
    }

    @Override
    protected ReportDataSet analyze(ReportParams params, User user) {
        // УВАГА: Тут виникає проблема. Метод analyze у старому коді був нереалізований 
        // і повертав порожній List. Якщо ваш бекенд надає ендпоінт для "Динаміки по Місяцях",
        // його потрібно викликати тут через transactionClient.

        // Приклад, як це може виглядати (якщо бекенд має ендпоінт /analytics/monthly):
        /*
        try {
             String path = "/analytics/monthly?startDate=...&endDate=...";
             String jsonResponse = ApiClient.get(path);
             List<ReportDataPoint> data = mapper.readValue(jsonResponse, new TypeReference<List<ReportDataPoint>>() {});
             return new ReportDataSet("Динаміка по Місяцях", new String[]{"Місяць", "Сума", "Зміна"}, data, "Аналіз динаміки.");
        } catch (Exception e) {
             e.printStackTrace();
             // Обробка помилок
        }
        */
        
        return new ReportDataSet("Динаміка по Місяцях", new String[]{"Місяць", "Сума", "Зміна"}, java.util.Collections.emptyList(), "Аналіз динаміки.");
    }

    @Override
    protected void renderTable(ReportDataSet dataSet) {
        renderer.render(dataSet);
    }

    @Override
    protected void renderChart(ReportDataSet dataSet) {
        renderer.renderChart(dataSet.getDataPoints());
    }
}