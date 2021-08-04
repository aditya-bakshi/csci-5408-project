package csci5408.dbms.query;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

public class UpdateTest {

    TableManager tm;
    UserManager um;
    CreateTable ct;
    Insert insert;
    Update update;
    Transaction t;

    @Before
    public void setUp() {
        tm = new TableManager();
        um = new UserManager();
        ct = new CreateTable(tm);
        t = um.createUser("test10", "test10").getSession("test10").getTransaction();
        insert = new Insert(tm, t);
        update = new Update(tm, t);
        assertTrue(ct.execute("create table test10 (a int, b varchar);", System.out));
        assertTrue(insert.execute("insert into test10 values (1, 'test string 1');", System.out));
        assertTrue(insert.execute("insert into test10 values (2, 'test string 2');", System.out));
        assertTrue(insert.execute("insert into test10 values (3, 'test string 3');", System.out));
    }

    @Test
    public void test1() {
        assertTrue(update.validateQueryFormat("update test10 set b='update' where a=1;"));
    }

    @Test
    public void test2() {
        assertEquals(update.getTableName("update test10 set b='update' where a=1;"), "test10");
    }

    @Test
    public void test3() {
        Entry<String, Object> ec = update.getCondition("update test10 set b='update' where a=1;");

        assertEquals("a", ec.getKey());
        assertEquals(Integer.valueOf(1), ec.getValue());
        
        Entry<String, Object> es = update.getSet("update test10 set b='update' where a=1;");

        assertEquals("b", es.getKey());
        assertEquals("update", es.getValue());
    }

    @Test
    public void test4() {
        assertEquals(3, tm.getTable("test10", t).getAllRow().size());
        assertTrue(update.execute("update test10 set b='update' where a=1;", System.out));
        assertEquals(3, tm.getTable("test10", t).getAllRow().size());
    }

    // public void test5() {
    //     assertTrue(update.validateQueryFormat("update from test10;"));
    // }

    // @Test
    // public void test6() {
    //     assertEquals("test10", update.getTableName("update from test10;"));
    // }

    
    // @Test
    // public void test7() {
    //     Entry<String, Object> e = update.getCondition("update from test10;");

    //     assertNull(e);
    //     // assertEquals(Integer.valueOf(1), e.getValue());
    // }

    // @Test
    // public void test8() {
    //     assertEquals(3, tm.getTable("test10", t).getAllRow().size());
    //     assertTrue(update.execute("update from test10;", System.out));
    //     assertEquals(0, tm.getTable("test10", t).getAllRow().size());
    // }

    @After
    public void end() throws IOException {
        File file = new File("data/table_space/test10");
        boolean result = Files.deleteIfExists(file.toPath());
        file = new File("data/users");
        result = Files.deleteIfExists(file.toPath());
    }

}
