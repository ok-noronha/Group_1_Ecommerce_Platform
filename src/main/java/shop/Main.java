package shop;

import org.json.simple.parser.ParseException;
import shop.display.Display;
import shop.products.Cart;
import shop.products.Product;
import shop.users.Admin;
import shop.users.Customer;
import shop.users.User;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws XMLStreamException, IOException, ParseException {
        String[] creds = loginflow();
        User user = new User(creds[0],creds[1]);
        if (user.isAdmin()){
            user = new Admin(creds);
            adminflow((Admin) user);
        }
        else {
            user = new Customer(creds);
            customerflow((Customer) user);
        }
    }

    static String[] loginflow(){
        boolean loggedout = true;
        String[] creds = new String[0];
        while (loggedout) {
            creds = Display.login();
            loggedout = !(User.validateUser(creds[0], creds[1]));
            if (loggedout)
                Display.error("Invalid Credentials");
        }
        return creds;
    }

    static void adminflow(Admin admin) throws XMLStreamException, IOException, ParseException {
        Display.displayGreeting(admin);
        boolean exit = false;
        while(!exit){
        int choice = Display.adminChoices();
        switch (choice) {
            case 1:
                User.addUser(Display.askUser());
                break;
            case 2:
                User.removeUser(Display.askUserId());
                break;
            case 3:
                new Product();
                break;
            case 4:
                Product.deleteFromJSON(Display.askId());
                break;
            case 5:
                Product t = new Product(Display.askId());
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter stock");
                t.setStock(scanner.nextInt());
                t.writeToJSON();
                break;
            case 6:
                customerflow(admin);
                break;
            case 7:
                Display.exitWishes();
                System.exit(0);
            default:
                Display.error("Invalid choice. Please enter a number between 1 and 7.");
                Display.adminChoices();
                break;
        }
    }}
    static void customerflow(Customer customer) throws XMLStreamException, IOException, ParseException {
        Display.displayGreeting(customer);
        boolean exit = false;
        while(!exit){
            int ch = Display.custChoice();
            switch (ch) {
                case 1:
                    Display.displayProducts();
                    break;
                case 2:
                    Display.custCart(customer);
                    break;
                case 3:
                    customer.addProdToCart((Display.askProd()));
//                    System.out.println(Display.askProd());
                    break;
                case 4:
                    customer.delProdFromCart(Objects.requireNonNull(Display.askProd()));
                    break;
                case 5:
                    customer.getCart().checkout();
                    break;
                case 6:
                    customer.setName(Display.askName());
                    customer.updateCsv();
                    break;
                case 7:
                    customer.setPassword(Display.askPassword(customer));
                    customer.updateCsv();
                    break;
                case 8:
                    Display.exitWishes();
                    exit = true;
                    break;
                default:
                    // Invalid choice
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
}
