package shop.users;

import org.json.simple.parser.ParseException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class Admin extends Customer {
    public Admin(String[] creds) {
        super(creds);
    }

    public Admin(String name, String password) throws XMLStreamException, IOException, ParseException {
        super(name,password,1);
    }

    public String getName(){
        return super.getName();
    }
}
