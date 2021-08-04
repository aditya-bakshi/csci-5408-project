package csci5408.dbms.query;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import csci5408.dbms.tables.Row;
import csci5408.dbms.tables.Table;
import csci5408.dbms.tables.TableManager;
import csci5408.dbms.users.Transaction;

public class Insert {
    private TableManager tm;
    private Transaction t;
    private String regexCreate = "^insert +into +(?<tabName>\\w+) +values *\\((?<values>([0-9]+|'[^']*')(, ([0-9]+|'[^']*'))*)\\) *;$";
        

    public Insert(TableManager tm, Transaction t) {
        this.tm = tm;
        this.t = t;
    }

    public boolean execute(String query, PrintStream out) {
        try {
            query = query.strip().toLowerCase();
            
            if (!validateQueryFormat(query)) {
                out.println("Error: Invalid query format");
                return false;
            }
            String tableName = getTableName(query);
            
            if (tableName==null || tableName.strip().equals("")){
                out.println("Error: Invalid table name");
                return false;
            }
            
            if (!tm.getTableNames().contains(tableName)){
                out.println("Error: Table does not exists");
                return false;
            }

            if (!tm.lock(tableName, t)){
                out.println("Error: Unable to acquire lock a table.");
                return false;
            }

            // TODO get schema
            Entry<Class[], Object[]> e = getData(query);
            // TODO create table
            Table tab = tm.getTable(tableName, t);
            
            Row row = new Row(e.getKey(), e.getValue());
            if (!tab.validateSchema(row)){
                out.println("Error: Schema mismatch.");
                return false;
            }
            
            return tab.insertRow(row);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateQueryFormat(String query) {
        Pattern pattern = Pattern.compile(regexCreate, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        if (!pattern.matcher(query).matches()) {
            return false;
        }
        return true;
    }

    public String getTableName(String query) {
        Pattern pattern = Pattern.compile(regexCreate, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        if (!pattern.matcher(query).matches()) {
            return null;
        }

        Matcher m = pattern.matcher(query);
        while (m.find()) {
            String tableName = m.group("tabName").trim();
            return tableName;
        }
        return null;
    }

    public Entry<Class[], Object[]> getData(String query) {
        Pattern pattern = Pattern.compile(regexCreate, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        if (!pattern.matcher(query).matches()) {
            return null;
        }

        Matcher m = pattern.matcher(query);
        while (m.find()) {
            String raw = m.group("values").trim();
            String[] ps = raw.split(",");
            Object[] values = new Object[ps.length];
            Class[] types = new Class[ps.length];
            for (int i=0; i<ps.length; i++){
                if (ps[i].strip().startsWith("'")){  // String
                    values[i] = ps[i].strip().substring(1, ps[i].strip().length()-1);
                    types[i] = String.class;
                } else {  // Int
                    values[i] = Integer.parseInt(ps[i].strip());
                    types[i] = Integer.class;
                }
            }
            return new Entry<Class[],Object[]>(){

                @Override
                public Class[] getKey() {
                    // TODO Auto-generated method stub
                    return types;
                }

                @Override
                public Object[] getValue() {
                    // TODO Auto-generated method stub
                    return values;
                }

                @Override
                public Object[] setValue(Object[] value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
            };
        }
        return null;
    }
}
