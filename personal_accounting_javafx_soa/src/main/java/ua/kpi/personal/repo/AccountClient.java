package ua.kpi.personal.repo;

import ua.kpi.personal.model.Account;
import ua.kpi.personal.util.ApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AccountClient {
    
    private final ObjectMapper mapper = new ObjectMapper(); 
    
    private static final String API_PATH = "/accounts"; 

    
    public List<Account> findByUserId(Long userId){ 
        
        try {
           
            String jsonResponse = ApiClient.get(API_PATH);
            
            
            List<Account> list = mapper.readValue(jsonResponse, new TypeReference<List<Account>>() {});
            
            return list;
        } catch(Exception e){ 
            e.printStackTrace(); 
            return new ArrayList<>(); 
        }
    }
    
    
    public Account findById(Long id, Long userId){
        String path = API_PATH + "/" + id;
        
        try {
            String jsonResponse = ApiClient.get(path);
            return mapper.readValue(jsonResponse, Account.class);
        } catch(Exception e){ 
            e.printStackTrace(); 
            return null;
        }
    }
    
    
    public Account update(Account account){
        String path = API_PATH + "/" + account.getId();
        try {
            String jsonResponse = ApiClient.put(path, account);
            return mapper.readValue(jsonResponse, Account.class);
        } catch(Exception e){ 
            e.printStackTrace(); 
            return null; 
        }
    }
    
    
    public Account create(Account account){
        try {
            String jsonResponse = ApiClient.post(API_PATH, account); 
            
            return mapper.readValue(jsonResponse, Account.class);
        } catch(Exception e){ 
            e.printStackTrace(); 
            return null; 
        }
    }
}