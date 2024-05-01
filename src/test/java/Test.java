import backend.Product;
import backend.User;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class Test {
    public static void main(String[] args) {

        // create user
        User.createUser("Test", "John", "password123", "employee");
        User.createUser("Test", "Alice", "password123", "employee");
        User.createUser("Test", "Jane", "password123", "employee");
        User.createUser("Test", "Billy", "password123", "Admin");

        // createUser returns new user
        User user_4 =  User.createUser("Test", "johnDoe", "password123", "employee");
        System.out.println(user_4.toString());

        // login verifies password and returns a user object if credentials match
        User curUser = User.login("Test", "Jane", "password123");

        // seting an attribute
        curUser.setRole("admin");

        // get an attribute
        String username = curUser.getUsername();
        System.out.println(username);

        // seaching for users that fit a description
        Dictionary<String, String> attributes = new Hashtable<>();
        attributes.put("Role", "Admin");
        ArrayList<User> users = User.search("Test", attributes);
        for (User user : users) {
            System.out.println(user.toString());
        }

        // searching for a specific user
        User foundUser = User.search("Test", "johnDoe");
        System.out.println(foundUser.toString());

        // return all users
        Dictionary<String, String> attributes2 = new Hashtable<>();
        ArrayList<User> users2 = User.search("Test", attributes2);
        for (User user : users2) {
            System.out.println(user.toString());
        }


        Product.createProduct("Test", "001", "Laptop", 1999, 10, "Electronics", "High-performance gaming laptop.");
        Product.createProduct("Test", "002", "Phone", 599.99, 50, "Electronics", "Android smartphone with a low-quality Camera.");
        Product.createProduct("Test", "003", "Phone", 999.99, 100, "Electronics", "Apple smartphone with a high-quality Camera.");
        Product.createProduct("Test", "004", "Watermelon", 5.99, 25, "Food", "Juicy ripe watermelon.");
        Product.createProduct("Test", "005", "Cake", 10.99, 5, "Food", "Delicious fluffy cake.");

        // searching for products that fit a description
        Dictionary<String, String> attributesToSearch = new Hashtable<>();
        attributesToSearch.put("Category", "Electronics");
        attributesToSearch.put("Name", "Phone");
        ArrayList<Product> products = Product.search("Test", attributesToSearch);
        for (Product product : products) {
            System.out.println(product.toString());
        }

        // search for a specific product
        Product product1 = Product.search("Test", "001");
        System.out.println(product1.toString());

        // return all products
        Dictionary<String, String> attributesToSearch2 = new Hashtable<>();
        ArrayList<Product> products2 = Product.search("Test", attributesToSearch2);
        for (Product product : products2) {
            System.out.println(product.toString());
        }

        // delete a product
        Product product2 = Product.search("Test", "002");
        product2.delete();

        // edit a product
        product1.setStock(15);
        product1.setPrice(799.99);
        product1.setName("Android Phone");
        product1.setCategory("Cellphone");
        product1.setDescription("Android smartphone with RCS.");
    }
}
