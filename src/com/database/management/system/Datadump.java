package com.database.management.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Datadump implements ExecutionData{
    private static final String DB_SPACE_PATH = "database";

    @Override
    public void execute(String query) throws FileNotFoundException, IOException {
        // TODO Auto-generated method stub
        String file = query.replace("dump ", "").strip();
        PrintStream ps = new PrintStream(new File(file));
        dumpDatabases(ps);
        for (String database: getDatabases(DB_SPACE_PATH)){
            for (String table: getTables(DB_SPACE_PATH, database)){
                System.out.println(table);
                dumpTable(ps, database, table);
            }
        }
    }

    @Override
    public boolean validateQuery(String query) throws FileNotFoundException {
        // TODO Auto-generated method stub
        return false;
    }

    private void dumpDatabases(PrintStream out) {
        for (String d: getFolderNames(DB_SPACE_PATH)){
            out.println("Create database "+d+";");
        }
    }

    private void dumpTable(PrintStream out, String db, String table) throws IOException{
        dumpTableStructure(out, db, table);
        dumpTableData(out, db, table);
    }

    private void dumpTableStructure(PrintStream out, String db, String table) throws IOException{
        String path = DB_SPACE_PATH + File.separator + db + File.separator + table + "_structure.txt";
        List<String> lines = Files.readAllLines(Paths.get(path));
        
        String columnName = "ColumnName=";
		String columnType = "ColumnType=";
		String colLength = "ColumnLength=";
		String key = "PrimaryKey=";
		String rest = "ForeignKey=false#ReferencedTable=null";
		String hash = "#";
        
        List<String> cols = new ArrayList<>();
        for (String line: lines){
            String[] ar=line.split(hash);
            String col = ar[0].replace(columnName, "").strip() + " "
            + ar[1].replace(columnType, "").strip() + "(" + ar[2].replace(colLength, "").strip()+ ") "
            + (ar[3].replace(key, "").strip().equals("true")?"primary key":"");
            cols.add(col);
        }
        String l = "Create table " + table + "(" + String.join(",", cols) + ");";
        out.println(l);
    }
    
    private void dumpTableData(PrintStream out, String db, String table) throws IOException{
        String path = DB_SPACE_PATH + File.separator + db + File.separator + table + "_values.txt";
        List<String> lines = Files.readAllLines(Paths.get(path));
        for (String l: lines){
            out.println("Insert into " + table + " values (" + String.join(",", l.split("-")) + ");");
        }
    }
    
    private List<String> getDatabases(String path){
        return getFolderNames(path);
    }

    private List<String> getTables(String path, String database){
        return getFileNames(path+File.separator+database).stream().map(x->x.split("_")[0]).distinct().collect(Collectors.toList());
    }

    private List<String> getFolderNames(String path) {
        File folder = new File(path);
        List<String> files = Arrays.stream(folder.listFiles()).filter(x -> x.isDirectory()).map(x -> x.getName())
                .collect(Collectors.toList());
        return files;
    }

    private List<String> getFileNames(String path) {
        File folder = new File(path);
        List<String> files = Arrays.stream(folder.listFiles()).filter(x -> x.isFile()).map(x -> x.getName())
                .collect(Collectors.toList());
        return files;
    }
}
