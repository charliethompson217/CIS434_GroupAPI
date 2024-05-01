package scenebuilder;
import backend.Product;
import backend.User;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class SceneBuilder extends Application {

    private BorderPane root;
    private static User currentUser;
    private static String database;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Inventory App");
        showLoginScreen(primaryStage);
    }

    private void showLoginScreen(Stage primaryStage) {
        primaryStage.setTitle("Inventory App: Login");
        GridPane loginPane = new GridPane();
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setPadding(new Insets(20));
        loginPane.setHgap(10);
        loginPane.setVgap(10);
        TextField databaseFeild = new TextField();
        databaseFeild.setPromptText("Database Name");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button createAccountButton = new Button("Create Account");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String databaseName = databaseFeild.getText();
            currentUser = User.login(databaseName, username, password);
            if (currentUser != null) {
                database = databaseName;
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + currentUser.getUsername() + "!");
                showMainInterface(primaryStage);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        });

        createAccountButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String databaseName = databaseFeild.getText();
            if(!User.isNewDatabase(databaseName)) {
                currentUser = User.createUser(databaseName, username, password, "Admin");
                if (currentUser != null) {
                    database = databaseName;
                    showAlert(Alert.AlertType.INFORMATION, "Account Creation Successful", "Welcome, " + currentUser.getUsername() + "!");
                    showMainInterface(primaryStage);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Account Creation Failed", "Username");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Account Creation Failed", "Database Already Exists");
            }
        });

        loginPane.add(new Label("Database:"), 0, 0);
        loginPane.add(databaseFeild, 1, 0);

        loginPane.add(new Label("Username:"), 0, 1);
        loginPane.add(usernameField, 1, 1);

        loginPane.add(new Label("Password:"), 0, 2);
        loginPane.add(passwordField, 1, 2);

        HBox hbox = new HBox();
        hbox.getChildren().addAll(loginButton, createAccountButton);
        hbox.setSpacing(10.0);

        loginPane.add(hbox, 1, 3);

        Scene scene = new Scene(loginPane, 300, 200);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showMainInterface(Stage primaryStage) {
        primaryStage.setTitle("Inventory App: "+ database);
        root = new BorderPane();

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab searchProductsTab = new Tab("Search Products");
        Tab addProductTab = new Tab("Add Product");
        Tab searchUserTab = new Tab("Search Users");
        Tab addUserTab = new Tab("Add User");

        tabPane.getTabs().addAll(searchProductsTab, addProductTab, searchUserTab);

        // Conditionally add the Add User tab if the user is an Admin
        if (currentUser != null && "Admin".equals(currentUser.getRole())) {
            tabPane.getTabs().add(addUserTab);
        }



        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab.getContent() == null) {
                if (newTab == searchProductsTab) {
                    root.setCenter(setupSearchProductsUI());
                } else if (newTab == addProductTab) {
                    root.setCenter(setupAddProductUI());
                } else if (newTab == searchUserTab) {
                    root.setCenter(setupSearchUsersUI());
                } else if (newTab == addUserTab) {
                    root.setCenter(setupAddUserUI());
                }
            } else {
                root.setCenter(newTab.getContent());
            }
        });
        HBox hbox = new HBox();
        Button logOut = new Button("Log Out");
        logOut.setOnAction(e -> {
            currentUser = null;
            database = null;
            showLoginScreen(primaryStage);
        });
        hbox.getChildren().addAll(tabPane, logOut);
        hbox.setId("tophbox");
        root.setTop(hbox);
        // Initialize with first tab content
        root.setCenter(setupSearchProductsUI());

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox setupSearchProductsUI() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_LEFT);

        // Search attribute selector
        ComboBox<String> attributeSelector = new ComboBox<>();
        attributeSelector.setItems(FXCollections.observableArrayList(
                "Product ID", "Name", "Price", "Stock Quantity", "Category", "Description"
        ));
        attributeSelector.setPromptText("Select attribute");

        // Search field for general search
        TextField searchField = new TextField();
        searchField.setPromptText("Enter search term");

        // Additional fields for range search
        TextField minField = new TextField();
        minField.setPromptText("Min value");
        TextField maxField = new TextField();
        maxField.setPromptText("Max value");

        // Hiding range fields initially
        minField.setVisible(false);
        maxField.setVisible(false);

        // Listen for changes in attribute selection
        attributeSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isRangeSearch = "Price".equals(newVal) || "Stock Quantity".equals(newVal) || "Product ID".equals(newVal);
            minField.setVisible(isRangeSearch);
            maxField.setVisible(isRangeSearch);
            searchField.setVisible(!isRangeSearch);
        });

        // Search button
        Button searchButton = new Button("Search");

        // Table View for displaying products
        TableView<Product> tableView = new TableView<>();
        setupProductTableView(tableView);  // This method sets up the columns for the table
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Search button action
        searchButton.setOnAction(event -> {
            String selectedAttribute = attributeSelector.getValue();
            if ("Price".equals(selectedAttribute) || "Stock Quantity".equals(selectedAttribute) || "Product ID".equals(selectedAttribute)) {
                // Handle range search for price or stock
                Double min = minField.getText().isEmpty() ? 0 : Double.valueOf(minField.getText());
                Double max = maxField.getText().isEmpty() ? Double.MAX_VALUE : Double.valueOf(maxField.getText());
                ArrayList<Product> products = Product.search(database, selectedAttribute, min, max);
                ObservableList<Product> productData = FXCollections.observableArrayList(products);
                tableView.setItems(productData);
            } else {
                // General search
                String searchText = searchField.getText();
                Dictionary<String, String> attributes = new Hashtable<>();
                if(!searchText.isEmpty())
                    attributes.put(selectedAttribute, searchText);
                ArrayList<Product> products = Product.search(database, attributes);
                ObservableList<Product> productData = FXCollections.observableArrayList(products);
                tableView.setItems(productData);
            }
        });

        vbox.getChildren().addAll(new Label("Search by:"), attributeSelector, searchField, minField, maxField, searchButton, tableView);
        return vbox;
    }


    private void setupProductTableView(TableView<Product> tableView) {
        TableColumn<Product, String> idColumn = new TableColumn<>("Product ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));

        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Stock Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        TableColumn<Product, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Product, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        tableView.getColumns().addAll(idColumn, nameColumn, priceColumn, quantityColumn, categoryColumn, descriptionColumn);

        tableView.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(event -> editProduct(row.getItem(), tableView));

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> {
                Product selectedProduct = row.getItem();
                selectedProduct.delete();
                tableView.getItems().remove(selectedProduct);
            });

            contextMenu.getItems().addAll(editItem, deleteItem);

            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });
    }

    private void editProduct(Product product, TableView<Product> tableView) {
        // Open a dialog to edit the product, or update a section of the UI to show edit fields
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        dialog.setHeaderText("Edit Product Details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(product.getName());
        TextField priceField = new TextField(String.valueOf(product.getPrice()));
        TextField quantityField = new TextField(String.valueOf(product.getStockQuantity()));
        TextField categoryField = new TextField(product.getCategory());
        TextField descriptionField = new TextField(product.getDescription());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Price:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);
        grid.add(new Label("Category:"), 0, 3);
        grid.add(categoryField, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descriptionField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                product.setName(nameField.getText());
                product.setPrice(Double.parseDouble(priceField.getText()));
                product.setStock(Integer.parseInt(quantityField.getText()));
                product.setCategory(categoryField.getText());
                product.setDescription(descriptionField.getText());
                tableView.refresh();
            }
            return null;
        });

        dialog.showAndWait();
    }



    private VBox setupSearchUsersUI() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_LEFT);

        // Search attribute selector
        ComboBox<String> attributeSelector = new ComboBox<>();
        attributeSelector.setItems(FXCollections.observableArrayList(
                "Username", "Role"
        ));
        attributeSelector.setPromptText("Select attribute");

        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Enter search term");

        // Search button
        Button searchButton = new Button("Search");

        // Table View for displaying users
        TableView<User> tableView = new TableView<>();
        setupUserTableView(tableView);

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Search button action
        searchButton.setOnAction(event -> {
            String selectedAttribute = attributeSelector.getValue();
            String searchText = searchField.getText();

            Dictionary<String, String> attributes = new Hashtable<>();
            if(!searchText.isEmpty())
                attributes.put(selectedAttribute, searchText);

            ArrayList<User> users = User.search(database, attributes);
            ObservableList<User> userData = FXCollections.observableArrayList(users);
            tableView.setItems(userData);
        });

        vbox.getChildren().addAll(new Label("Search by:"), attributeSelector, searchField, searchButton, tableView);
        return vbox;
    }

    private void setupUserTableView(TableView<User> tableView) {
        TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        tableView.getColumns().addAll(usernameColumn, roleColumn);

        tableView.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(event -> editUser(row.getItem(), tableView));

            contextMenu.getItems().add(editItem);

            // Set context menu on the row, but use a binding to make it only show for non-empty rows
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });
    }

    private void editUser(User user, TableView<User> tableView) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit User Details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField usernameField = new TextField(user.getUsername());
        PasswordField passwordField = new PasswordField();  // Consider how to handle password edits
        passwordField.setPromptText("(leave blank to keep current)");
        TextField roleField = new TextField(user.getRole());

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Role:"), 0, 2);
        grid.add(roleField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                user.setUsername(usernameField.getText());
                if (!passwordField.getText().isEmpty()) {
                    user.setPassword(passwordField.getText());
                }
                user.setRole(roleField.getText());
                tableView.refresh();  // Refresh to display updated info
            }
            return null;
        });

        dialog.showAndWait();
    }



    private VBox setupAddUserUI() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_LEFT);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        TextField roleField = new TextField();
        roleField.setPromptText("Role");
        Button addButton = new Button("Add User");

        addButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = roleField.getText();

            User newUser = User.createUser(database, username, password, role);
            if (newUser != null) {
                showAlert(Alert.AlertType.INFORMATION, "User Added", "The user was successfully added!");
                usernameField.clear();
                passwordField.clear();
                roleField.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Username already exists!");
            }
        });

        vbox.getChildren().addAll(
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                new Label("Role:"), roleField,
                addButton
        );
        return vbox;
    }


    private VBox setupAddProductUI() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_LEFT);

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
        Button addButton = new Button("Add Product");

        addButton.setOnAction(event -> {
            // Try to parse price and stock quantity. Handle potential NumberFormatException if invalid.
            try {
                double price = Double.parseDouble(priceField.getText());
                int stockQuantity = Integer.parseInt(stockQuantityField.getText());
                Product newProduct = Product.createProduct(
                        database,
                        String.valueOf(System.currentTimeMillis()),
                        nameField.getText(),
                        price,
                        stockQuantity,
                        categoryField.getText(),
                        descriptionField.getText()
                );
                if (newProduct != null) {
                    showAlert(Alert.AlertType.INFORMATION, "Product Added", "The product was successfully added!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add product. Please check the details.");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numbers for price and stock quantity.");
            }
        });

        vbox.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Price:"), priceField,
                new Label("Stock Quantity:"), stockQuantityField,
                new Label("Category:"), categoryField,
                new Label("Description:"), descriptionField,
                addButton);
        return vbox;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

