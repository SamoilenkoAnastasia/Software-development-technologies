package ua.kpi.personal.service;

import ua.kpi.personal.model.User;
import ua.kpi.personal.repo.UserDao;

public class AuthService {
    private final UserDao userDao = new UserDao();

    public User login(String username, String password){
        User u = userDao.findByUsername(username);
        if(u==null) return null;
        if(u.getPassword()!=null && u.getPassword().equals(password)) return u;
        return null;
    }

    public User register(String username, String password, String fullName){
        User exists = userDao.findByUsername(username);
        if(exists!=null) return null;
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setFullName(fullName);
        return userDao.create(u);
    }
}
