package ua.kpi.personal.processor;

import ua.kpi.personal.model.Transaction;

public abstract class TransactionDecorator implements TransactionProcessor {

    protected TransactionProcessor wrappedProcessor;

    public TransactionDecorator(TransactionProcessor wrappedProcessor) {
        this.wrappedProcessor = wrappedProcessor;
    }

    
    @Override
    public Transaction create(Transaction tx) {
        return wrappedProcessor.create(tx);
    }
}