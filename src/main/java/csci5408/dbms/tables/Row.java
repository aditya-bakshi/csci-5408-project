package csci5408.dbms.tables;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Row implements Serializable {
    private Object[] data;
    private Class[] schema;

    public Row(Class[] schema) {
        for (Class c: schema){
            if (!Serializable.class.isAssignableFrom(c)){
                new Exception("Unsupported data type");
            };
        }
        this.schema = schema.clone();
        this.data = new Object[this.schema.length];

    }

    public Row(Class[] schema, Object[] data) {
        this(schema);
        for (int i = 0; i < data.length; i++) {
            if (!this.setValue(i, data[i])){
                throw new RuntimeException("Error while setting value");
            }
        }
    }

    public Class[] getSchema() {
        return this.schema.clone();
    }

    public boolean setValue(int index, Object value) {
        if (this.schema[index].isInstance(value)) {
            this.data[index] = value;
            return true;
        } else {
            return false;
        }
    }

    public Object getValue(int index) {
        return this.data[index];
    }

    public Object[] getValues() {
        return this.data.clone();
    }

    @Override
    public String toString(){
        String[] t = Arrays.stream(this.data).map(x-> x.toString()).collect(Collectors.toList()).toArray(new String[0]);
        return String.join(", ", t);
    }
}
