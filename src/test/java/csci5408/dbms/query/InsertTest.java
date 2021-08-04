package csci5408.dbms.query;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import csci5408.dbms.tables.Row;
import csci5408.dbms.tables.TableManager;
import csci5408.dbms.users.Transaction;
import csci5408.dbms.users.UserManager;

public class InsertTest {

    TableManager tm;
    UserManager um;
    CreateTable ct;
    Insert insert;
    Transaction t;

    @Before
    public void setUp() {
        tm = new TableManager();
        um = new UserManager();
        ct = new CreateTable(tm);
        t = um.createUser("test7", "test7").getSession("test7").getTransaction();
        insert = new Insert(tm, t);
        assertTrue(ct.execute("create table test7 (a int, b varchar);", System.out));
    }

    @Test
    public void test1() {
        assertTrue(insert.validateQueryFormat("insert into test7 values (45, 'test string');"));
    }

    @Test
    public void test2() {
        assertEquals(insert.getTableName("insert into test7 values (45, 'test string');"), "test7");
    }

    @Test
    public void test3() {
        Class[] types = new Class[] { Integer.class, String.class };
        Object[] values = new Object[] { Integer.parseInt("45"), "test string" };

        Entry<Class[], Object[]> e = insert.getData("insert into test7 values (45, 'test string');");

        assertArrayEquals(types, e.getKey());
        assertArrayEquals(values, e.getValue());
    }

    @Test
    public void test4() {
        Class[] types = new Class[] { Integer.class, String.class };
        Object[] values = new Object[] { Integer.parseInt("45"), "test string" };

        assertTrue(insert.execute("insert into test7 values (45, 'test string');", System.out));
        assertEquals(1, tm.getTable("test7", t).getAllRow().size());
        Row row = tm.getTable("test7", t).getAllRow().get(0);
        assertArrayEquals(types, row.getSchema());
        assertArrayEquals(values, row.getValues());
    }

    @After
    public void end() throws IOException {
        File file = new File("data/table_space/test7");
        boolean result = Files.deleteIfExists(file.toPath());
        file = new File("data/users");
        result = Files.deleteIfExists(file.toPath());
    }

}
