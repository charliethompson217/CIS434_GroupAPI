import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import com.opencsv.exceptions.CsvValidationException;

public class User {
    private String username;
    private String password;
    private String role;

    private User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // Setters with integrated update logic
    public boolean setUsername(String username) {
        try {
            if (!CSVIO.itemExists("Users", username)) {
                Dictionary<String, String> attributes = new Hashtable<>();
                attributes.put("Username", username);
                if(CSVIO.updateRow("Users", attributes)){
                    this.username = username;
                    return true;
                } else return false;
            } else {
                System.out.println("Username already taken.");
                return false;
            }
        } catch (IOException | CsvValidationException e) {
            System.out.println("Error checking username availability: " + e.getMessage());
            return false;
        }
    }

    public boolean setPassword(String password) {
        if(update("Password", password)){
            this.password = password;
            return true;
        } else return false;
    }

    public boolean setRole(String role) {
        if(update("Role", role)){
            this.role = role;
            return true;
        } else return false;
    }

    // Specific attribute update in CSV
    private boolean update(String attribute, String newValue) {
        Dictionary<String, String> attributes = new Hashtable<>();
        attributes.put("Username", username);
        attributes.put(attribute, newValue);

        try {
            return CSVIO.updateRow("Users", attributes);
        } catch (IOException | CsvValidationException e) {
            System.out.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    // Delete a user from the CSV file
    public boolean delete() {
        try {
            return CSVIO.deleteRow("Users", username);
        } catch (IOException | CsvValidationException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    // Static factory method to create a new User
    public static User createUser(String username, String password, String role) {
        try {
            // Check if the role (table) exists
            if (!CSVIO.tableExists("Users")) {
                createUserTable();
            }

            // Check if username is already taken
            if (!CSVIO.itemExists("Users", username)) {
                Dictionary<String, String> attributes = new Hashtable<>();
                attributes.put("Username", username);
                attributes.put("Password", password);
                attributes.put("Role", role);

                if (CSVIO.createRow("Users", attributes)) {
                    System.out.println("User successfully created.");
                    return new User(username, password, role);  // Return new user if creation is successful
                } else {
                    System.out.println("Failed to create user in CSV.");
                    return null;
                }
            } else {
                System.out.println("Username already taken.");
                return null;
            }
        } catch (IOException | CsvValidationException e) {
            System.out.println("Error creating user: " + e.getMessage());
            return null;
        }
    }

    // Create a table of users
    private static boolean createUserTable() throws IOException {
        if (CSVIO.tableExists("Users")) {
            System.out.println("User table already exists");
            return true;
        }
        else {
            String[] columns = {"Username", "Password", "Role"};
            return CSVIO.createTable("Users", columns,  "Username");
        }
    }

    // Static method to login
    public static User login(String username, String password) {
        try {
            Dictionary<String, String> credentials = CSVIO.getItem("Users", username);
            if (credentials != null && credentials.get("Password").equals(password)) {
                System.out.println("Login successful.");
                return new User(username, password, credentials.get("Role"));
            } else {
                System.out.println("Invalid username or password.");
                return null;
            }
        } catch (IOException | CsvValidationException e) {
            System.out.println("Login failed: " + e.getMessage());
            return null;
        }
    }

    public static ArrayList<User> search(Dictionary<String, String> attributes){
        try {
            ArrayList<User> users = new ArrayList<>();
            ArrayList<Dictionary<String, String>> items =  CSVIO.search("Users", attributes);
            if(items == null)
                return null;
            for (Dictionary<String, String> item : items) {
                users.add( new User(item.get("Username"), item.get("Password"), item.get("Role")));
            }
            return users;
        } catch (IOException | CsvValidationException e){
            return null;
        }
    }

    public static User search(String username){
        try {
            Dictionary<String, String> item =  CSVIO.getItem("Users", username);
            if(item != null)
                return new User(item.get("Username"), item.get("Password"), item.get("Role"));
            else return null;
        } catch (IOException | CsvValidationException e){
            return null;
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +  // Consider security implications of displaying passwords
                ", role='" + role + '\'' +
                '}';
    }
}
