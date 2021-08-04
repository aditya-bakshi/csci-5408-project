package csci5408.dbms.query;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import csci5408.dbms.tables.Row;
import csci5408.dbms.tables.Table;
import csci5408.dbms.tables.TableManager;
import csci5408.dbms.users.Transaction;

public class Select {
    private TableManager tm;
    private Transaction t;
    private String regexSelect = "^select +(?<cols>\\w+ *(, *\\w+ *)*) +from +(?<tabName>\\w+) *;$";
        

    public Select(TableManager tm, Transaction t) {
        this.tm = tm;
        this.t = t;
    }

    public void execute(String query, PrintStream out) {
        try {
            query = query.strip().toLowerCase();
            
            if (!validateQueryFormat(query)) {
                out.println("Error: Invalid query format");
                return;
            }
            String tableName = getTableName(query);
            
            if (tableName==null || tableName.strip().equals("")){
                out.println("Error: Invalid table name");
                return;
            }
            
            if (!tm.getTableNames().contains(tableName)){
                out.println("Error: Table does not exists");
                return;
            }

            if (!tm.lock(tableName, t)){
                out.println("Error: Unable to acquire lock a table.");
                return;
            }

            // TODO get schema
            String[] cols = getCols(query);
            // TODO create table
            Table tab = tm.getTable(tableName, t);
            
            int[] index = new int[cols.length];
            for (int i=0; i< cols.length; i++){
                if (!Arrays.asList(tab.getColNames()).contains(cols[i])){
                    out.println("Error: Invalid column name: "+cols[i]);
                    return;
                }
                index[i] = Arrays.asList(tab.getColNames()).indexOf(cols[i]);
            }
            

            List<String[]> data = new ArrayList<>();
            for (Row row: tab.getAllRow()){
                String[] d = new String[index.length];
                for (int i=0; i<d.length; i++){
                    d[i] = row.getValue(index[i]).toString();
                }
                data.add(d);
            }

            out.println(String.join("\t|", cols));
            for (String[] d: data){
                out.println(String.join("\t|", d));
            }

        } catch (Exception e) {
            return;
        }
    }

    public boolean validateQueryFormat(String query) {
        Pattern pattern = Pattern.compile(regexSelect, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        if (!pattern.matcher(query).matches()) {
            return false;
        }
        return true;
    }

    public String getTableName(String query) {
        Pattern pattern = Pattern.compile(regexSelect, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
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

    public String[] getCols(String query) {
        Pattern pattern = Pattern.compile(regexSelect, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        if (!pattern.matcher(query).matches()) {
            return null;
        }

        Matcher m = pattern.matcher(query);
        while (m.find()) {
            String raw = m.group("cols").trim();
            String[] ps = raw.split(",");
            String[] cols = new String[ps.length];
            for (int i=0; i<ps.length; i++){
                cols[i] = ps[i].strip();
            }
            return cols;
        }
        return null;
    }
}
