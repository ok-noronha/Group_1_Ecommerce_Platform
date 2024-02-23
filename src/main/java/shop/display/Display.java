package shop.display;

import org.json.simple.parser.ParseException;
import shop.constants.Files_;
import shop.products.Cart;
import shop.products.Product;
import shop.users.Admin;
import shop.users.Customer;
import shop.users.User;

import java.io.IOException;
import java.util.Scanner;
import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Display {
    public static String[] login() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username:");
        String username = scanner.nextLine();
        System.out.println("Enter your password:");
        String password = scanner.nextLine();
        return new String[]{username, password};
    }
    public static void error(String message) {
        String ANSI_RED = "\u001B[31m";
        String ANSI_RESET = "\u001B[0m";
        System.out.println(ANSI_RED + "Error: " + message + ANSI_RESET);
    }

    public static void cartInfo(Cart cart) {
        System.out.println(cart);
    }
    public static void userInfo(User user) {
        System.out.println(user);
    }

    public static void displayGreeting(User user) {
        if (user instanceof Admin) {
            Admin admin = (Admin) user;
            System.out.println("Hello, " + admin.getName() + "! Welcome back to the admin panel.");
        } else if (user instanceof Customer) {
            Customer customer = (Customer) user;
            System.out.println("Hello, " + customer.getName() + "! Welcome back to our website.");
        } else {
            System.out.println("Hello, " + user.getId() + "!");
        }
    }
    public static Integer custChoice() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please select an option:");
        System.out.println("1. View products");
        System.out.println("2. View cart");
        System.out.println("3. Add product to cart");
        System.out.println("4. Remove product from cart");
        System.out.println("5. Checkout");
        System.out.println("6. Change name");
        System.out.println("7. Change password");
        System.out.println("8. Exit");
        System.out.print("Enter your choice (1-8): ");
        return scanner.nextInt();
    }
    public static void exitWishes() {
        System.out.println("Thank you for using our portal. Have a nice day!");
    }

    public static void displayProducts() {
        try {
            // Read products from JSON file
            JSONParser parser = new JSONParser();
            JSONArray products = (JSONArray) parser.parse(new FileReader(Files_.productsJson));

            // Display products in table format
            System.out.printf("%-5s %-20s %-10s %-10s\n", "ID", "Name", "Price", "Stock");
            for (Object obj : products) {
                JSONObject product = (JSONObject) obj;
                String id = (String) product.get("id");
                String name = (String) product.get("name");
                double price = (double) product.get("price");
                long stock = (long) product.get("stock");
                System.out.printf("%-5s %-20s $%-9.2f %-10d\n", id, name, price, stock);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void custCart(Customer customer) {
        System.out.println(customer.cart);
    }
    public static String[] askProd() throws IOException, ParseException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which product?");
        String prodId = scanner.nextLine();
        System.out.println("Is this the product? (Y/N)");
        System.out.println(new Product(prodId));
        String confirmation = scanner.nextLine().toUpperCase();
        if (!confirmation.equals("Y")) {
            return null;
        }
        System.out.println("How many ?");
        int quantity = scanner.nextInt();
        return new String[]{prodId, String.valueOf(quantity)};
    }
    public static String askPassword(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your old password:");
        String oldPassword = scanner.nextLine();
        if (!user.getPassword().equals(oldPassword)) {
            error("Incorrect password");
            return null;
        }
        System.out.println("Enter your new password:");
        String newPassword = scanner.nextLine();
        return newPassword;
    }
    public static String askName() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your new name:");
        return scanner.nextLine();
    }
    public static int adminChoices() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please select an option:");
        System.out.println("1. Add user");
        System.out.println("2. Delete user");
        System.out.println("3. Add product to inventory");
        System.out.println("4. Delete product from inventory");
        System.out.println("5. Change stock");
        System.out.println("6. Customer login");
        System.out.println("7. Exit");
        System.out.print("Enter your choice (1-7): ");
        return scanner.nextInt();
    }
    public static Object[] askUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name:");
        String name = scanner.nextLine();
        System.out.println("Enter your password:");
        String password = scanner.nextLine();
        System.out.println("Are you an admin? (Y/N)");
        String isAdminStr = scanner.nextLine().toUpperCase();
        boolean isAdmin = isAdminStr.equals("Y");
        return new Object[]{name, password, isAdmin};
    }
    public static String askUserId() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your id:");
        return scanner.nextLine();
    }


    public static String askId() {
        Scanner scanner = new Scanner(System.in);
        // Prompt the user for the ID of the product to delete
        System.out.print("Enter the ID of the product to delete: ");
        return scanner.nextLine();
    }
}
