package com.database.management.system;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * 
 * @author Aditya
 *
 */
public class DeleteData implements ExecutionData {
	String database;
	String query;
	String tableName;
	String whereColumn;
	String whereValue;
	public final Map<String, ArrayList<String>> allData = new HashMap<>();
	int numberOfRows;
	public final List<String> tableColumnNames = new ArrayList<String>();
	boolean isWhere;
	
	Log log = new Log();

	public DeleteData(String database, String query) {
		this.database = database;
		this.query = query;
	}

	@Override
	public void execute() throws IOException {
		if (validateQuery(query)) {
			fetchTableStructure(tableName);
			readTableData(tableName);
			deleteData();
			updateTable(tableName);
		} else {
			throw new RuntimeException("Invalid query");
		}
	}

	@Override
	public boolean validateQuery(String query) throws FileNotFoundException {
		isWhere = false;
		
		String[] words = query.split(" ");
		if (words.length < 3) {
			return false;
		}

		if (!words[1].equalsIgnoreCase("from")) {
			return false;
		}


		// fetch table name
		tableName = words[2].toLowerCase();
		if (tableName.contains(";")) {
			tableName = tableName.replaceAll(";", "");
		}

		if (words.length > 3) {
				if (!words[3].equalsIgnoreCase("where")) {
					return false;
				}
				isWhere = true;
				String[] whereCondition = words[4].split("=");
				whereColumn = whereCondition[0].toLowerCase();
				whereValue = whereCondition[1].toLowerCase();

					if (!validateColumns(tableName, whereColumn)) {
						throw new RuntimeException("Column specified in where clause doesn't exist");
					}
		}
		return true;
	}

	public boolean validateColumns(String tableName, String whereColumns) throws FileNotFoundException {
		Map<Integer, String> columns = getColumns(tableName);
		if (!columns.containsValue(whereColumns.toLowerCase())) {
			return false;
		}
		return true;
	}

	public Map<Integer, String> getColumns(String tableName) {
		File tableFile = new File("database/" + database + "/" + tableName + "_structure.txt");
		Scanner sc;
		Map<Integer, String> tableColumns = new TreeMap<Integer, String>();
		try {
			sc = new Scanner(tableFile);
			int counter = 0;
			while (sc.hasNextLine()) {
				String data = sc.nextLine();
				String[] colValues = data.split("#");
				String[] keyValue = colValues[0].split("=");
				tableColumns.put(counter, keyValue[1].toLowerCase());
				counter++;
			}
		} catch (FileNotFoundException e) {
			System.out.println("Table doesn't exist");
			DBController obj = new DBController();
			obj.run();
		}
		return tableColumns;
	}

	public void readTableData(String tableName) throws IOException {
		List<String> fileData = Files.readAllLines(Paths.get("database/" + database + "/" + tableName + "_values.txt"));
		Map<Integer, String> columns = getColumns(tableName);
		for (int i = 0; i < columns.size(); i++) {
			ArrayList<String> val = new ArrayList<>();
			for (String fileDatum : fileData) {
				String columnData = fileDatum.split("-")[i];
				val.add(columnData);
			}
			int numberOfRecords = val.size();
			allData.put(columns.get(i), val);
		}
	}

	public void deleteData() {
		if (isWhere) {
			ArrayList<String> key = allData.get(whereColumn);
			int index = key.indexOf(whereValue);
			numberOfRows = key.size();

			for (Map.Entry<String, ArrayList<String>> entry : allData.entrySet()) {
				entry.getValue().remove(index);
			}
		}
	}

	public void fetchTableStructure(String tableName) throws FileNotFoundException {
		File tableFile = new File("database/" + database + "/" + tableName + "_structure.txt");
		Scanner sc;
		sc = new Scanner(tableFile);
		while (sc.hasNextLine()) {
			String data = sc.nextLine();
			String[] col = data.split("#");
			String[] colNames = col[0].split("=");
			String[] colType = col[1].split("=");

			tableColumnNames.add(colNames[1].toLowerCase());
		}
	}

	public void updateTable(String tableName) throws IOException {
		log.writeLog(String.valueOf(new Timestamp(System.currentTimeMillis())) + ": Deleting data from " + tableName);
		File myObj = new File("database/" + database + "/" + tableName + "_values.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(myObj));

		if (isWhere) {
			List<String> rows = new ArrayList<String>();
			String row = "";
			for (int i = 0; i < numberOfRows - 1; i++) {
				int j = 0;
				for (String columns : tableColumnNames) {
					row = row + allData.get(columns).get(i);
					if (j < tableColumnNames.size() - 1) {
						row = row + "-";
						j++;
					}
				}
				rows.add(row);
				row = "";
			}
			for (String r : rows) {
				writer.append(r);
				writer.append("\n");
			}
			writer.close();
		} else {
			writer.append("");
			writer.close();
		}
	}
}
