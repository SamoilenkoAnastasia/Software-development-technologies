package ua.kpi.personal.repo;

import ua.kpi.personal.model.User;
import ua.kpi.personal.model.RegistrationRequest;
import ua.kpi.personal.util.ApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Map;


public class UserClient {

    private final ObjectMapper mapper = ApiClient.MAPPER;
    private static final String AUTH_PATH = "/auth";

    public User login(String username, String password) {
        
        Map<String, String> loginRequest = Map.of(
            "username", username,
            "password", password
        );

        try {
            String jsonResponse = ApiClient.post(AUTH_PATH + "/login", loginRequest);
            
            
            Map<String, Object> responseMap = mapper.readValue(jsonResponse, new TypeReference<Map<String, Object>>() {});
           
            String token = (String) responseMap.get("token"); 
      
            if (token != null) {
                ApiClient.setAuthToken(token);
            }
    
            Object userObject = responseMap.get("user");
            if (userObject != null) {
                return mapper.convertValue(userObject, User.class);
            }
            return null;

        } catch(Exception e){
  
            System.err.println("Login failed: " + e.getMessage());
            ApiClient.clearAuthToken(); 
            return null;
        }
    }

   
    public User register(RegistrationRequest request){
        try {
            String jsonResponse = ApiClient.post(AUTH_PATH + "/register", request);
            return mapper.readValue(jsonResponse, User.class);
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}