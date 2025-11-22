package com.kpi.pa.service.service;

import com.kpi.pa.service.dto.TransactionDTO;
import com.kpi.pa.service.model.Transaction;
import com.kpi.pa.service.repo.TransactionRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepo transactionRepo;

    public TransactionService(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    public List<TransactionDTO> getTransactionsByUser(Long userId, LocalDateTime start, LocalDateTime end) {
        List<Transaction> transactions = transactionRepo.findAllByUser_IdAndCreatedAtBetween(userId, start, end);
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TransactionDTO convertToDTO(Transaction t) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(t.getId());
        dto.setUserId(t.getUser() != null ? t.getUser().getId() : null);
        dto.setUsername(t.getUser() != null ? t.getUser().getUsername() : null);

        dto.setAccountId(t.getAccount() != null ? t.getAccount().getId() : null);
        dto.setAccountName(t.getAccount() != null ? t.getAccount().getName() : null);

        dto.setCategoryId(t.getCategory() != null ? t.getCategory().getId() : null);
        dto.setCategoryName(t.getCategory() != null ? t.getCategory().getName() : null);

        dto.setAmount(t.getAmount());
        dto.setType(t.getType());
        dto.setDescription(t.getDescription());
        dto.setCurrency(t.getCurrency());
        dto.setCreatedAt(t.getCreatedAt());

        return dto;
    }
}
