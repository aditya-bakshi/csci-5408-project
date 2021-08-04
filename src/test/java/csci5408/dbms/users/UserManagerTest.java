package csci5408.dbms.users;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Test;

import csci5408.dbms.users.UserManager;

public class UserManagerTest {

    @Test
    public void test(){
        UserManager userManager = new UserManager();
        userManager.createUser("Test User", "TestPass");
        assertNotNull(userManager.getUser("Test User"));
        assertNull(userManager.getUser("Unknown User"));
    }
    
    @After
    public void end() throws IOException{
        File file = new File("data/users");
        boolean result = Files.deleteIfExists(file.toPath());
    }
}
