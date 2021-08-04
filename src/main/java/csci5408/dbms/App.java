package csci5408.dbms;

import java.io.PrintStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.plaf.synth.SynthTextAreaUI;

import csci5408.dbms.query.CreateTable;
import csci5408.dbms.query.Delete;
import csci5408.dbms.query.Dump;
import csci5408.dbms.query.ERD;
import csci5408.dbms.query.Insert;
import csci5408.dbms.query.Select;
import csci5408.dbms.query.Update;
import csci5408.dbms.tables.TableManager;
import csci5408.dbms.users.Session;
import csci5408.dbms.users.User;
import csci5408.dbms.users.UserManager;

public class App {
    private static UserManager um = new UserManager();
    private static TableManager tm = new TableManager();

    public static void main(String[] args) {
        System.out.println("Database Management System");
        Scanner sc = new Scanner(System.in);
        mainFlow(System.out, sc);
    }

    public static void mainFlow(PrintStream out, Scanner sc) {
        while (true) {
            out.println("-----------Main Menu------------");
            out.println("1. Register User");
            out.println("2. Login");
            out.println("0. Exit");
            switch (sc.nextInt()) {
                case 1:
                    registerUserFlow(out, sc);
                    break;
                case 2:
                    loginFlow(out, sc);
                    break;
                case 0:
                    return;
                default:
                    out.println("Invalid choice");
            }
        }
    }

    public static void registerUserFlow(PrintStream out, Scanner sc) {
        out.println("Enter Username");
        String userName = sc.next();
        out.println("Enter Password");
        String passWord = sc.next();
        User user = um.createUser(userName, passWord);
        if (user != null) {
            out.println("User created successfully");
        } else {
            out.println("Error while user creation");
        }
    }

    public static void loginFlow(PrintStream out, Scanner sc) {
        out.println("Enter Username");
        String userName = sc.next();
        User user = um.getUser(userName);
        if (user == null) {
            out.println("User not exists");
            return;
        }
        out.println("Enter Password");
        String passWord = sc.next();
        Session session = user.getSession(passWord);
        if (session == null) {
            out.println("Invalid Password");
            return;
        }
        userMainFlow(out, sc, session);
    }

    public static void userMainFlow(PrintStream out, Scanner sc, Session session) {
        while (true) {
            out.println("-----------Main Menu------------");
            out.println("1. Create Tables");
            out.println("2. List Tables");
            out.println("3. Select");
            out.println("4. Insert");
            out.println("5. Update");
            out.println("6. Delete");
            out.println("7. Commit");
            out.println("8. Rollback");
            out.println("9. Print ERD");
            out.println("10. Data Dump");
            out.println("0. Exit");
            switch (sc.nextInt()) {
                case 1:
                    createTableFlow(out, sc, session);
                    break;
                case 2:
                    listTableFlow(out, sc, session);
                    break;
                case 3:
                    SelectFlow(out, sc, session);
                    break;
                case 4:
                    InsertFlow(out, sc, session);
                    break;
                case 5:
                    UpdateFlow(out, sc, session);
                    break;
                case 6:
                    DeleteFlow(out, sc, session);
                    break;
                case 7:
                    commitFlow(out, sc, session);
                    break;
                case 8:
                    rollbackFlow(out, sc, session);
                    break;
                case 9:
                    erdFlow(out, sc, session);
                    break;
                case 10:
                    dumpFlow(out, sc, session);
                    break;
                case 0:
                    return;
                default:
                    out.println("Invalid choice");
            }
        }
    }

    public static void createTableFlow(PrintStream out, Scanner sc, Session session) {
        CreateTable ct = new CreateTable(tm);
        out.println("Enter Query:");
        String query = "";
        while (query.strip().equals("")) {
            query = sc.nextLine().strip();
        }
        if (ct.execute(query, out)) {
            out.println("Info: Table Created Successfully");
        }
    }

    public static void InsertFlow(PrintStream out, Scanner sc, Session session) {
        Insert insert = new Insert(tm, session.getTransaction());
        out.println("Enter Query:");
        String query = "";
        while (query.strip().equals("")) {
            query = sc.nextLine().strip();
        }
        if (insert.execute(query, out)) {
            out.println("Info: Insertion Successful");
        }
    }

    public static void UpdateFlow(PrintStream out, Scanner sc, Session session) {
        Update insert = new Update(tm, session.getTransaction());
        out.println("Enter Query:");
        String query = "";
        while (query.strip().equals("")) {
            query = sc.nextLine().strip();
        }
        if (insert.execute(query, out)) {
            out.println("Info: Updation Successful");
        }
    }

    public static void DeleteFlow(PrintStream out, Scanner sc, Session session) {
        Delete insert = new Delete(tm, session.getTransaction());
        out.println("Enter Query:");
        String query = "";
        while (query.strip().equals("")) {
            query = sc.nextLine().strip();
        }
        if (insert.execute(query, out)) {
            out.println("Info: Deletion Successful");
        }
    }

    public static void SelectFlow(PrintStream out, Scanner sc, Session session) {
        Select select = new Select(tm, session.getTransaction());
        out.println("Enter Query:");
        String query = "";
        while (query.strip().equals("")) {
            query = sc.nextLine().strip();
        }
        select.execute(query, out);
    }

    public static void listTableFlow(PrintStream out, Scanner sc, Session session) {
        out.println("List of tables:");
        for (String t : tm.getTableNames()) {
            out.println(t);
        }
    }
    public static void erdFlow(PrintStream out, Scanner sc, Session session) {
        ERD erd = new ERD(tm, session.getTransaction());
        erd.execute(out);
    }

    public static void dumpFlow(PrintStream out, Scanner sc, Session session) {
        Dump dump = new Dump(tm, session.getTransaction());
        dump.execute(out);
    }

    public static void commitFlow(PrintStream out, Scanner sc, Session session) {
        session.getTransaction().commit();
        out.println("Commit successful");
    }

    public static void rollbackFlow(PrintStream out, Scanner sc, Session session) {
        session.getTransaction().rollback();
        out.println("Rollback successful");
    }
}
