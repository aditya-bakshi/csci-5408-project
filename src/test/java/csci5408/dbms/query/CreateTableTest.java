package csci5408.dbms.query;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Test;

import csci5408.dbms.tables.Row;
import csci5408.dbms.tables.TableManager;
import csci5408.dbms.users.Transaction;
import csci5408.dbms.users.User;

public class CreateTableTest {

    @Test
    public void test1() {
        TableManager tm = new TableManager();
        CreateTable ct = new CreateTable(tm);
        assertTrue(ct.validateQueryFormat("create table test6 (a int, b varchar);"));
    }

    @Test
    public void test2() {
        TableManager tm = new TableManager();
        CreateTable ct = new CreateTable(tm);
        assertEquals(ct.getTableName("create table test6 (a int, b varchar);"), "test6");
    }

    @Test
    public void test3() {
        TableManager tm = new TableManager();
        CreateTable ct = new CreateTable(tm);
        String[] names = new String[] {"a", "b"};
        Class[] types = new Class[] {Integer.class, String.class};
        Entry<String[], Class[]> e = ct.getSchema("create table test6 (a int, b varchar);");
        assertArrayEquals(names, e.getKey());
        assertArrayEquals(types, e.getValue());
    }
    
    
    @Test
    public void test4() {
        TableManager tm = new TableManager();
        CreateTable ct = new CreateTable(tm);
        assertTrue(ct.execute("create table test6 (a int, b varchar);", System.out));
        assertTrue(tm.getTableNames().contains("test6"));
    }

    @After
    public void end() throws IOException{
        File file = new File("data/table_space/test6");
        boolean result = Files.deleteIfExists(file.toPath());
    }

}
