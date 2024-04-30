package scenebuilder;

import backend.Product;
import backend.User;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;


public class SceneBuilder extends Application {

        private TextArea outputArea;

        @Override
        public void start(Stage primaryStage) {
            primaryStage.setTitle("Inventory App");

            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10, 10, 10, 10));
            grid.setVgap(8);
            grid.setHgap(10);

            outputArea = new TextArea();
            outputArea.setEditable(false);
            Button createUserButton = new Button("Create User");
            Button loginButton = new Button("Login");
            Button searchUsersButton = new Button("Search Users");
            Button createProductButton = new Button("Create Product");
            Button searchProductsButton = new Button("Search Products");

            // Add controls to the grid
            GridPane.setConstraints(createUserButton, 0, 0);
            GridPane.setConstraints(loginButton, 1, 0);
            GridPane.setConstraints(searchUsersButton, 2, 0);
            GridPane.setConstraints(createProductButton, 0, 1);
            GridPane.setConstraints(searchProductsButton, 1, 1);
            GridPane.setConstraints(outputArea, 0, 2, 3, 1);

            grid.getChildren().addAll(createUserButton, loginButton, searchUsersButton, createProductButton, searchProductsButton, outputArea);

            // Set actions for buttons
            createUserButton.setOnAction(e -> createUserDialog());
            loginButton.setOnAction(e -> loginDialog());
            searchUsersButton.setOnAction(e -> searchUsersDialog());
            createProductButton.setOnAction(e -> createProductDialog());
            searchProductsButton.setOnAction(e -> searchProductsDialog());

            // Create scene and set it on the stage
            Scene scene = new Scene(grid, 600, 400);
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        private void createUserDialog() {
            Dialog<UserInput> dialog = new Dialog<>();
            dialog.setTitle("Create User");
            dialog.setHeaderText("Enter user details");

            ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField usernameField = new TextField();
            usernameField.setPromptText("Username");
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Password");
            TextField roleField = new TextField();
            roleField.setPromptText("Role");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(usernameField, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(passwordField, 1, 1);
            grid.add(new Label("Role:"), 0, 2);
            grid.add(roleField, 1, 2);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == createButtonType) {
                    return new UserInput(usernameField.getText(), passwordField.getText(), roleField.getText());
                }
                return null;
            });

            dialog.showAndWait().ifPresent(userInput -> {
                User.createUser(userInput.getUsername(), userInput.getPassword(), userInput.getRole());
                outputArea.appendText("User created: " + userInput.getUsername() + "\n");
            });
        }

        private void loginDialog() {
            Dialog<UserInput> dialog = new Dialog<>();
            dialog.setTitle("Login");
            dialog.setHeaderText("Enter login details");

            ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField usernameField = new TextField();
            usernameField.setPromptText("Username");
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Password");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(usernameField, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(passwordField, 1, 1);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    return new UserInput(usernameField.getText(), passwordField.getText(), null);
                }
                return null;
            });

            dialog.showAndWait().ifPresent(userInput -> {
                User curUser = User.login(userInput.getUsername(), userInput.getPassword());
                if (curUser != null) {
                    outputArea.appendText("Login successful. Role: " + curUser.getRole() + "\n");
                } else {
                    outputArea.appendText("Invalid username or password.\n");
                }
            });
        }

    private void searchUsersDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Search Users");
        dialog.setHeaderText("Enter search criteria");

        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField roleField = new TextField();
        roleField.setPromptText("Role");

        grid.add(new Label("Role:"), 0, 0);
        grid.add(roleField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == searchButtonType) {
                return roleField.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(role -> {
            Dictionary<String, String> attributes = new Hashtable<>();
            attributes.put("Role", role);
            ArrayList<User> users = User.search(attributes);
            if (users != null && !users.isEmpty()) {
                outputArea.appendText("Users found:\n");
                for (User user : users) {
                    outputArea.appendText(user.toString() + "\n");
                }
            } else {
                outputArea.appendText("No users found with role: " + role + "\n");
            }
        });
    }

    private void createProductDialog() {
        Dialog<ProductInput> dialog = new Dialog<>();
        dialog.setTitle("Create Product");
        dialog.setHeaderText("Enter product details");

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField productIDField = new TextField();
        productIDField.setPromptText("Product ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField stockQuantityField = new TextField();
        stockQuantityField.setPromptText("Stock Quantity");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        grid.add(new Label("Product ID:"), 0, 0);
        grid.add(productIDField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Stock Quantity:"), 0, 3);
        grid.add(stockQuantityField, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryField, 1, 4);
        grid.add(new Label("Description:"), 0, 5);
        grid.add(descriptionField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new ProductInput(
                        productIDField.getText(),
                        nameField.getText(),
                        Double.parseDouble(priceField.getText()),
                        Integer.parseInt(stockQuantityField.getText()),
                        categoryField.getText(),
                        descriptionField.getText()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(productInput -> {
            Product existingProduct = Product.search(productInput.getProductID());
            if (existingProduct != null) {
                outputArea.appendText("Product already exists: " + existingProduct.getProductID() + "\n");
            } else {
                Product newProduct = Product.createProduct(
                        productInput.getProductID(),
                        productInput.getName(),
                        productInput.getPrice(),
                        productInput.getStockQuantity(),
                        productInput.getCategory(),
                        productInput.getDescription()
                );
                if (newProduct != null) {
                    outputArea.appendText("Product created: " + newProduct.getProductID() + "\n");
                } else {
                    outputArea.appendText("Failed to create product.\n");
                }
            }
        });
    }

    private void searchProductsDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Search Products");
        dialog.setHeaderText("Enter search criteria");

        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        grid.add(new Label("Category:"), 0, 0);
        grid.add(categoryField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == searchButtonType) {
                return categoryField.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(category -> {
            Dictionary<String, String> attributes = new Hashtable<>();
            attributes.put("Category", category);
            ArrayList<Product> products = Product.search(attributes);
            if (products != null && !products.isEmpty()) {
                outputArea.appendText("Products found:\n");
                for (Product product : products) {
                    outputArea.appendText(product.toString() + "\n");
                }
            } else {
                outputArea.appendText("No products found in category: " + category + "\n");
            }
        });
    }

    private static class ProductInput {
        private String productID;
        private String name;
        private double price;
        private int stockQuantity;
        private String category;
        private String description;

        public ProductInput(String productID, String name, double price, int stockQuantity, String category, String description) {
            this.productID = productID;
            this.name = name;
            this.price = price;
            this.stockQuantity = stockQuantity;
            this.category = category;
            this.description = description;
        }

        public String getProductID() {
            return productID;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getStockQuantity() {
            return stockQuantity;
        }

        public String getCategory() {
            return category;
        }

        public String getDescription() {
            return description;
        }
    }


        private static class UserInput {
            private String username;
            private String password;
            private String role;

            public UserInput(String username, String password, String role) {
                this.username = username;
                this.password = password;
                this.role = role;
            }

            public String getUsername() {
                return username;
            }

            public String getPassword() {
                return password;
            }

            public String getRole() {
                return role;
            }
        }

        public static void main(String[] args) {
            launch(args);
        }


    }

