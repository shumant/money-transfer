# money-transfer

Sample app for RESTful API (including data model and the backing implementation) for money
transfers between accounts.

- Jetty as servlet container
- Jersey as REST framework
- HK2 for Dependency Injection
- Hibernate as ORM
- H2 for in-memory database
- Gradle as build system

Unit-tests are omitted because the project is really tiny.
API workflow is presented in IntegrationTest.java 

To run tests and build jar:
> ./gradlew clean build

After that to run the app as server on port 8080:
> java -jar build/libs/money-transfer-1.0-SNAPSHOT.jar
