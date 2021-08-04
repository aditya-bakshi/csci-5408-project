package csci5408.dbms.tables;

import java.util.Arrays;

public class Schema {

    private String[] colNames;
    private Class[] colTypes;

    public Schema (String[] colNames, Class[] colTypes){
        this.colNames = colNames.clone();
        this.colTypes = colTypes.clone();
    }

    public String getColName(int index){
        return this.colNames[index];
    }
    
    public Class getColType(int index){
        return this.colTypes[index];
    }

    public String[] getAllNames(){
        return this.colNames.clone();
    }

    public Class[] getAllTypes(){
        return this.colTypes.clone();
    }

    public boolean equals(Schema s) {
        return Arrays.deepEquals(this.colTypes, s.colTypes) && Arrays.deepEquals(this.colNames, s.colNames);
    }
}
