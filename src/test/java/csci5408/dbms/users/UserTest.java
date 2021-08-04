package csci5408.dbms.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.*;

import csci5408.dbms.users.User;
import csci5408.dbms.users.UserManager;

public class UserTest {

    private UserManager userManager;

    @Test
    public void test(){
        UserManager userManager = new UserManager();
        User user = userManager.createUser("Test User", "TestPass");
        assertNotNull(user);
        assertEquals("Test User", user.getUsername());
        assertNotNull(user.getSession("TestPass"));
    }

    @After
    public void end() throws IOException{
        File file = new File("data/users");
        boolean result = Files.deleteIfExists(file.toPath());
    }
}
