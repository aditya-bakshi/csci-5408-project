package csci5408.dbms.tables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Table implements Serializable {
    private String tableName;
    private List<Row> rows;
    private Class[] schema;
    private String[] colNames;

    public Table (String tableName, String[] colNames, Class[] schema) {
        this.tableName = tableName;
        this.rows = new ArrayList<>();
        this.schema = schema.clone();
        this.colNames = colNames.clone();
        for (Class c: schema){
            if (!Serializable.class.isAssignableFrom(c)){
                new Exception("Unsupported data type");
            };
        }
    }

    public String getName(){
        return this.tableName;
    }
    
    public List<Row> getAllRow() {
        return this.rows;
    }

    public boolean insertRow(Row row) {
        if (!validateSchema(row)){
            return false;
        } else {
            this.rows.add(row);
            return true;
        }
    }
    
    public boolean validateSchema(Row row) {
        return Arrays.deepEquals(row.getSchema(), this.schema);
    }

    public boolean deleteRow(Row row) {
        return rows.remove(row);
    }

    public Class[] getSchema() {
        return this.schema.clone();
    }

    public String[] getColNames() {
        return this.colNames.clone();
    }
}
