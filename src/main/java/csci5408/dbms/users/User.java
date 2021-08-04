package csci5408.dbms.users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable{
    private String username;
    private String password;
    private transient Session session;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Session getSession(String password) {
        if (authenticate(password)) {
            if (this.session == null){
                this.session = new Session(this);
            }
            return this.session;
        } else {
            return null;
        }
    }

    public String getUsername() {
        return this.username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private boolean authenticate(String password) {
        return this.password.equals(password);
    }
}
