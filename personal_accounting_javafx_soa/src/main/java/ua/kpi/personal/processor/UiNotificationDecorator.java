package ua.kpi.personal.processor;

import ua.kpi.personal.controller.TransactionsController;
import ua.kpi.personal.model.Transaction;


public class UiNotificationDecorator extends TransactionDecorator {

    private final TransactionsController controller;
    
    public UiNotificationDecorator(TransactionProcessor wrappedProcessor, TransactionsController controller) {
        super(wrappedProcessor);
        this.controller = controller;
    }

    @Override
    public Transaction create(Transaction tx) {
        try {
            
            Transaction savedTx = super.create(tx);
          
            String type = savedTx.getType().equals("EXPENSE") ? "Витрату" : "Дохід";
            String successMsg = "? " + type + " успішно збережено!";
            
            controller.displaySuccessDialog(successMsg); 
            
            return savedTx;
            
        } catch (RuntimeException e) {
            
            String errorMsg = e.getMessage();
            controller.displayErrorDialog(errorMsg);
            throw e; 
        }
    }
}