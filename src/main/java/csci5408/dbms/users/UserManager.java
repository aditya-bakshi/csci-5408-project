package csci5408.dbms.users;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserManager implements Serializable{
    private final String USER_SPACE_PATH = "data/users";

    private Map<String, User> users;

    public UserManager() {
        this.users = readUsersFromFile();
        if (this.users == null) {
            this.users = new HashMap<>();
        }
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public User createUser(String username, String password) {
        if (this.users.keySet().contains(username)) {
            return null;
        }
        User user = new User(username, password);
        this.users.put(user.getUsername(), user);
        writeUsersToFile(users);
        return user;
    }

    private boolean writeUsersToFile(Map<String, User> users) {
        try {
            String filepath = USER_SPACE_PATH;
            new File(filepath).getParentFile().mkdirs();
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(users);
            objectOut.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private Map<String, User> readUsersFromFile() {
        try {
            String filepath = USER_SPACE_PATH;
            FileInputStream streamIn = new FileInputStream(filepath);
            ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
            Map<String, User> users = (Map<String, User>) objectinputstream.readObject();
            return users;
        } catch (Exception ex) {
            // ex.printStackTrace();
            return null;
        }
    }
}
