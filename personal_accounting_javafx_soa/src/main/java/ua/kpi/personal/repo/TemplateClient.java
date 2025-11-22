package ua.kpi.personal.repo;

import ua.kpi.personal.model.TransactionTemplate;
import ua.kpi.personal.util.ApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TemplateClient {
    
    private final ObjectMapper mapper = new ObjectMapper(); 
    private static final String API_PATH = "/templates"; 

    
    public TransactionTemplate create(TransactionTemplate template) {
        try {
            String jsonResponse = ApiClient.post(API_PATH, template); 
            
            return mapper.readValue(jsonResponse, TransactionTemplate.class);
        } catch(Exception e){ 
            e.printStackTrace(); 
            return null; 
        }
    }

    
    public List<TransactionTemplate> findByUserId(Long userId) {
        try {
            String jsonResponse = ApiClient.get(API_PATH);
            
            
            List<TransactionTemplate> list = mapper.readValue(jsonResponse, new TypeReference<List<TransactionTemplate>>() {});
            
            return list;
        } catch(Exception e){ 
            e.printStackTrace(); 
            return new ArrayList<>(); 
        }
    }

    
    public boolean delete(Long templateId) {
        String path = API_PATH + "/" + templateId;
        try {
            ApiClient.delete(path);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}