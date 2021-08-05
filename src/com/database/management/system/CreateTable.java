package com.database.management.system;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateTable implements ExecutionData {
	String database;
	String query;
	String tableName;
	String primaryKey;
	public final List<String> columnNames = new ArrayList<String>();
	public final List<String> columnDataTypes = new ArrayList<String>();
	public final List<String> columnLength = new ArrayList<String>();

	public CreateTable(String database, String query) {
		this.database = database;
		this.query = query;
	}

	@Override
	public void execute() throws FileNotFoundException, IOException {
		if (validateQuery(query)) {
			createTable();
		}
	}

	@Override
	public boolean validateQuery(String query) throws FileNotFoundException {
		String regexCreate = "CREATE TABLE\\s(\\w+)[(]((((\\w+)\\s(varchar[(]\\d+[)]|int)*\\s*(?:NOT\\sNULL)?)(,)*\\s*)+)(?:,\\sPRIMARY KEY[(](\\w+)[)])?[)];";
		Pattern syntaxExp = Pattern.compile(regexCreate, Pattern.CASE_INSENSITIVE);
		Matcher queryParts = syntaxExp.matcher(query);

		if (queryParts.find()) {
			tableName = queryParts.group(1);
			String columns = queryParts.group(2).toLowerCase();
			primaryKey = queryParts.group(8);

			if (tableExists(tableName)) {
				System.out.println("Table already exist");
			} else {
				String[] columnList = columns.split(", ");
				for (String col : columnList) {
					String[] temp = col.split(" ");
					columnNames.add(temp[0]);
					if (temp[1].contains("(")) {
						String[] dataType = temp[1].split("\\(");
						columnDataTypes.add(dataType[0]);
						String length = dataType[1].replaceAll("\\)", "");
						columnLength.add(length);
					} else {
						columnDataTypes.add(temp[1]);
						columnLength.add("255");
					}
				}
			}
			return true;
		}
		return false;
	}

	public boolean tableExists(String tableName) {
		File f = new File("database/" + database + "/" + tableName + "_structure.txt");
		if (f.exists()) {
			return true;
		}
		return false;
	}

	public void createTable() throws IOException {
		String columnName = "ColumnName=";
		String columnType = "ColumnType=";
		String colLength = "ColumnLength=";
		String key = "PrimaryKey=";
		String rest = "ForeignKey=false#ReferencedTable=null";
		String hash = "#";

		int length = columnNames.size();
		List<String> rows = new ArrayList<String>();
		for (int i = 0; i < length; i++) {
			String row = "";
			row = columnName + columnNames.get(i) + hash + columnType + columnDataTypes.get(i) + hash + colLength
					+ columnLength.get(i) + hash + key + "false" + hash + rest;
			rows.add(row);
		}
		if (primaryKey != null) {
			String row = columnName + columnNames.get(0) + hash + columnType + columnDataTypes.get(0) + hash + colLength
					+ columnLength.get(0) + hash + key + "true" + hash + rest;
			rows.set(0, row);
		}

		File myObj = new File("database/" + database + "/" + tableName + "_structure.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(myObj));
		for (String s : rows) {
			writer.append(s);
			writer.append("\n");
		}
		writer.close();
		
		myObj = new File("database/" + database + "/" + tableName + "_values.txt");
		myObj.createNewFile();
	}

}
