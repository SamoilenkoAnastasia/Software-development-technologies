package ua.kpi.personal.processor;

import ua.kpi.personal.model.Transaction;

public class CurrencyDecorator extends TransactionDecorator {

    private static final String BASE_CURRENCY = "UAH";
    private static final double USD_RATE = 42.0; 
    private static final double EUR_RATE = 51.5; 

    public CurrencyDecorator(TransactionProcessor wrappedProcessor) {
        super(wrappedProcessor);
    }

    @Override
    public Transaction create(Transaction tx) {
        
        String inputCurrency = tx.getCurrency(); 
        double originalAmount = tx.getAmount();

        if (inputCurrency != null && !BASE_CURRENCY.equals(inputCurrency)) {
            
            double exchangeRate = 1.0;
            
            switch (inputCurrency) {
                case "USD":
                    exchangeRate = USD_RATE;
                    break;
                case "EUR":
                    exchangeRate = EUR_RATE;
                    break; 
            }
            
            double convertedAmount = originalAmount * exchangeRate;
            
            tx.setAmount(convertedAmount);
            tx.setDescription(tx.getDescription().trim() + 
                             " (конвертовано з " + inputCurrency + ": " + 
                             String.format("%.2f", originalAmount) + ")");
            
            System.out.println("? CurrencyDecorator: Конвертовано " + originalAmount + " " + inputCurrency + 
                               " на " + convertedAmount + " " + BASE_CURRENCY);
        }
        
        return super.create(tx);
    }
}