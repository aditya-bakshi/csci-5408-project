package csci5408.dbms.users;

import static org.junit.Assert.*;

import org.junit.*;

import csci5408.dbms.users.Session;
import csci5408.dbms.users.Transaction;
import csci5408.dbms.users.User;

public class SessionTest {

    @Test
    public void test(){
        User user = new User("Test User", "TestPass");
        Session session = user.getSession("TestPass");
        assertNotNull(session.getTransaction());
        assertEquals(session.getTransaction(), session.getTransaction());
        Transaction t = session.getTransaction();
        t.commit();
        // assertNotEquals(t, session.getTransaction());
        assertFalse(session.getTransaction().isClosed());
        assertEquals(session.getUser(), user);
    }
    
}
