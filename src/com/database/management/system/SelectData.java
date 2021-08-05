package com.database.management.system;

import java.io.File;
import java.io.FileNotFoundException;
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

public class SelectData implements ExecutionData {
	
	String database;
	String query;
	public String tableName = "";
	public final List<String> whereColumns = new ArrayList<String>();
	public final List<String> selectColumns = new ArrayList<String>();
	public final Map<Integer, String> tableColumns = new TreeMap<Integer, String>();
	public final Map<String, String> whereColumnValues = new HashMap<String, String>();
	public final Map<String, ArrayList<String>> allData = new HashMap<>();
	public final Map<String, ArrayList<String>> data = new HashMap<>();
	public int numberOfRecords = 0;
	public boolean isWhere = false;

	public SelectData(String database, String query) {
		this.database = database;
		this.query = query;
	}

	@Override
	public void execute() throws IOException {
		boolean isQueryValid = validateQuery(query);
		if (isQueryValid) {
			getQueryColumns(query);
			readFile(tableName);
			writeOutput();
		}
	}

	@Override
	public boolean validateQuery(String query) throws FileNotFoundException {
//		String regexSelect = "^select\\s(?:\\*|\\w+)\\sfrom\\s\\w+;?\\s*$";
		String regexSelect = "SELECT\\s((?:\\*)|(?:(?:\\w+)(?:,\\s?\\w+)*))\\sFROM\\s(\\w+)(=|<=|>=|>|<|!=);?$";
		boolean isItMatchingForSelect = Pattern.compile(regexSelect, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE)
				.matcher(query).matches();
//		if (!isItMatchingForSelect) {
//			return false;
//		}
		return getTableName(query);
//		return true;
	}

	private boolean getTableName(String query) throws FileNotFoundException {
		String[] queryString = query.split(" ");

		// Get table name
		if (query.contains("where")) {
			Pattern patternTable = Pattern.compile("from(.*)where", Pattern.CASE_INSENSITIVE);
			Matcher matcherTable = patternTable.matcher(query);
			while (matcherTable.find()) {
				tableName = matcherTable.group(1).trim();
			}

			String[] whereColumns = queryString[queryString.length - 1].split("=");
			String column = whereColumns[0].trim();
			String value = whereColumns[1].trim();
			value = value.replace(";", "");

			this.whereColumns.add(column);
			this.whereColumnValues.put(column, value);

			isWhere = true;

			if (!validateColumns(tableName, this.whereColumns)) {
				return false;
			}
		} else {
			tableName = queryString[queryString.length - 1];
			if (tableName.contains(";")) {
				tableName = tableName.replace(";", "");
			}
			getColumns(tableName);
		}
		return true;
	}

	public boolean validateColumns(String tableName, List<String> whereColumns) throws FileNotFoundException {
		Map<Integer, String> columns = getColumns(tableName);
		for (int i = 0; i < whereColumns.size(); i++) {
			if (!columns.containsValue(whereColumns.get(i).toLowerCase())) {
				return false;
			}
		}
		return true;
	}

	public Map<Integer, String> getColumns(String tableName) throws FileNotFoundException {
		File tableFile = new File("database/" + database + "/" + tableName + "_structure.txt");
		Scanner sc = new Scanner(tableFile);
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

	public void getQueryColumns(String query) {
		Pattern pattern = Pattern.compile("select(.*)from", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(query);
		String[] columns = new String[0];

		while (matcher.find()) {
			columns = matcher.group(1).split(",");
		}
		for (int i = 0; i < columns.length; i++) {
			selectColumns.add(columns[i].trim());
		}
	}

	public void readFile(String table) throws IOException {
		List<String> fileData = Files.readAllLines(Paths.get("database/" + database + "/" + tableName + "_values.txt"));
		Map<Integer, String> columns = getColumns(table);
		for (int i = 0; i < columns.size(); i++) {
			ArrayList<String> val = new ArrayList<>();
			for (String fileDatum : fileData) {
				String columnData = fileDatum.split("-")[i];
				val.add(columnData);
			}
			numberOfRecords = val.size();
			allData.put(columns.get(i), val);
		}
		if (isWhere) {
			filterData(allData);
		}
	}

	public void filterData(Map<String, ArrayList<String>> data) {
		List<Integer> indexListToBeRemoved = new ArrayList<>();
		for (Map.Entry<String, ArrayList<String>> entry : data.entrySet()) {
			if (entry.getKey().equals(whereColumns.get(0))) {
				int i = 0;
				for (String s : entry.getValue()) {
					if (!s.equals(whereColumnValues.get(whereColumns.get(0)))) {
						indexListToBeRemoved.add(i);
					}
					i++;
				}
				numberOfRecords = numberOfRecords - indexListToBeRemoved.size();
			}
		}
		for (Map.Entry<String, ArrayList<String>> entry : data.entrySet()) {
			for (int i = 0; i < indexListToBeRemoved.size(); i++) {
				int f = indexListToBeRemoved.get(i);
				if (i > 0) {
					f = f - i;
				}
				entry.getValue().remove(f);
			}
		}
	}

	public void writeOutput() {

		if (selectColumns.contains("*")) {
			for (Map.Entry<Integer, String> entry : tableColumns.entrySet()) {
				System.out.print(entry.getValue() + "\t\t\t");
			}
			System.out.println();
			for (int i = 0; i < numberOfRecords; i++) {
				for (Map.Entry<Integer, String> entry : tableColumns.entrySet()) {
					System.out.print(allData.get(entry.getValue()).get(i) + "\t\t\t");
				}
				System.out.println();
			}
		} else {
			for (Map.Entry<Integer, String> entry : tableColumns.entrySet()) {
				if (selectColumns.contains(entry.getValue())) {
					System.out.print(entry.getValue() + "\t\t\t");
				}
			}
			System.out.println();
			for (int i = 0; i < numberOfRecords; i++) {
				for (Map.Entry<Integer, String> entry : tableColumns.entrySet()) {
					if (selectColumns.contains(entry.getValue())) {
						System.out.print(allData.get(entry.getValue()).get(i) + "\t\t\t");
					}
				}
				System.out.println();
			}
		}
	}

}
