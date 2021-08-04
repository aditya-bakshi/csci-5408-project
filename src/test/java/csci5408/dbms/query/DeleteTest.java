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

public class DeleteTest {

    TableManager tm;
    UserManager um;
    CreateTable ct;
    Insert insert;
    Delete delete;
    Transaction t;

    @Before
    public void setUp() {
        tm = new TableManager();
        um = new UserManager();
        ct = new CreateTable(tm);
        t = um.createUser("test9", "test9").getSession("test9").getTransaction();
        insert = new Insert(tm, t);
        delete = new Delete(tm, t);
        assertTrue(ct.execute("create table test9 (a int, b varchar);", System.out));
        assertTrue(insert.execute("insert into test9 values (1, 'test string 1');", System.out));
        assertTrue(insert.execute("insert into test9 values (2, 'test string 2');", System.out));
        assertTrue(insert.execute("insert into test9 values (3, 'test string 3');", System.out));
    }

    @Test
    public void test1() {
        assertTrue(delete.validateQueryFormat("delete from test9 where a=1;"));
    }

    @Test
    public void test2() {
        assertEquals(delete.getTableName("delete from test9 where a=1;"), "test9");
    }

    @Test
    public void test3() {
        Entry<String, Object> e = delete.getCondition("delete from test9 where a=1;");

        assertEquals("a", e.getKey());
        assertEquals(Integer.valueOf(1), e.getValue());
    }

    @Test
    public void test4() {
        assertEquals(3, tm.getTable("test9", t).getAllRow().size());
        assertTrue(delete.execute("delete from test9 where a=1;", System.out));
        assertEquals(2, tm.getTable("test9", t).getAllRow().size());
    }

    // public void test5() {
    //     assertTrue(delete.validateQueryFormat("delete from test9;"));
    // }

    // @Test
    // public void test6() {
    //     assertEquals("test9", delete.getTableName("delete from test9;"));
    // }

    
    // @Test
    // public void test7() {
    //     Entry<String, Object> e = delete.getCondition("delete from test9;");

    //     assertNull(e);
    //     // assertEquals(Integer.valueOf(1), e.getValue());
    // }

    // @Test
    // public void test8() {
    //     assertEquals(3, tm.getTable("test9", t).getAllRow().size());
    //     assertTrue(delete.execute("delete from test9;", System.out));
    //     assertEquals(0, tm.getTable("test9", t).getAllRow().size());
    // }

    @After
    public void end() throws IOException {
        File file = new File("data/table_space/test9");
        boolean result = Files.deleteIfExists(file.toPath());
        file = new File("data/users");
        result = Files.deleteIfExists(file.toPath());
    }

}
