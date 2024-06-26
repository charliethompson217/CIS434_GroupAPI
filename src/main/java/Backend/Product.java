package backend;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class Product {
    private String productID;
    private String name;
    private double price;
    private int stockQuantity;
    private String category;
    private String description;
    private String database;
    // Private constructor to enforce the use of factory methods
    private Product(String database, String productID, String name, double price, int stockQuantity, String category, String description) {
        this.productID = productID;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.description = description;
        this.database = database;
    }

    // Getters
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

    // Factory method for creating a product
    public static Product createProduct(String database, String productID, String name, double price, int stockQuantity, String category, String description) {
        try {
            if(!createProductsTable(database)) {
                System.out.println("Creating product table failed");
                return null;
            }
            if (!CSVIO.itemExists( database, "Products", productID)) {
                Dictionary<String, String> attributes = new Hashtable<>();
                attributes.put("Product ID", productID);
                attributes.put("Name", name);
                attributes.put("Price", String.valueOf(price));
                attributes.put("Stock Quantity", String.valueOf(stockQuantity));
                attributes.put("Category", category);
                attributes.put("Description", description);

                if (CSVIO.createRow(database, "Products", attributes)) {
                    System.out.println("Product successfully created.");
                    return new Product(database, productID, name, price, stockQuantity, category, description);
                } else {
                    System.out.println("Failed to create product in CSV.");
                    return null;
                }
            } else {
                System.out.println("Product ID already taken.");
                return null;
            }
        } catch (IOException | CsvValidationException e) {
            System.out.println("Error creating product: " + e.getMessage());
            return null;
        }
    }

    // Instance methods for modifying product details
    public boolean setStock(int newStockQuantity) {
        if(update("Stock Quantity", String.valueOf(newStockQuantity))){
            stockQuantity = newStockQuantity;
            return true;
        } else return false;
    }

    public boolean setPrice(double newPrice) {
        if(update("Price", String.valueOf(newPrice))){
            price = newPrice;
            return true;
        } else return false;
    }

    public boolean setName(String newName) {
        if(update("Name", newName)){
            this.name = newName;
            return true;
        } else return false;
    }

    public boolean setCategory(String newCategory) {
        if(update("Category", newCategory)){
            this.category = newCategory;
            return true;
        } else return false;
    }

    public boolean setDescription(String newDescription) {
        if(update("Description", newDescription)){
            this.description = newDescription;
            return true;
        } else return false;
    }

    // General method to update any product field in CSV
    private boolean update(String attribute, String newValue) {
        Dictionary<String, String> attributes = new Hashtable<>();
        attributes.put("Product ID", productID); // Assume productID is the key
        attributes.put(attribute, newValue);

        try {
            return CSVIO.updateRow(database, "Products", attributes);
        } catch (IOException | CsvValidationException e) {
            System.out.println("Error updating product: " + e.getMessage());
            return false;
        }
    }

    // Method to delete a product
    public boolean delete() {
        try {
            return CSVIO.deleteRow(database, "Products", productID);
        } catch (IOException | CsvValidationException e) {
            System.out.println("Error deleting product: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "Database='" + database + '\'' +
                ", Product ID='" + productID + '\'' +
                ", Name='" + name + '\'' +
                ", Price=" + price +
                ", StockQuantity=" + stockQuantity +
                ", Category='" + category + '\'' +
                ", Description='" + description + '\'' +
                '}';
    }

    // Static method to ensure the category table exists
    private static boolean createProductsTable(String database) throws IOException {
        if (!CSVIO.tableExists(database, "Products")) {
            String[] header = {"Product ID", "Name", "Price", "Stock Quantity", "Category", "Description"};
            return CSVIO.createTable(database, "Products", header, "Product ID");
        }
        System.out.println("Products table already exists.");
        return true;
    }
    public static ArrayList<Product> search(String database, String attribute, double min, double max){
        try {

            ArrayList<Product> products = new ArrayList<>();
            ArrayList<Dictionary<String, String>> items =  CSVIO.searchRange(database, "Products", attribute, min, max);
            if(items == null)
                return null;
            for (Dictionary<String, String> item : items) {
                products.add(new Product(database, item.get("Product ID"), item.get("Name"), Double.parseDouble(item.get("Price")), Integer.parseInt(item.get("Stock Quantity")), item.get("Category"), item.get("Description")));
            }
            return products;
        } catch (IOException | CsvValidationException e){
            return null;
        }
    }
    public static ArrayList<Product> search(String database, Dictionary<String, String> attributes){
        try {
            ArrayList<Product> products = new ArrayList<>();
            ArrayList<Dictionary<String, String>> items =  CSVIO.search(database, "Products", attributes);
            if(items == null)
                return null;
            for (Dictionary<String, String> item : items) {
                products.add(new Product(database, item.get("Product ID"), item.get("Name"), Double.parseDouble(item.get("Price")), Integer.parseInt(item.get("Stock Quantity")), item.get("Category"), item.get("Description")));
            }
            return products;
        } catch (IOException | CsvValidationException e){
            return null;
        }
    }
    public static Product search(String database, String productID){
        try {
            Dictionary<String, String> item =  CSVIO.getItem(database,"Products", productID);
            if(item != null)
                return new Product(database, item.get("Product ID"), item.get("Name"), Double.parseDouble(item.get("Price")), Integer.parseInt(item.get("Stock Quantity")), item.get("Category"), item.get("Description"));
            else return null;
        } catch (IOException | CsvValidationException e){
            return null;
        }
    }
}
