package ua.kpi.personal.repo;

import ua.kpi.personal.model.Category;
import ua.kpi.personal.util.ApiClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;
import java.util.List;


public class CategoryClient {

    
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())                         
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  

    private static final String API_PATH = "/categories";

    
    public List<Category> findByUserId(Long userId) {
        try {
            String jsonResponse = ApiClient.get(API_PATH);

            return mapper.readValue(jsonResponse, new TypeReference<List<Category>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    
    public Category create(Category category) {
        try {
            String jsonResponse = ApiClient.post(API_PATH, category);
            return mapper.readValue(jsonResponse, Category.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   
    public boolean update(Category category) {
        String path = API_PATH + "/" + category.getId();
        try {
            ApiClient.put(path, category);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean delete(Long id) {
        String path = API_PATH + "/" + id;
        try {
            ApiClient.delete(path);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting category: " + e.getMessage());
            return false;
        }
    }

  
    public Category findById(Long id) {
        String path = API_PATH + "/" + id;
        try {
            String jsonResponse = ApiClient.get(path);
            return mapper.readValue(jsonResponse, Category.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
