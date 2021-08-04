package csci5408.dbms.users;

import java.beans.Transient;
import java.io.Serializable;

public class Session{
    private User user;

    private Transaction transaction;

    public Session(User user){
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public Transaction getTransaction(){
        if (this.transaction == null || transaction.isClosed()) {
            this.transaction = new Transaction(this);
        }
        return this.transaction;
    }

}
