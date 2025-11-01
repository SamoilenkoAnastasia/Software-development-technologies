# PersonalAccountingClient

JavaFX (JDK 17) Maven project "Особиста бухгалтерія" — minimal demo desktop client.

## How to run

1. Ensure you have JDK 17 installed.
2. Create a MySQL database `personal_finance` and tables `users`, `transactions` matching expected columns (see DbConfig).
3. Build and run with Maven:
   ```
   mvn clean javafx:run
   ```
4. Default DB connection: jdbc:mysql://127.0.0.1:3306/personal_finance
   User: root
   Password: N_030306-a

This project is a minimal scaffold for demonstration and educational purposes.
