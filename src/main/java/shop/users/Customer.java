package shop.users;
import org.json.simple.parser.ParseException;
import shop.constants.Files_;
import shop.products.Cart;
import shop.products.Product;

import javax.xml.stream.XMLStreamException;
import java.io.*;

public class Customer extends User{
    public Customer(String name, String password) throws XMLStreamException, IOException, ParseException {
        super(getNewId(),password);
        this.name=name;
        this.cart=new Cart();
        try (PrintWriter writer = new PrintWriter(new FileWriter(Files_.userCsv, true))) {
            writer.printf("\n%s,%s,%s,%s,%s", this.id, this.password, "0", this.name, this.cart.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Customer(String name, String password, int isAdmin) throws XMLStreamException, IOException, ParseException {
        super(getNewId(),password);
        this.name=name;
        this.cart=new Cart();
        try (PrintWriter writer = new PrintWriter(new FileWriter(Files_.userCsv, true))) {
            writer.printf("\n%s,%s,%s,%s,%s", this.id, this.password, "1", this.name, this.cart.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getNewId() {
        String lastId = "0";
        try (BufferedReader br = new BufferedReader(new FileReader(Files_.userCsv))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                lastId = fields[0];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int newId = Integer.parseInt(lastId) + 1;
        return String.format("%04d", newId);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    private String name;
    public Cart cart;

    public Cart getCart() {
        return cart;
    }

    public Customer(String[] creds) {
        super(creds[0],creds[1]);
        try (BufferedReader br = new BufferedReader(new FileReader(Files_.userCsv))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(creds[0])) {
                    this.name = fields[3];
                    this.cart = new Cart(fields[4]);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public void addProdToCart(String[] prodInfo) throws IOException, ParseException {
        String prodId = prodInfo[0];
        int quantity = Integer.parseInt(prodInfo[1]);
        cart.addProduct(prodId, quantity);
    }
    public void delProdFromCart(String[] prodInfo) throws IOException, ParseException {
        String prodId = prodInfo[0];
        int quantity = Integer.parseInt(prodInfo[1]);
        cart.removeProduct(prodId, quantity);
    }
    public void updateCsv() throws IOException {
        // Read the contents of the user CSV file
        BufferedReader reader = new BufferedReader(new FileReader(Files_.userCsv));
        StringBuilder stringBuilder = new StringBuilder();
        String line = reader.readLine();

        while (line != null) {
            String[] fields = line.split(",");
            if (fields[0].equals(this.id)) {
                // Replace the current line with the updated information
                stringBuilder.append(String.format("%s,%s,%s,%s,%s", this.id, this.password, fields[2], this.name, this.cart.getId()));
            } else {
                // Keep the original line if the ID doesn"t match
                stringBuilder.append(line);
            }

            stringBuilder.append("\n");
            line = reader.readLine();
        }

        reader.close();

        // Write the updated CSV file
        FileWriter writer = new FileWriter(Files_.userCsv);
        writer.write(stringBuilder.toString());
        writer.close();
    }


}
