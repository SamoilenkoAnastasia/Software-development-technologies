package ua.kpi.personal.state;

import ua.kpi.personal.model.User;

public class LoggedInState implements SessionState {
    
    @Override
    public void handleLogin(ApplicationSession session, User user) {
        // *** ВИПРАВЛЕННЯ ДЛЯ КНОПКИ "НАЗАД" ***
        // Якщо ми вже ввійшли і викликаємо login, ми просто перезавантажуємо головну view.
        // Зауваження: Ми не викликаємо changeState(this), щоб не створювати новий об'єкт
        // на кожному кроці, але нам потрібно ініціювати loadView().
        
        // Найпростіше рішення: викликати changeState(this) для перезавантаження loadView().
        // Оскільки ми вже LoggedIn, User встановлений.
        session.changeState(this); 
    }

    @Override
    public void handleLogout(ApplicationSession session) {
        session.setCurrentUser(null);
        session.changeState(new LoggedOutState());
    }

    @Override
    public String getFxmlView() {
        return "/fxml/main.fxml"; 
    }
}