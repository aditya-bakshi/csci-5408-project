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

public class SelectTest {

    TableManager tm;
    UserManager um;
    CreateTable ct;
    Insert insert;
    Select select;
    Transaction t;

    @Before
    public void setUp() {
        tm = new TableManager();
        um = new UserManager();
        ct = new CreateTable(tm);
        t = um.createUser("test8", "test8").getSession("test8").getTransaction();
        insert = new Insert(tm, t);
        select = new Select(tm, t);
        assertTrue(ct.execute("create table test8 (a int, b varchar);", System.out));
        assertTrue(insert.execute("insert into test8 values (45, 'test string');", System.out));
    }

    @Test
    public void test1() {
        assertTrue(select.validateQueryFormat("select a, b from test8;"));
    }

    @Test
    public void test2() {
        assertEquals(select.getTableName("select a, b from test8;"), "test8");
    }

    @Test
    public void test3() {
        String[] cols = new String[] {"a", "b"};

        assertArrayEquals(cols, select.getCols("select a, b from test8;"));
    }

    @After
    public void end() throws IOException {
        File file = new File("data/table_space/test8");
        boolean result = Files.deleteIfExists(file.toPath());
        file = new File("data/users");
        result = Files.deleteIfExists(file.toPath());
    }

}
