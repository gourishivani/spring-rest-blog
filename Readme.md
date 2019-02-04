# spring-rest-blog

A simple Restful Blogging App powered by [Spring Boot](http://projects.spring.io/spring-boot/) Hypermedia-Driven RESTful Web Service


## Requirements

For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)
- [Mysql Server 5.7.x](https://dev.mysql.com/downloads/mysql/5.7.html#downloads)

## Running the application locally

1. Install Mysql locally based on the instructions here https://dev.mysql.com/downloads/mysql/5.7.html#downloads
2. Create a Database and user by following the instructions here: `management.sql`
3. Tables need not be created as I have the ddl-auto set to create. When the application starts up, If you want to disable this, the following property can be changed in application.properties file
`spring.jpa.hibernate.ddl-auto=none`


There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `com.blogosphere.blog.SpringRestBlogApplication` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn clean spring-boot:run
```
or

```shell
./mvnw clean spring-boot:run
```

Alternatively using your installed maven version type this:

```shell
$ mvn clean spring-boot:run
```

When the app starts, you can run:
$ curl -v localhost:8080/users


## Test the service
Very basic health check is in the link below. This is what comes out of the box when actuator is enabled. So it does not ping any dependent health services.
  
`curl -X GET   http://localhost:8080/actuator/health`

Swagger is enabled for this project and I have tried to annotate the API calls to the extent I could. Swagger can be accessed via:
`http://localhost:8080/swagger-ui.html#/`
A user need not be logged in to access swagger. Please take a look at the swagger documentation to see more details. 


## Project Structure
Source code can be found under `src/main/java`. There are packages for Controller, Service and DAO. DTOs are used to de-serialize/serialize data from Client to Rest Service. Entities are located under models package. Security related files are under jwt packages. There is also a package called `knownobjects` that is shared between source and tests. This is kept in source so that data can be easily loaded during application startup.
![PackageStructure](screenshots/Backend-PackageStructure.png?raw=true "PackageStructure")


## Assumptions
* DB access should be done in a configurable way. Right now, the credentials are just hard coded based on the user created by code during startup. In reality, the credentials will be specific to a certain environment and they could be loaded from Secret Server.
* I am assuming that UI is run on `http://localhost:4200` (as per the instructions in UI repo). 


## Details
#### Security:
I am not using session based authentication with the intent of being Restful & stateless. JWT tokens are used instead to authenticate a user. 

#### Pre configured Data:
All the entities are loaded when the application starts via LoadDatabase.java. 
The following user credentials should work as a result:
johndoe@example.com/test123
tester@example.com/tester

The UI pre-populates first user's credentials in the login form. This is only meant for ease of testing. This shouldn't be the case in production. If you would like to disable this feature, comment out @Bean declaration in `LoadDatabase` file

#### Actuator:
Actuator is enabled for this project. Authenticated user should have access to all the end points. This is not a good thing to do for apps deployed to production for security reasons. Instead, a role based access should be considered. 

#### Improved Traceability through MDC
There are not many log statements, however, there is a MDC filter enabled that will provide unique request ID for each request. This can be super helpful to trace calls/ issues in production systems.

#### Tests
Integration Tests are done using Spring Boot Test Harness. There are no unit tests because of time constraints. Also, I am heavily leaning on Spring for JPA, security, hateoas and rest. So it might be much more meaningful to have integration tests for this simple web app. To run Integration tests, please disable `LoadDatabase` bean declaration. Otherwise, tests will fail as I expect the database to be clean.

#### Timestamps
The application stores timestamps in UTC. This is to facilitate a clustered application server setup where the server could reside in different time zones.

#### DTO
I am using DTOs to explicitly define Resource representation. This helps to separate resource representation concerns of REST from transactional concerns of JPA. ModelMapper is used to ease this conversion.


## Improvements

1. All the issues can be found in this link: [Issue-Links](https://github.com/gourishivani/spring-rest-blog/issues) file.
2. I have not enabled pagination. Incuding this would be trivial with Spring.
3. Externalized Configuration management is not implemented. (prod,dev, local etc)
4. Dependency tree could be further trimmed down. Due to time constraints, I have let the defaults be.
5. AuditingSupport: Go get things running quickly, I added auditing fields like created, lastModified to the BaseEntity class. However, introducing support for Auditable interface might have been a good alternative for production code
6. Internationalization support could be added


References:
1. MDC #https://github.com/Verdoso/GreenSummer/blob/master/summer-demo/src/main/resources/log4j2.xml
2. JWT: 
* https://g00glen00b.be/spring-boot-jwt/
* https://auth0.com/blog/implementing-jwt-authentication-on-spring-boot/
* Udemy Courses
3. https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application
4. https://stackoverflow.com/questions/17874688/what-is-a-good-strategy-for-converting-jpa-entities-into-restful-resources
5. https://www.baeldung.com/exception-handling-for-rest-with-spring
6. https://stackoverflow.com/questions/2109476/how-to-handle-dataintegrityviolationexception-in-spring/42422568#42422568
7. https://spring.io/guides/gs/accessing-data-mysql/




