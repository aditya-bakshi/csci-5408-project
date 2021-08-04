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

public class Update {
    private TableManager tm;
    private Transaction t;
    private String regexSelect = "^update +(?<tabName>\\w+) +(?<setCause>set \\w+ *= *([0-9]+|'[^']*')) +(?<whereCause>where \\w+ *= *([0-9]+|'[^']*'))?;$";

    public Update(TableManager tm, Transaction t) {
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

            Table tab = tm.getTable(tableName, t);
            Entry<String, Object> es = getSet(query);
            Entry<String, Object> ec = getCondition(query);
            
            int sIndex = Arrays.binarySearch(tab.getColNames(), es.getKey());
            Object sValue = es.getValue();
            
            // if (ec != null){
                int cIndex = Arrays.binarySearch(tab.getColNames(), ec.getKey());
                Object cValue = ec.getValue();
                
                for (Row r: tab.getAllRow()){
                    if (r.getValue(cIndex).equals(cValue)){
                        r.setValue(sIndex, sValue);
                    }
                }
                return true;
            // } else {
            //     for (Row r: tab.getAllRow()){
            //         r.setValue(sIndex, sValue);
            //     }
            //     return true;
            // }
        } catch (Exception e) {
            return false;
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

    public Entry<String, Object> getSet(String query) {
        Pattern pattern = Pattern.compile(regexSelect, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        if (!pattern.matcher(query).matches()) {
            return null;
        }

        Matcher m = pattern.matcher(query);
        while (m.find()) {
            String raw = m.group("setCause").trim();
            String[] ps = raw.split("=");
            String col = ps[0].replaceAll("set", "").strip();
            Object value;
            if (ps[1].strip().startsWith("'")){  // String
                value = ps[1].strip().substring(1, ps[1].strip().length()-1);
            } else {  // Int
                value = Integer.parseInt(ps[1].strip());
            }
            
            return new Entry<String,Object>(){

                @Override
                public String getKey() {
                    // TODO Auto-generated method stub
                    return col;
                }

                @Override
                public Object getValue() {
                    // TODO Auto-generated method stub
                    return value;
                }

                @Override
                public Object setValue(Object value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
            };
        }
        return null;
    }

    public Entry<String, Object> getCondition(String query) {
        Pattern pattern = Pattern.compile(regexSelect, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        if (!pattern.matcher(query).matches()) {
            return null;
        }

        Matcher m = pattern.matcher(query);
        while (m.find()) {
            String raw = m.group("whereCause").trim();
            String[] ps = raw.split("=");
            String col = ps[0].replaceAll("where", "").strip();
            Object value;
            if (ps[1].strip().startsWith("'")){  // String
                value = ps[1].strip().substring(1, ps[1].strip().length()-1);
            } else {  // Int
                value = Integer.parseInt(ps[1].strip());
            }
            
            return new Entry<String,Object>(){

                @Override
                public String getKey() {
                    // TODO Auto-generated method stub
                    return col;
                }

                @Override
                public Object getValue() {
                    // TODO Auto-generated method stub
                    return value;
                }

                @Override
                public Object setValue(Object value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
            };
        }
        return null;
    }
}
