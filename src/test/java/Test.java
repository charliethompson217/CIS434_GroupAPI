import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class Test {
    public static void main(String[] args) {

        // create user
        User.createUser("John", "password123", "employee");
        User.createUser("Alice", "password123", "employee");
        User.createUser("Jane", "password123", "employee");
        User.createUser("Billy", "password123", "Admin");

        // createUser returns new user
        User user_4 =  User.createUser("johnDoe", "password123", "employee");
        System.out.println(user_4.toString());

        // login verifies password and returns a user object if credentials match
        User curUser = User.login("Jane", "password123");

        // seting an attribute
        curUser.setRole("admin");

        // get an attribute
        String username = curUser.getUsername();
        System.out.println(username);

        // seaching for users that fit a description
        Dictionary<String, String> attributes = new Hashtable<>();
        attributes.put("Role", "Admin");
        ArrayList<User> users = User.search(attributes);
        for (User user : users) {
            System.out.println(user.toString());
        }

        // searching for a specific user
        User foundUser = User.search("johnDoe");
        System.out.println(foundUser.toString());

        // return all users
        Dictionary<String, String> attributes2 = new Hashtable<>();
        ArrayList<User> users2 = User.search(attributes2);
        for (User user : users2) {
            System.out.println(user.toString());
        }


        Product.createProduct("001", "Laptop", 1999, 10, "Electronics", "High-performance gaming laptop.");
        Product.createProduct("002", "Phone", 599.99, 50, "Electronics", "Android smartphone with a low-quality Camera.");
        Product.createProduct("003", "Phone", 999.99, 100, "Electronics", "Apple smartphone with a high-quality Camera.");
        Product.createProduct("004", "Watermelon", 5.99, 25, "Food", "Juicy ripe watermelon.");
        Product.createProduct("005", "Cake", 10.99, 5, "Food", "Delicious fluffy cake.");

        // searching for products that fit a description
        Dictionary<String, String> attributesToSearch = new Hashtable<>();
        attributesToSearch.put("Category", "Electronics");
        attributesToSearch.put("Name", "Phone");
        ArrayList<Product> products = Product.search(attributesToSearch);
        for (Product product : products) {
            System.out.println(product.toString());
        }

        // search for a specific product
        Product product1 = Product.search("001");
        System.out.println(product1.toString());

        // return all products
        Dictionary<String, String> attributesToSearch2 = new Hashtable<>();
        ArrayList<Product> products2 = Product.search(attributesToSearch2);
        for (Product product : products2) {
            System.out.println(product.toString());
        }

        // delete a product
        Product product2 = Product.search("002");
        product2.delete();

        // edit a product
        product1.updateStock(15);
        product1.updatePrice(799.99);
        product1.updateName("Android Phone");
        product1.updateCategory("Cellphone");
        product1.updateDescription("Android smartphone with RCS.");
    }
}
