import java.io.FileNotFoundException;
import java.io.IOException;

import com.database.management.system.DBController;
/**
 * 
 * @author Aditya
 *
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        DBController dbController = new DBController();
        dbController.run();
    }
}
