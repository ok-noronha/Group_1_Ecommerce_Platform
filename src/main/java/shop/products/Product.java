package shop.products;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.json.simple.JSONArray;
import shop.constants.Files_;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Product {
    private String id;
    private Double price;
    private String name;

    private int stock;

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Product(String id) throws IOException, ParseException {
        this.id = id;
        this.readFromJSON();
    }

    public Product() {
        Scanner scanner = new Scanner(System.in);

        try {
            // Get the next ID from the JSON file
            this.id = Product.getNextId();

            // Prompt the user for the product"s name, price, and stock
            System.out.print("Enter the product's name: ");
            this.name = scanner.nextLine();

            System.out.print("Enter the product's price: ");
            this.price = Double.parseDouble(scanner.nextLine());

            System.out.print("Enter the product's stock: ");
            this.stock = Integer.parseInt(scanner.nextLine());

            JSONParser parser = new JSONParser();
            JSONArray productsArray = (JSONArray) parser.parse(new FileReader(Files_.productsJson));

            // Create a new JSON object for the new product
            JSONObject newProductObj = new JSONObject();
            newProductObj.put("id", this.id);
            newProductObj.put("name", this.name);
            newProductObj.put("price", this.price);
            newProductObj.put("stock", this.stock);

            // Add the new product object to the products array
            productsArray.add(newProductObj);

            // Write the updated products array to the JSON file
            FileWriter file = new FileWriter(Files_.productsJson);
            file.write(productsArray.toJSONString());
            file.flush();
            file.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFromJSON(String id) throws IOException, ParseException {
        // Read the contents of the products JSON file
        JSONParser parser = new JSONParser();
        JSONArray productsArray = (JSONArray) parser.parse(new FileReader(Files_.productsJson));

        // Find the product with the matching ID and remove it from the products array
        for (int i = 0; i < productsArray.size(); i++) {
            JSONObject productObj = (JSONObject) productsArray.get(i);
            String productId = (String) productObj.get("id");
            if (productId.equals(id)) {
                productsArray.remove(i);
                break;
            }
        }

        // Write the updated products array to the JSON file
        FileWriter file = new FileWriter(Files_.productsJson);
        file.write(productsArray.toJSONString());
        file.flush();
        file.close();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id + "\"" +
                ", price=" + price +
                ", name=" + name + "\"" +
                "}";
    }

    private void readFromJSON() throws IOException, ParseException {
        // Read the contents of the products JSON file
        JSONParser parser = new JSONParser();
        JSONArray productsArray = (JSONArray) parser.parse(new FileReader(Files_.productsJson));

        // Look up the product with the matching ID and populate the object"s fields
        for (Object obj : productsArray) {
            JSONObject productObj = (JSONObject) obj;
            String productId = (String) productObj.get("id");
            if (productId.equals(this.id)) {
                this.name = (String) productObj.get("name");
                this.price = (Double) productObj.get("price");
                this.stock = ((Long) productObj.get("stock")).intValue();
                break;
            }
        }
    }
    public void writeToJSON() throws IOException, ParseException {
        // Read the contents of the products JSON file
        JSONParser parser = new JSONParser();
        JSONArray productsArray = (JSONArray) parser.parse(new FileReader(Files_.productsJson));

        // Find the product with the matching ID and update its fields
        for (Object obj : productsArray) {
            JSONObject productObj = (JSONObject) obj;
            String productId = (String) productObj.get("id");
            if (productId.equals(this.id)) {
                productObj.put("name", this.name);
                productObj.put("price", this.price);
                productObj.put("stock", this.stock);
                break;
            }
        }

        // Write the updated products array to the JSON file
        FileWriter file = new FileWriter(Files_.productsJson);
        file.write(productsArray.toJSONString());
        file.flush();
        file.close();
    }
    public static String getNextId() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONArray productsArray = (JSONArray) parser.parse(new FileReader(Files_.productsJson));

        // Get the last product in the array and increment its ID
        JSONObject lastProductObj = (JSONObject) productsArray.get(productsArray.size() - 1);
        String lastProductId = (String) lastProductObj.get("id");
        int nextId = Integer.parseInt(lastProductId.substring(1)) + 1;

        return "P" + nextId;
    }
}