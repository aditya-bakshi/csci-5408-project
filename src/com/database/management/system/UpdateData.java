package com.database.management.system;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateData implements ExecutionData {
	String database;
	String query;
	String tableName = "";
	String whereColumn;
	String whereValue;
	List<String> setColumnNames = new ArrayList<String>();
	List<String> setColumnValues = new ArrayList<String>();
	public final List<String> tableColumnNames = new ArrayList<String>();
	public final List<String> tableColumnType = new ArrayList<String>();
	public final Map<String, ArrayList<String>> allData = new HashMap<>();
	int numberOfRows;

	public UpdateData(String database, String query) {
		this.database = database;
		this.query = query;
	}

	@Override
	public void execute() throws IOException {
		if (validateQuery(query)) {
			getQueryElements(query);
			validateColumns(tableName, whereColumn);
			fetchTableStructure(tableName);
			validateSetValues();
			readTableData(tableName);
			updateData();
			updateTable(tableName);
		}
	}

	@Override
	public boolean validateQuery(String query) {
		String regexUpdate = "INSERT INTO (\\w+)(\\((?:\\w+)(?:, \\w+)*\\))? VALUES (\\((?:(?:\"(\\w+)\"|\\d+))(?:, (?:\"(\\w+)\"|\\d+))*\\));";
		boolean isItMatchingForInsert = Pattern.compile(regexUpdate, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE)
				.matcher(query).matches();
//		return isItMatchingForInsert;
		return true;
	}

	public void getQueryElements(String query) {
		String[] querySplit = query.split(" ");

		// get table name
		tableName = querySplit[1];

		String[] whereCondition = querySplit[querySplit.length - 1].split("=");
		whereColumn = whereCondition[0].trim().toLowerCase();
		whereValue = whereCondition[1].trim().replace(";", "");

		// check if table exists or if where column exists
		try {
			if (!validateColumns(tableName, whereColumn)) {
				throw new RuntimeException();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Table doesn't exist");
			DBController obj = new DBController();
			obj.run();
		} catch (RuntimeException e) {
			System.out.println("Column specified in where doesn't exist");
			DBController obj = new DBController();
			obj.run();
		}

		// get set columns and values
		Pattern pattern = Pattern.compile("set(.*)where", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(query);
		String[] setColumns = new String[0];
		while (matcher.find()) {
			setColumns = matcher.group(1).trim().split(",");
		}
		for (int i = 0; i < setColumns.length; i++) {
			String[] columnValue = setColumns[i].split("=");
			setColumnNames.add(columnValue[0].trim().toLowerCase());
			setColumnValues.add(columnValue[1].trim());
		}
	}

	public boolean validateColumns(String tableName, String whereColumns) throws FileNotFoundException {
		Map<Integer, String> columns = getColumns(tableName);
		if (!columns.containsValue(whereColumns.toLowerCase())) {
			return false;
		}
		return true;
	}

	public Map<Integer, String> getColumns(String tableName) throws FileNotFoundException {
		File tableFile = new File("database/" + database + "/" + tableName + "_structure.txt");
		Scanner sc = new Scanner(tableFile);
		Map<Integer, String> tableColumns = new TreeMap<Integer, String>();
		int counter = 0;
		while (sc.hasNextLine()) {
			String data = sc.nextLine();
			String[] colValues = data.split("#");
			String[] keyValue = colValues[0].split("=");
			tableColumns.put(counter, keyValue[1].toLowerCase());
			counter++;
		}
		return tableColumns;
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
			tableColumnType.add(colType[1]);
		}
	}

	public void validateSetValues() {
		try {
			if (setColumnNames.contains(tableColumnNames.get(0))) {
				throw new RuntimeException();
			}
		} catch (RuntimeException e) {
			System.out.println("Cannot update key value");
			DBController obj = new DBController();
			obj.run();
		}

		try {
			for (int i = 0; i < setColumnNames.size(); i++) {
				int index = tableColumnNames.indexOf(setColumnNames.get(i));
				String dataType = tableColumnType.get(index);
				if (dataType.equals("Int")) {
					Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
					boolean isInt = pattern.matcher(setColumnValues.get(i)).matches();
					if (!isInt) {
						throw new NumberFormatException();
					}
				}
			}
		} catch (NumberFormatException e) {
			System.out.println("Enter data with correct data type");
			DBController obj = new DBController();
			obj.run();
		}
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

	public void updateData() {
		ArrayList<String> key = allData.get(whereColumn);
		int index = key.indexOf(whereValue);
		numberOfRows = key.size();

		for (int i = 0; i < setColumnNames.size(); i++) {
			String column = setColumnNames.get(i);
			String value = setColumnValues.get(i);
			ArrayList<String> mapValue = allData.get(column);
			mapValue.set(index, value);
			allData.put(column, mapValue);
		}
	}

	public void updateTable(String tableName) throws IOException {
		List<String> rows = new ArrayList<String>();
		String row = "";
		for (int i = 0; i < numberOfRows; i++) {
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
		File myObj = new File("database/" + database + "/" + tableName + "_values.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(myObj));
		for (String r : rows) {
			writer.append(r);
			writer.append("\n");
		}
		writer.close();
	}
}
