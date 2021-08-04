package csci5408.dbms.tables;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import csci5408.dbms.users.Transaction;

public class TableManager {
    private final String TABLE_SPACE_PATH = "data/table_space";
    private Map<String, Transaction> locks;
    private Map<String, Table> cache;
    // private List<String> tables;

    public TableManager() {
        this.locks = new HashMap<>();
        this.cache = new HashMap<>();
    }

    public Table getTable(String tableName, Transaction t) {
        if (lock(tableName, t)) {
            if (!this.cache.containsKey(tableName)) {
                this.cache.put(tableName, readTableFromFile(tableName));
            }
            return this.cache.get(tableName);
        } else {
            return null;
        }
    }

    public void createTable(String tableName, String[] colNames, Class[] schema) {
        Table table = new Table(tableName, colNames, schema);
        writeTableToFile(table);
    }

    public Transaction getLock(String table) {
        if (isLocked(table)) {
            return this.locks.get(table);
        } else {
            return null;
        }
    }

    public boolean isLocked(String table) {
        return this.locks.containsKey(table) && (!this.locks.get(table).isClosed());
    }

    public boolean lock(String table, Transaction t) {
        if (isLocked(table)) {
            if (getLock(table) == t) {
                return true;
            } else {
                return false;
            }
        } else {
            this.locks.put(table, t);
            t.setCommitCallback(() -> {
                commit(table, t);
            });
            t.setRollbackCallback(() -> {
                rollback(table, t);
            });
            return true;
        }
    }

    public boolean unlock(String table, Transaction t) {
        if (!isLocked(table)) {
            return true;
        } else if (getLock(table) == t) {
            this.locks.remove(table);
            return true;
        } else {
            return false;
        }
    }

    public boolean commit(String table, Transaction t) {
        if (this.getLock(table) == t) {
            writeTableToFile(this.getTable(table, t));
            this.cache.remove(table);
            return true;
        } else {
            return false;
        }
    }

    public boolean rollback(String table, Transaction t) {
        if (this.getLock(table) == t) {
            this.cache.remove(table);
            unlock(table, t);
            return true;
        } else {
            return false;
        }
    }

    public List<String> getTableNames() {
        return this.getFileNames();
    }

    private boolean writeTableToFile(Table table) {
        try {
            String filepath = getFilePath(table.getName());
            new File(filepath).getParentFile().mkdirs();
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(table);
            objectOut.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private Table readTableFromFile(String tableName) {
        try {
            String filepath = getFilePath(tableName);
            FileInputStream streamIn = new FileInputStream(filepath);
            ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
            Table table = (Table) objectinputstream.readObject();
            return table;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String getFilePath(String tableName) {
        return TABLE_SPACE_PATH + "/" + tableName;
    }

    private List<String> getFileNames() {
        File folder = new File(TABLE_SPACE_PATH);
        List<String> files = Arrays.stream(folder.listFiles()).filter(x -> x.isFile()).map(x -> x.getName())
                .collect(Collectors.toList());
        return files;
    }
}
