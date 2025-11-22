PA Service module
=================
This module is a Spring Boot REST service that exposes CRUD API for Transactions.
It uses H2 in-memory DB and a simple /auth/login endpoint that returns a JWT token.
Instructions to build/run:
- Install JDK 17 and Maven.
- From pa-service directory run: mvn spring-boot:run
- Service will start on port 8443 (note: for demo it's HTTP on 8443 unless HTTPS configured).
