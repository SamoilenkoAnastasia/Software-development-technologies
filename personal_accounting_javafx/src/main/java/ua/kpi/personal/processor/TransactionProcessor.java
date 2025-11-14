package ua.kpi.personal.processor;

import ua.kpi.personal.model.Transaction;

public interface TransactionProcessor { 
    Transaction create(Transaction tx);
}