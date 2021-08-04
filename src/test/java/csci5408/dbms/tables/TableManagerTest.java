package csci5408.dbms.tables;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Test;

import csci5408.dbms.users.Transaction;
import csci5408.dbms.users.User;

public class TableManagerTest {

    @Test
    public void test1() {
        TableManager tm = new TableManager();

        Transaction t = new User("aa", "aa").getSession("aa").getTransaction();

        Class[] c1 = new Class[] { Integer.class, String.class };
        tm.createTable("test", new String[]{"a", "b"}, c1);
        assertNotNull(t);
        assertFalse(tm.isLocked("test"));
        assertTrue(tm.lock("test", t));

        Table tab = tm.getTable("test", t);

        assertNotNull(tab);
        assertNotNull(tm.getTableNames());
        // assertEquals(tm.getTableNames().size(), 1);
        assertTrue(tm.getTableNames().contains("test"));
    }

    @Test
    public void test2() {
        TableManager tm = new TableManager();

        Transaction t = new User("aa", "aa").getSession("aa").getTransaction();

        Class[] c1 = new Class[] { Integer.class, String.class };
        tm.createTable("test2", new String[]{"a", "b"}, c1);

        assertNotNull(tm.getTable("test2", t));
        assertTrue(tm.isLocked("test2"));

        assertNotNull(tm.getLock("test2"));
        assertEquals(tm.getLock("test2"), t);

        assertTrue(tm.unlock("test2", t));
        assertFalse(tm.isLocked("test2"));
    }

    @Test
    public void test3() {
        TableManager tm = new TableManager();

        Transaction t = new User("aa", "aa").getSession("aa").getTransaction();

        Class[] c1 = new Class[] { Integer.class, String.class };
        tm.createTable("test3", new String[]{"a", "b"}, c1);
        Table tab = tm.getTable("test3", t);
        assertNotNull(tab);

        Row row = new Row(c1, new Object[] { 23, "Test String" });
        assertTrue(tab.insertRow(row));
        assertEquals(tab.getAllRow().size(), 1);
        assertArrayEquals(tab.getAllRow().get(0).getSchema(), c1);
        assertEquals(23, tab.getAllRow().get(0).getValue(0));
        assertEquals("Test String", tab.getAllRow().get(0).getValue(1));
        
        tab = tm.getTable("test3", t);
        assertEquals(tab.getAllRow().size(), 1);
        assertArrayEquals(tab.getAllRow().get(0).getSchema(), c1);
        assertEquals(23, tab.getAllRow().get(0).getValue(0));
        assertEquals("Test String", tab.getAllRow().get(0).getValue(1));
        
        assertTrue(tm.commit("test3", t));

        tab = tm.getTable("test3", t);
        assertEquals(tab.getAllRow().size(), 1);
        assertArrayEquals(tab.getAllRow().get(0).getSchema(), c1);
        assertEquals(23, tab.getAllRow().get(0).getValue(0));
        assertEquals(tab.getAllRow().get(0).getValue(1), "Test String");
    }

    @Test
    public void test4() {
        TableManager tm = new TableManager();

        Transaction t1 = new User("aa", "aa").getSession("aa").getTransaction();
        Transaction t2 = new User("aaa", "aaa").getSession("aaa").getTransaction();

        assertTrue(tm.lock("test4", t1));
        assertTrue(tm.lock("test4", t1));
        assertFalse(tm.lock("test4", t2));
        assertTrue(tm.lock("test4", t1));
    }

    @After
    public void end() throws IOException{
        File file = new File("data/table_space/test");
        boolean result = Files.deleteIfExists(file.toPath());
        file = new File("data/table_space/test2");
        result = Files.deleteIfExists(file.toPath());
        file = new File("data/table_space/test3");
        result = Files.deleteIfExists(file.toPath());
        file = new File("data/table_space/test4");
        result = Files.deleteIfExists(file.toPath());
    }
}
