package ua.kpi.personal.repo;

import ua.kpi.personal.model.Transaction;
import ua.kpi.personal.model.TransactionRequest;
import ua.kpi.personal.model.Account;
import ua.kpi.personal.model.analytics.ReportParams;
import ua.kpi.personal.util.ApiClient;
import ua.kpi.personal.processor.TransactionProcessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionClient implements TransactionProcessor {

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final static String API_PATH = "/transactions";

    
    public List<Transaction> findTransactionsByDateRange(ReportParams params, Long userId) {
        if (params == null || userId == null) return new ArrayList<>();

        String path = String.format("%s?startDate=%s&endDate=%s&userId=%d",
                API_PATH,
                params.getStartDate().format(formatter),
                params.getEndDate().format(formatter),
                userId);
        try {
            String jsonResponse = ApiClient.get(path);
            return ApiClient.MAPPER.readValue(jsonResponse, new TypeReference<List<Transaction>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Помилка API при отриманні транзакцій: " + e.getMessage());
        }
    }

    
    public List<Transaction> findByUserId(Long userId) {
        if (userId == null) return new ArrayList<>();

        try {
            LocalDateTime start = LocalDateTime.now().minusMonths(1); 
            LocalDateTime end = LocalDateTime.now();

            String path = String.format("%s?userId=%d&startDate=%s&endDate=%s",
                    API_PATH,
                    userId,
                    start.format(formatter),
                    end.format(formatter));

            String jsonResponse = ApiClient.get(path);
            return ApiClient.MAPPER.readValue(jsonResponse, new TypeReference<List<Transaction>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    
    @Override
    public Transaction create(Transaction tx) {
        try {
            
            Account account = tx.getAccount();
            if (account == null || account.getId() == null) {
                throw new IllegalArgumentException("Account is required");
            }

            Double amountValue = tx.getAmount();
            if (amountValue == null || amountValue <= 0.0) {
                throw new IllegalArgumentException("Amount must be positive");
            }

            String type = tx.getType() != null ? tx.getType().toUpperCase() : "EXPENSE";
            if (!"INCOME".equals(type) && !"EXPENSE".equals(type)) {
                throw new IllegalArgumentException("Invalid transaction type: " + type);
            }

            String description = tx.getDescription() != null ? tx.getDescription().trim() : "No description";

            
            double finalAmount = amountValue;
            String currency = tx.getCurrency();
            if (currency != null && !"UAH".equals(currency)) {
                switch (currency) {
                    case "USD": finalAmount = amountValue * 42.0; break;
                    case "EUR": finalAmount = amountValue * 51.5; break;
                    default: finalAmount = amountValue; break;
                }
                description += String.format(" (конвертовано з %s: %.2f)", currency, amountValue);
            }

           
            TransactionRequest request = new TransactionRequest();
            request.setAccountId(account.getId());
            request.setAmount(BigDecimal.valueOf(finalAmount));
            request.setTitle(description);
            request.setType(type);
            request.setTimestamp(tx.getCreatedAt() != null ? tx.getCreatedAt() : LocalDateTime.now());

         
            String jsonResponse = ApiClient.post(API_PATH, request);

            
            return ApiClient.MAPPER.readValue(jsonResponse, Transaction.class);

        } catch (IllegalArgumentException e) {
        
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Помилка API при створенні транзакції: " + e.getMessage());
        }
    }
}
