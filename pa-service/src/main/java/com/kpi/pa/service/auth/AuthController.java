package com.kpi.pa.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.kpi.pa.service.model.User;
import com.kpi.pa.service.repo.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kpi.pa.service.auth.RegistrationRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth") 
public class AuthController {
    
    private static final String SECRET = "replace-with-strong-secret"; 
    
    private final UserRepo userRepo;
    
    public AuthController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        User user = userOpt.get();
        
        if (user.getPassword() == null || !user.getPassword().equals(password)) { 
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        
        String token = JWT.create()
                .withSubject(user.getId().toString())
                .withIssuer("pa-service")
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600_000)) 
                .sign(Algorithm.HMAC256(SECRET));
        
        Map<String, Object> res = new HashMap<>();
        res.put("token", token);
        res.put("user", user); 
        
        return ResponseEntity.ok(res);
    }
    
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegistrationRequest request) {
        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(409).body(null);
        }
        
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(request.getPassword());
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail() != null ? request.getEmail() : ""); 
        
        User createdUser = userRepo.save(newUser);
        return ResponseEntity.ok(createdUser);
    }
}
