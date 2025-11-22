package ua.kpi.personal.analytics.report;

import ua.kpi.personal.analytics.output.OutputRenderer;
import ua.kpi.personal.model.analytics.ReportDataSet;
import ua.kpi.personal.model.analytics.ReportParams;
import ua.kpi.personal.repo.TransactionClient; // <<< ЗМІНА: Використовуємо клієнт
import ua.kpi.personal.processor.TransactionProcessor; // <<< Використовуємо інтерфейс, якщо він є
import ua.kpi.personal.model.User;

public abstract class FinancialReport {
    
    protected OutputRenderer renderer;
    // ЗМІНА: Тип об'єкта змінюється з TransactionDao на TransactionProcessor/TransactionClient
    protected final TransactionProcessor transactionClient; 

    // ЗМІНА: Конструктор приймає TransactionProcessor або TransactionClient
    public FinancialReport(TransactionProcessor client) { 
        this.transactionClient = client;
    }

    public void setOutputRenderer(OutputRenderer renderer) {
        this.renderer = renderer;
    }
    
    protected abstract void renderTable(ReportDataSet dataSet);
    protected abstract void renderChart(ReportDataSet dataSet);
    
    protected abstract ReportDataSet analyze(ReportParams params, User user);

    
    public final void generate(ReportParams params, User user) {
        if (renderer == null) {
            throw new IllegalStateException("OutputRenderer (Міст) не встановлено. Викличте setOutputRenderer().");
        }
        
        ReportDataSet dataSet = analyze(params, user);
        
        renderTable(dataSet);
        renderChart(dataSet);
    }
}