# Inventory Management System

A robust Java-based Inventory Management System designed with DAO architecture. This application integrates MySQL for data persistence, Jakarta Mail for email reporting, and features a modern JavaFX user interface.

## Features

*   **User Interface:** Interactive and modern UI built with JavaFX.
*   **Database Integration:** Seamless MySQL connectivity using JDBC and DAO patterns.
*   **Email Reporting:** Automated email alerts and reports powered by Jakarta Mail.
*   **Data Export:** Capability to export inventory data to CSV format.
*   **Testing:** Comprehensive unit tests using JUnit 4 and Mockito.

## Technologies Used

*   **Java 17**
*   **JavaFX 23.0.2**
*   **Maven**
*   **MySQL 8.0+**
*   **Jakarta Mail 2.0.1**
*   **JUnit 4 & Mockito**

## Prerequisites

Ensure you have the following installed:

*   Java Development Kit (JDK) 17 or higher
*   Maven 3.6 or higher
*   MySQL Server

## Setup & Installation

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/Sundhar004/Inventory-Management.git
    cd Inventory-Management
    ```

2.  **Database Configuration:**
    *   Ensure your MySQL server is running.
    *   Create a database (schema details should be verified in the source code).
    *   Update database credentials in the application properties or configuration files if necessary.

3.  **Build the project:**
    ```sh
    mvn clean install
    ```

## Running the Application

To launch the JavaFX application, use the Maven plugin:

```sh
mvn javafx:run
```

Alternatively, if you have built the jar with dependencies:

```sh
java -jar target/Inventory-Management-1.0-SNAPSHOT-jar-with-dependencies.jar
```
