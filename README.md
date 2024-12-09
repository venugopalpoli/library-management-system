# Getting Started

### Reference Documentation
# Library Management System

LMS in short is a system to handle Book Operations

* addBook
* removeBook
* findBookByISBN
* findBooksByAuthor
* borrowBook
* returnBook

## Software Used to build this system

* Java 17
* Spring Boot 3.3.4
* Lombok 1.18.20
* JsonWebToken 0.11.15
* Spring Security 6.2.3
* Junit 5

## Usage

```lms
# mvn clean install

cleans, builds the project and creates a lms-0.0.1-SNAPSHOT.jar

# mvn test

runs unit tests

# mvn verify

runs integration tests

# mvn surefire-report:report

creates a reports folder in target with surefire.html 
to view test reports in any browser 
with stats of unit and integration tests.
```
## API Endpoints
```endpoints
create jwt token (POST) - http://localhost:8080/login
add book (POST)         - http://localhost:8080/services/library/books
remove book (DELETE)    - http://localhost:8080/services/library/books/remove/{ISBN}
findBookByISBN (GET)    - http://localhost:8080/services/library/books/book/{ISBN}
findBooksByAuthor (GET) - http://localhost:8080/services/library/books/{author}
borrowBook (PATCH)       - http://localhost:8080/services/library/books/borrow//{ISBN}
returnBook (PATCH)       - http://localhost:8080/services/library/books/return/{ISBN}
