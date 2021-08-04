package csci5408.dbms.query;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import csci5408.dbms.tables.TableManager;

public class CreateTable {
    private TableManager tm;

    public CreateTable(TableManager tm) {
        this.tm = tm;
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
            
            if (tm.getTableNames().contains(tableName)){
                out.println("Error: table already exists");
                return false;
            }

            // TODO get schema
            Entry<String[], Class[]> e = getSchema(query);
            // TODO create table
            tm.createTable(tableName, e.getKey(), e.getValue());

            return tm.getTableNames().contains(tableName);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateQueryFormat(String query) {
        String regexCreate = "^create *table *(?<tabName>\\w+) *\\((?<colCause>[a-z0-9]+ +(int|varchar)( *, *[a-z0-9]+ +(int|varchar))*)\\) *;$";
        Pattern pattern = Pattern.compile(regexCreate, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        if (!pattern.matcher(query).matches()) {
            return false;
        }
        return true;
    }

    public String getTableName(String query) {
        String regexCreate = "^create *table *(?<tabName>\\w+) *\\((?<colCause>[a-z0-9]+ +(int|varchar)( *, *[a-z0-9]+ +(int|varchar))*)\\) *;$";
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

    public Entry<String[], Class[]> getSchema(String query) {
        String regexCreate = "^create *table *(?<tabName>\\w+) *\\((?<colCause>[a-z0-9]+ +(int|varchar)( *, *[a-z0-9]+ +(int|varchar))*)\\) *;$";
        Pattern pattern = Pattern.compile(regexCreate, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        if (!pattern.matcher(query).matches()) {
            return null;
        }

        Matcher m = pattern.matcher(query);
        while (m.find()) {
            String raw = m.group("colCause").trim();
            String[] ps = raw.split(",");
            String[] names = new String[ps.length];
            Class[] types = new Class[ps.length];
            for (int i=0; i<ps.length; i++){
                names[i] = ps[i].strip().split(" ")[0].strip();
                types[i] = ps[i].strip().split(" ")[1].strip().equals("int") ? Integer.class : String.class;
            }
            return new Entry<String[],Class[]>(){

                @Override
                public String[] getKey() {
                    return names;
                }

                @Override
                public Class[] getValue() {
                    // TODO Auto-generated method stub
                    return types;
                }

                @Override
                public Class[] setValue(Class[] value) {
                    // TODO Auto-generated method stub
                    return null;
                }
            };
        }
        return null;
    }
}
