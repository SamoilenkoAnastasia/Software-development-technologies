package ua.kpi.personal.analytics.report;

import ua.kpi.personal.model.User;
import ua.kpi.personal.model.Transaction;
import ua.kpi.personal.model.analytics.ReportParams;
import ua.kpi.personal.model.analytics.ReportDataSet;
import ua.kpi.personal.model.analytics.ReportDataPoint;
import ua.kpi.personal.repo.TransactionClient; // Залишаємо
import ua.kpi.personal.processor.TransactionProcessor; // Залишаємо
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AllTransactionsReport extends FinancialReport {

    private final TransactionClient transactionClient; // ЗМІНА: Зберігаємо клієнта в окремому полі

    private static class SimpleReportDataPoint extends ReportDataPoint {
        
        public SimpleReportDataPoint(String key, double value) {
            super(key, value, 0.0);
        }
    }

    // ВИПРАВЛЕННЯ: Конструктор приймає процесор, але витягує з нього Client
    public AllTransactionsReport(TransactionProcessor processor) { 
        super(processor); 
        
        // Витягуємо фактичний клієнт, оскільки findTransactionsByDateRange є специфічним для клієнта/репо
        if (processor instanceof TransactionClient) {
            this.transactionClient = (TransactionClient) processor;
        } else if (processor instanceof FinancialReport) {
             // Якщо FinancialReport є батьківським класом і містить transactionClient, 
             // потрібно використати геттер або привести. Припускаємо, що FinancialReport має геттер.
             // В іншому випадку приводимо Processor до Client.
             // Оскільки у вас FinancialReport є абстрактним, краще переконатися, що процесор є Client.
             this.transactionClient = getClientFromProcessor(processor); 
        } else {
             throw new IllegalArgumentException("TransactionProcessor must be an instance of TransactionClient or wrap it.");
        }
    }
    
    // Допоміжний метод для витягування TransactionClient (якщо використовується Декоратор)
    // Якщо ви не використовуєте складні декоратори, цей простий підхід буде працювати:
    private TransactionClient getClientFromProcessor(TransactionProcessor processor) {
        if (processor instanceof TransactionClient) {
            return (TransactionClient) processor;
        }
        throw new IllegalArgumentException("Processor must be TransactionClient");
    }


    @Override 
    protected ReportDataSet analyze(ReportParams params, User user) {
        String title = "Загальний звіт доходів і витрат";
        String[] headers = {"Дата", "Тип", "Категорія", "Сума", "Рахунок"};
        
        // ВИПРАВЛЕННЯ: Викликаємо метод на об'єкті transactionClient
        List<Transaction> transactions = transactionClient.findTransactionsByDateRange(params, user.getId()); 

        double totalIncome = transactions.stream()
            .filter(t -> "INCOME".equalsIgnoreCase(t.getType()))
            .mapToDouble(Transaction::getAmount)
            .sum();
            
        double totalExpense = transactions.stream()
            .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
            .mapToDouble(Transaction::getAmount)
            .sum();
        
        String summary = String.format("? Дохід: %.2f | ? Витрата: %.2f | ? Баланс: %.2f", 
                                        totalIncome, totalExpense, totalIncome - totalExpense);

        return new ReportDataSet(title, headers, (List<ReportDataPoint>) (List<?>) transactions, summary); 
    }

    @Override 
    protected void renderTable(ReportDataSet dataSet) {
        // Зміни відсутні, логіка виведення залишається
        List<Transaction> transactions = (List<Transaction>) (List<?>) dataSet.getDataPoints(); 
        renderer.renderAllTransactionsTable(transactions);
    }
    
    @Override 
    protected void renderChart(ReportDataSet dataSet) {
        // Зміни відсутні, логіка виведення залишається
        List<Transaction> transactions = (List<Transaction>) (List<?>) dataSet.getDataPoints(); 
        
        double totalIncome = transactions.stream()
            .filter(t -> "INCOME".equalsIgnoreCase(t.getType()))
            .mapToDouble(Transaction::getAmount)
            .sum();
            
        double totalExpense = transactions.stream()
            .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
            .mapToDouble(Transaction::getAmount)
            .sum();

        List<ReportDataPoint> chartData = new ArrayList<>();
        
        if (totalIncome > 0) {
            chartData.add(new SimpleReportDataPoint("Дохід", totalIncome));
        }
        if (totalExpense > 0) {
            chartData.add(new SimpleReportDataPoint("Витрата", totalExpense));
        }

        renderer.renderChart(chartData);
    }
}