# Inventory Management System Frontend

This README provides information about the frontend module of the Inventory Management System, including setup instructions, usage details, and an overview of the project's structure.

## Project Structure

The frontend module follows the Maven project structure and includes the following key directories and files:

- **src/main/java/org/example**: Contains the Java source files for the application.
  - **MainApp.java**: The entry point for the JavaFX application.
  - **controller/**: Contains controllers for handling user interactions.
    - **AdminController.java**: Logic for the Admin Dashboard.
    - **LoginController.java**: Manages login functionality.
    - **RegisterController.java**: Handles user registration.
    - **UserController.java**: Manages the User Dashboard.
  - **viewmodel/**: Contains the ViewModel for managing data and business logic.
    - **InventoryViewModel.java**: Facilitates communication between views and the backend.
  - **util/**: Utility classes for the application.
    - **FXLoader.java**: Loads FXML files and manages scene transitions.

- **src/main/resources**: Contains resources for the application.
  - **fxml/**: FXML files defining the layout for different views.
    - **LoginView.fxml**: Layout for the login view.
    - **RegisterView.fxml**: Layout for the registration view.
    - **AdminDashboard.fxml**: Layout for the admin dashboard.
    - **UserDashboard.fxml**: Layout for the user dashboard.
  - **css/**: CSS files for styling the application.
    - **styles.css**: Contains styles for the JavaFX application.

- **src/test/java/org/example/controller**: Contains unit tests for the controllers.
  - **LoginControllerTest.java**: Tests for the LoginController.

## Setup Instructions

1. **Clone the Repository**: Clone the Inventory Management System repository to your local machine.

2. **Navigate to the Frontend Directory**: Change your working directory to the `frontend` folder.

3. **Build the Project**: Use Maven to build the project by running the following command:
   ```
   mvn clean install
   ```

4. **Run the Application**: After building, you can run the application using the following command:
   ```
   mvn javafx:run
   ```

## Usage

- **Login**: Users can log in using the Login view. Upon successful authentication, they will be redirected to their respective dashboards.
- **Registration**: New users can register through the Registration view, which validates input and interacts with the backend to create a new user.
- **Admin Dashboard**: Admin users can manage products and users through the Admin Dashboard.
- **User Dashboard**: Regular users can view their information and manage their profiles through the User Dashboard.

## Suggested Alterations

1. **Backend Integration**: Ensure that the frontend controllers interact with the backend services for user authentication and product management.
2. **ViewModel Pattern**: Implement the ViewModel pattern to separate UI logic from business logic.
3. **Error Handling**: Add error handling in controllers to manage exceptions and provide user feedback.
4. **CSS Styling**: Enhance the UI with improved styles in `styles.css`.
5. **Testing**: Expand test coverage for frontend controllers and ViewModel.
6. **Documentation**: Update README files to reflect changes made during integration.

## License

This project is licensed under the MIT License. See the LICENSE file for more details.