package ua.kpi.personal.processor;

import ua.kpi.personal.model.Account;
import ua.kpi.personal.model.Transaction;

public class BalanceCheckDecorator extends TransactionDecorator {

    public BalanceCheckDecorator(TransactionProcessor wrappedProcessor) {
        super(wrappedProcessor);
    }

    @Override
    public Transaction create(Transaction tx) {
       
        if ("EXPENSE".equals(tx.getType())) {
            
            Account account = tx.getAccount();
            double currentBalance = account.getBalance() != null ? account.getBalance() : 0.0;
            double transactionAmount = tx.getAmount();

            
            if (currentBalance < transactionAmount) {
                String errorMessage = String.format(
                    "Помилка: Недостатньо коштів на рахунку '%s'. Поточний баланс: %.2f %s, необхідна сума: %.2f %s.",
                    account.getName(), 
                    currentBalance, account.getCurrency(), 
                    transactionAmount, account.getCurrency() 
                );
                
                throw new RuntimeException(errorMessage); 
            }
            
            System.out.println("BalanceCheckDecorator: Баланс OK. Продовжуємо обробку.");
        }
         
        return super.create(tx);
    }
}