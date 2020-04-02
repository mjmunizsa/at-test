# AT-TEST

## Introduction

This application implements a microservice that simulates the handling and control of banking transactions.




## Requirements

- Apache Maven 3 - https://maven.apache.org/download.cgi
- JDK 1.8 - http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html


## Compile and Test

In the directory where the project has been downloaded, execute the following maven command:
```sh
mvn clean install -DskipTests=true
```

To pass the unit and integration tests, execute the following maven command:
```sh
mvn test
```

## Deploy
Once the project is compiled, it is launched like any spring boot application:
```sh
mvn spring-boot:run
```
The microservice has been implemented with a REST API.
To verify that the microservice is correctly deployed, the URL of the API documentation can be accessed:
http://localhost:8080/swagger-ui.html

## Functional requirements

Because some requirements have been left ambiguous, I have had to make the following assumptions:
### Create transaction

- *"It is IMPORTANT to note that a transaction that leaves the total account balance bellow 0 is not allowed"*:  
To simulate account balance control, I have assumed that the first transaction that for a iban_account, creates an account with a 0 balance.  
This should really be delegated to another microservice in charge of account management.

- *Non-mandatory fields*:   
I have considered that if the date of the transaction is null, the system stores it with the current date and time.  
In the same way, if the fee is zero, it is stored as zero

### Search transactions
I have implemented the search using a new endpoint that allows me to return paged results in addition to setting the search criteria.  
Some examples:
- Order by amount desc, 20 page size: http://localhost:8080/api/transaction?account_iban=ES9820385778983000760236&sort=amount,desc&size=20
- Order by fee asc, 10 page size: http://localhost:8080/api/transaction?account_iban=ES9820385778983000760236&sort=fee,asc&size=10

### Transaction status
In the requirements it does not say anything about what the behavior is when the channel is not specified.
In this sense I have assumed that the system will behave in the same way as if the CLIENT channel were specified

## TODO
- Finish documenting the API in swagger.  
- Securing the API with Spring Security.
- Mocking Account microservice