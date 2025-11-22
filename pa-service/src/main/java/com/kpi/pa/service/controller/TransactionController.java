package com.kpi.pa.service.controller;

import com.kpi.pa.service.model.*;
import com.kpi.pa.service.repo.*;
import com.kpi.pa.service.TransactionRequest;
import com.kpi.pa.service.dto.TransactionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;
    private final AccountRepo accountRepo;
    private final CategoryRepo categoryRepo;

    public TransactionController(TransactionRepo transactionRepo, UserRepo userRepo,
                                 AccountRepo accountRepo, CategoryRepo categoryRepo) {
        this.transactionRepo = transactionRepo;
        this.userRepo = userRepo;
        this.accountRepo = accountRepo;
        this.categoryRepo = categoryRepo;
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Користувач не автентифікований.");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            Long userId = Long.valueOf(userDetails.getUsername());
            return userRepo.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Користувача не знайдено."));
        } catch (NumberFormatException e) {
            return userRepo.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Користувача не знайдено."));
        }
    }

    @GetMapping
    public List<TransactionDTO> list(Authentication authentication) {
        User user = getCurrentUser(authentication);
        LocalDateTime start = LocalDateTime.of(1900,1,1,0,0);
        LocalDateTime end = LocalDateTime.now();
        return transactionRepo.findAllByUser_IdAndCreatedAtBetween(user.getId(), start, end)
                .stream()
                .map(TransactionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<TransactionDTO> create(@RequestBody TransactionRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Назва транзакції обов'язкова.");
        }

        Account account = accountRepo.findByIdAndUserId(request.getAccountId(), user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Рахунок не знайдено або не належить користувачеві."));

        String type = request.getType() != null ? request.getType().toUpperCase() : "";
        if (!"INCOME".equals(type) && !"EXPENSE".equals(type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Невірний тип транзакції. Використовуйте INCOME або EXPENSE.");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Сума транзакції повинна бути позитивною.");
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setTitle(request.getTitle());
        transaction.setType(type);
        transaction.setAmount(request.getAmount());
        transaction.setCreatedAt(request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now());

        transaction.setCurrency(request.getCurrency());
        transaction.setDescription(request.getDescription());

        if (request.getCategoryId() != null) {
            Category category = categoryRepo.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Категорія не знайдена."));
            transaction.setCategory(category);
        }

        BigDecimal currentBalance = account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO;
        BigDecimal newBalance = "INCOME".equals(type) ?
                currentBalance.add(transaction.getAmount()) :
                currentBalance.subtract(transaction.getAmount());
        account.setBalance(newBalance);
        accountRepo.save(account);

        Transaction saved = transactionRepo.save(transaction);
        return new ResponseEntity<>(TransactionDTO.fromEntity(saved), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public TransactionDTO get(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Transaction t = transactionRepo.findById(id)
                .filter(tx -> tx.getUser() != null && user.getId().equals(tx.getUser().getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Транзакцію не знайдено або вона не належить користувачеві."));
        return TransactionDTO.fromEntity(t);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void delete(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Transaction transaction = transactionRepo.findById(id)
                .filter(t -> t.getUser() != null && user.getId().equals(t.getUser().getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Транзакцію не знайдено або вона не належить користувачеві."));

        Account account = transaction.getAccount();
        BigDecimal currentBalance = account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO;
        BigDecimal newBalance = "INCOME".equals(transaction.getType()) ?
                currentBalance.subtract(transaction.getAmount()) :
                currentBalance.add(transaction.getAmount());
        account.setBalance(newBalance);
        accountRepo.save(account);

        transactionRepo.deleteById(id);
    }
}
