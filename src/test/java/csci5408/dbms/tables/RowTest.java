package csci5408.dbms.tables;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import csci5408.dbms.tables.Row;

public class RowTest {

    @Test
    public void test1(){
        Class[] c1 = new Class[] {Integer.class, String.class};
        Row r = new Row(c1);
        
        assertArrayEquals(c1, r.getSchema());
        
        assertTrue(r.setValue(0, 23));
        assertTrue(r.setValue(1, "str"));
        
        assertEquals(r.getValue(0), 23);
        assertEquals(r.getValue(1), "str");
    }
    
}
