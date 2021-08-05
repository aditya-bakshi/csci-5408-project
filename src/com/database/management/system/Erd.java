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

public class Erd implements ExecutionData{
    private static final String DB_SPACE_PATH = "database";
    String query;

    public Erd(String query){
        this.query = query;
    }

    @Override
    public void execute() throws FileNotFoundException, IOException {
        // TODO Auto-generated method stub
        // String file = query.replace("dump ", "").strip();
        // PrintStream ps = new PrintStream(new File(file));
        for (String database: getDatabases(DB_SPACE_PATH)){
            for (String table: getTables(DB_SPACE_PATH, database)){
                dumpTableStructure(System.out, database, table);
            }
        }
    }

    @Override
    public boolean validateQuery(String query) throws FileNotFoundException {
        // TODO Auto-generated method stub
        return false;
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
            + (ar[3].replace(key, "").strip().equals("true")?"PK(true)": "PK(false)")
            + (ar[4].replace("ForeignKey=", "").strip().equals("true")?" Ref("+ar[5].replace("ReferencedTable=", "").strip()+")": "");
            cols.add(col);
        }
        String l = db + "." + table + "(" + String.join(",", cols) + ");";
        out.println(l);
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
