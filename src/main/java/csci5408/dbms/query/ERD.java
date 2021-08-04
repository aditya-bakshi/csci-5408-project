package csci5408.dbms.query;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import csci5408.dbms.tables.Row;
import csci5408.dbms.tables.Table;
import csci5408.dbms.tables.TableManager;
import csci5408.dbms.users.Transaction;

public class ERD {
    private TableManager tm;
    private Transaction t;

    public ERD(TableManager tm, Transaction t) {
        this.tm = tm;
        this.t = t;
    }

    public void execute(PrintStream out) {
        try {
            out.println("------------ERD------------");
            List<String> tables = tm.getTableNames();
            for (String table: tables) {
                Table tab = tm.getTable(table, t);
                String[] colNames = tab.getColNames();
                Class[] colTypes = tab.getSchema();
                out.print(tab.getName() + " (");
                for (int i=0; i<colNames.length; i++){
                    out.print(colNames[i]+ ": " + colTypes[i].getSimpleName() + "; ");
                }
                out.print(")");
                out.println();
            }
            out.println("---------------------------");
        } catch (Exception e) {
            return;
        }
    }
}
