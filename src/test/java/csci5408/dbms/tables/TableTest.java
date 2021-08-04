package csci5408.dbms.tables;

import static org.junit.Assert.*;

import org.junit.*;

import csci5408.dbms.tables.Row;
import csci5408.dbms.tables.Table;
import csci5408.dbms.users.Session;
import csci5408.dbms.users.Transaction;
import csci5408.dbms.users.User;

public class TableTest {

    @Test
    public void test1(){
        Class[] c1 = new Class[] {Integer.class, String.class};
        Class[] c2 = new Class[] {Integer.class, String.class};
        Table tab = new Table("Test", new String[]{"a", "b"},c1);
        Row r = new Row(c2);
        assertTrue(tab.validateSchema(r));
    }

    @Test
    public void test2(){
        Class[] c1 = new Class[] {Integer.class, String.class};
        Class[] c2 = new Class[] {Integer.class, String.class};
        Table tab = new Table("Test", new String[]{"a", "b"}, c1);
        Row r = new Row(c2);
        assertTrue(tab.validateSchema(r));
    }
}
