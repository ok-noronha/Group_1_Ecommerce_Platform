package shop.products;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import shop.constants.Files_;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Cart {
    String id;
    HashMap<String, Integer> list;
    double total;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, Integer> getList() {
        return list;
    }

    public void setList(HashMap<String, Integer> list) {
        this.list = list;
    }

    public double getTotal() {
        return total;
    }

    public Cart() throws IOException, ParseException {
        this.id = getNewId();
        this.list = new HashMap<>();
        this.writeJson();
    }

    public Cart(String id) throws IOException, XMLStreamException, ParseException {
        this.id = id;
        this.list = new HashMap<>();
        this.readJson();
    }
    public void addProduct(String prod, int quantity) throws IOException, ParseException {
        if (list.containsKey(prod)) {
            int currentQuantity = list.get(prod);
            list.put(prod, currentQuantity + quantity);
        } else {
            list.put(prod, quantity);
        }
        this.writeJson();
    }
    public void removeProduct(String prod, int quantity) throws IOException, ParseException {
        if (list.containsKey(prod)) {
            int currentQuantity = list.get(prod);
            if (currentQuantity > quantity) {
                list.put(prod, currentQuantity - quantity);
            } else {
                list.remove(prod);
            }
        }
        this.writeJson();
    }
    public void checkout() throws IOException, ParseException {
        // Create a copy of the list property to avoid ConcurrentModificationException
        HashMap<String, Integer> copyList = new HashMap<>(this.list);

        // Print each product in the cart, remove from the cart, and update the stock in the JSON file
        for (Map.Entry<String, Integer> entry : copyList.entrySet()) {
            String prodId = entry.getKey();
            int quantity = entry.getValue();
            System.out.println("Product: " + prodId + ", Quantity: " + quantity);
            this.removeProduct(prodId, quantity);
            Product tempProd = new Product(prodId);
            int newStock = (tempProd.getStock() > quantity) ? tempProd.getStock() - quantity : 0;
            tempProd.setStock(newStock);
            tempProd.writeToJSON();
        }

        // Remove the cart from the JSON file
        JSONParser parser = new JSONParser();
        JSONArray carts = (JSONArray) parser.parse(new FileReader(Files_.cartsJson));
        for (Object obj : carts) {
            JSONObject cart = (JSONObject) obj;
            String cartId = (String) cart.get("id");
            if (cartId.equals(this.id)) {
                carts.remove(obj);
                break;
            }
        }
        FileWriter file = new FileWriter(Files_.cartsJson);
        file.write(carts.toJSONString());
        file.flush();
        file.close();
    }
    public void readJson() throws IOException, ParseException {
        // Read the contents of the carts JSON file
        JSONParser parser = new JSONParser();
        JSONArray carts = (JSONArray) parser.parse(new FileReader(Files_.cartsJson));

        // Find the cart with the matching ID and populate the HashMap with its products
        for (Object obj : carts) {
            JSONObject cart = (JSONObject) obj;
            String cartId = (String) cart.get("id");
            if (cartId.equals(this.id)) {
                JSONArray products = (JSONArray) cart.get("products");
                for (Object prodObj : products) {
                    JSONObject prod = (JSONObject) prodObj;
                    String prodId = (String) prod.get("id");
                    int quantity = ((Long) prod.get("quantity")).intValue();
                    this.list.put(prodId, quantity);
                }
                break;
            }
        }
    }
    public void writeJson() throws IOException, ParseException {
        // Read the contents of the carts JSON file
        JSONParser parser = new JSONParser();
        JSONArray carts = (JSONArray) parser.parse(new FileReader(Files_.cartsJson));

        // Find the cart with the matching ID or create a new one if not found
        boolean found = false;
        for (Object obj : carts) {
            JSONObject cart = (JSONObject) obj;
            String cartId = (String) cart.get("id");
            if (cartId.equals(this.id)) {
                // Update the existing cart with the current contents of the HashMap
                JSONArray products = new JSONArray();
                for (Map.Entry<String, Integer> entry : this.list.entrySet()) {
                    JSONObject prod = new JSONObject();
                    prod.put("id", entry.getKey());
                    prod.put("quantity", entry.getValue());
                    products.add(prod);
                }
                cart.put("products", products);
                found = true;
                break;
            }
        }

        // If the cart was not found, create a new one with the current contents of the HashMap
        if (!found) {
            JSONObject cart = new JSONObject();
            cart.put("id", this.id);
            JSONArray products = new JSONArray();
            for (Map.Entry<String, Integer> entry : this.list.entrySet()) {
                JSONObject prod = new JSONObject();
                prod.put("id", entry.getKey());
                prod.put("quantity", entry.getValue());
                products.add(prod);
            }
            cart.put("products", products);
            carts.add(cart);
        }

        // Write the updated carts JSON file
        FileWriter writer = new FileWriter(Files_.cartsJson);
        writer.write(carts.toJSONString());
        writer.close();
    }

    public String toString() {
        StringBuilder tableContent = new StringBuilder();
        tableContent.append("\n");
        tableContent.append(String.format("%-5s %-20s %-10s %-10s %-10s\n", "ID", "Name", "Price", "Quantity", "Total"));
        this.total = 0;
        for (Map.Entry<String, Integer> entry : list.entrySet()) {
            Product prod = null;
            try {
                prod = new Product(entry.getKey());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            int quantity = entry.getValue();
            double total = prod.getPrice() * quantity;
            tableContent.append(String.format("%-5s %-20s |%-9.2f|%-10d|%-9.2f\n", prod.getId(), prod.getName(), prod.getPrice(), quantity, total));
            this.total += total;
        }
        tableContent.append("\n");
        tableContent.append(String.format("Total: $%.2f", this.total));
        return tableContent.toString();
    }
    private static String getNewId() throws IOException, ParseException {
        String lastId = "0";
        JSONParser parser = new JSONParser();
        JSONArray carts = (JSONArray) parser.parse(new FileReader(Files_.cartsJson));
        for (Object obj : carts) {
            JSONObject cart = (JSONObject) obj;
            String cartId = (String) cart.get("id");
            int idNum = Integer.parseInt(cartId.substring(1));
            if (idNum > Integer.parseInt(lastId)) {
                lastId = String.valueOf(idNum);
            }
        }
        int newIdNum = Integer.parseInt(lastId) + 1;
        String newIdStr = String.format("C%04d", newIdNum);
        return newIdStr;
    }
    public static void removeCart(String id) {
        try {
            JSONParser parser = new JSONParser();
            JSONArray carts = (JSONArray) parser.parse(new FileReader(Files_.cartsJson));
            for (Object obj : carts) {
                JSONObject cart = (JSONObject) obj;
                String cartId = (String) cart.get("id");
                if (cartId.equals(id)) {
                    carts.remove(obj);
                    break;
                }
            }
            FileWriter file = new FileWriter(Files_.cartsJson);
            file.write(carts.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
