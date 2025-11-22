package com.kpi.pa.service.controller;

import com.kpi.pa.service.model.Account;
import com.kpi.pa.service.model.User;
import com.kpi.pa.service.repo.AccountRepo;
import com.kpi.pa.service.repo.UserRepo;
import com.kpi.pa.service.AccountRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountRepo accountRepo;
    private final UserRepo userRepo;

    @Autowired
    public AccountController(AccountRepo accountRepo, UserRepo userRepo) {
        this.accountRepo = accountRepo;
        this.userRepo = userRepo;
    }

    
    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Користувач не автентифікований.");
        }
        
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        try {
            Long userId = Long.valueOf(userDetails.getUsername());
            return userRepo.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Користувача не знайдено."));
        } catch (NumberFormatException e) {
             
             return userRepo.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Користувача не знайдено."));
        }
    }

    @GetMapping
    public List<Account> getAllUserAccounts(Authentication authentication) {
        
        User user = getCurrentUser(authentication);
        return accountRepo.findByUserId(user.getId());
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(
            @RequestBody AccountRequest request,
            Authentication authentication
    ) {
        
        User user = getCurrentUser(authentication);

        Account newAccount = new Account();
        newAccount.setUser(user);
        newAccount.setName(request.getName());
        newAccount.setBalance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO);

        if (request.getCurrency() != null && !request.getCurrency().isEmpty()) {
            newAccount.setCurrency(request.getCurrency());
        }

        return new ResponseEntity<>(accountRepo.save(newAccount), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(
            @PathVariable Long id,
            @RequestBody AccountRequest request,
            Authentication authentication
    ) {
        User user = getCurrentUser(authentication);

        Account account = accountRepo.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Рахунок не знайдено або не належить користувачу."));

        if (request.getName() != null) account.setName(request.getName());
        if (request.getInitialBalance() != null) account.setBalance(request.getInitialBalance());
        if (request.getCurrency() != null && !request.getCurrency().isEmpty()) account.setCurrency(request.getCurrency());

        return ResponseEntity.ok(accountRepo.save(account));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = getCurrentUser(authentication);

        Account account = accountRepo.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Рахунок не знайдено або не належить користувачу."));

        accountRepo.delete(account);
        return ResponseEntity.noContent().build();
    }
}