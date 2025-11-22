package ua.kpi.personal.service;

import ua.kpi.personal.model.User;
import ua.kpi.personal.repo.UserClient;
import ua.kpi.personal.model.RegistrationRequest;
import ua.kpi.personal.util.ApiClient; 

public class AuthService {
    
    private final UserClient userClient = new UserClient();
    private User currentUser;
    
    public User getCurrentUser() {
        return currentUser;
    }

    public boolean login(String username, String password) {
 
        User user = userClient.login(username, password);
        
        if (user != null) {
            this.currentUser = user;
            return true;
        }
        this.currentUser = null;
        return false;
    }
    
  
    public User register(String username, String password, String fullName) {
      RegistrationRequest request = new RegistrationRequest(
          username,
          password,
          fullName,
          "" 
      );

      return userClient.register(request);
    }
    
   
    public void logout() {
        this.currentUser = null;
        ApiClient.clearAuthToken(); 
    }
}