package csci5408.dbms.users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Transaction implements Serializable{
    private boolean isClose;
    private Session session;
    private List<Runnable> commitCallbacks;
    private List<Runnable> rollbackCallbacks;

    public Transaction(Session session) {
        this.session = session;
        this.isClose = false;
        this.commitCallbacks = new ArrayList<>();
        this.rollbackCallbacks = new ArrayList<>();
    }

    private void close() {
        this.isClose = true;
    }

    public boolean isClosed() {
        return this.isClose;
    }

    public Session getSession() {
        return this.session;
    }

    public void setRollbackCallback(Runnable r) {
        this.rollbackCallbacks.add(r);
    }

    public void setCommitCallback(Runnable r) {
        this.commitCallbacks.add(r);
    }

    public void rollback() {
        for (Runnable r : this.rollbackCallbacks) {
            r.run();
        }
        close();
    }

    public void commit() {
        for (Runnable r : this.commitCallbacks) {
            r.run();
        }
        close();
    }
}
