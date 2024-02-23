package shop.users;
import org.json.simple.parser.ParseException;
import shop.constants.Files_;
import shop.products.Cart;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.Objects;

public class User {
    public User(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    String id;
    String password;

    public static boolean validateUser(String user_id, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(Files_.userCsv))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(user_id) && fields[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean isAdmin() {
        try (BufferedReader br = new BufferedReader(new FileReader(Files_.userCsv))) {
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] fields = line.split(",");
//                System.out.println(Objects.equals(fields[0], this.id));
                if (Objects.equals(fields[0], this.id)) {
                    return Integer.parseInt(fields[2]) ==1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static User addUser(Object[] userParams) throws XMLStreamException, IOException, ParseException {
        String name = (String) userParams[0];
        String password = (String) userParams[1];
        boolean isAdmin = (boolean) userParams[2];
        if (isAdmin) {
            return new Admin(name, password);
        } else {
            return new Customer(name, password);
        }
    }
    public static void removeUser(String id) {
        try {
            File inputFile = new File(Files_.userCsv);
            File tempFile = new File("temp.csv");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] fields = currentLine.split(",");
                if (fields[0].equals(id)) {
                    Cart.removeCart(fields[4]);
                    continue;
                }
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
            boolean deleted = inputFile.delete();
            boolean renamed = tempFile.renameTo(inputFile);
            if (!deleted) {
                System.out.println("Error: Could not delete file " + Files_.userCsv);
            }
            if (!renamed) {
                System.out.println("Error: Could not rename file temp.csv to " + Files_.userCsv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}