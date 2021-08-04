package com.database.management.system;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * 
 * @author Aditya
 *
 */
public class InsertData implements ExecutionData {
	static String database;
	public static String tableName;
	public final List<String> tableColumnNames = new ArrayList<String>();
	public final List<String> tableColumnType = new ArrayList<String>();
	public static String[] values = null;
	
	Log log = new Log();

	public InsertData(String database) {
		this.database = database;
	}

	@Override
	public void execute(String query) throws IOException {
		boolean isQueryValid = validateQuery(query);
		if (isQueryValid) {
			fetchValues(query);
			validateValues();
			checkKeyViolation();
			insertData();
		}
	}

	@Override
	public boolean validateQuery(String query) {
		String regexInsert = "(insert)\\s(into)\\s(\\w+)(\\s?)\\(.*";
		boolean isItMatchingForInsert = Pattern.compile(regexInsert, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE)
				.matcher(query).matches();
//		return isItMatchingForInsert;
		return true;
	}

	public void fetchValues(String query) {
		String[] tempList = query.split(" ");
		tableName = tempList[2];

		File tableFile = new File("database/" + database + "/" + tableName + "_structure.txt");
		Scanner sc;
		try {
			sc = new Scanner(tableFile);
			while (sc.hasNextLine()) {
				String data = sc.nextLine();
				String[] col = data.split("#");
				String[] colNames = col[0].split("=");
				String[] colType = col[1].split("=");

				tableColumnNames.add(colNames[1].toLowerCase());
				tableColumnType.add(colType[1]);
			}
			String[] valueString = query.split(".*(?=values)");

			String[] temp = valueString[2].split("\\(");
			String tempValues;
			for (int i = 1; i < temp.length; i++) {
				tempValues = temp[i];
				tempValues = tempValues.replaceAll("[)]", "");
				tempValues = tempValues.replaceAll("[;]", "");
				values = tempValues.split(",");
			}
		} catch (FileNotFoundException e) {
			System.out.println("Table doesn't exist");
			DBController obj = new DBController();
			obj.run();
		}
	}

	public void validateValues() {
		try {
			if (values.length != tableColumnNames.size()) {
				throw new Exception();
			}

			for (int i = 0; i < values.length; i++) {
				String dataType = tableColumnType.get(i);
				if (dataType.equals("Int")) {
					if (values[i] != null) {
						Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
						boolean isInt = pattern.matcher(values[i]).matches();
						if (!isInt) {
							throw new NumberFormatException();
						}
					}

				}
			}

		} catch (NumberFormatException e) {
			System.out.println("Enter data with correct data type");
			DBController obj = new DBController();
			obj.run();
		} catch (Exception e) {
			System.out.println("Enter the correct number of fields");
			DBController obj = new DBController();
			obj.run();
		}
	}

	public void checkKeyViolation() {
		String key = values[0];
		List<String> keyRows = new ArrayList<String>();
		File tableFile = new File("database/" + database + "/" + tableName + "_values.txt");
		Scanner sc;
		try {
			sc = new Scanner(tableFile);
			while (sc.hasNextLine()) {
				String data = sc.nextLine();
				String[] row = data.split("-");
				keyRows.add(row[0]);
			}
			if (keyRows.contains(key)) {
				throw new RuntimeException();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Table doesn't exist");
			DBController obj = new DBController();
			obj.run();
		} catch (RuntimeException e) {
			System.out.println("Row with given key already exists");
			DBController obj = new DBController();
			obj.run();
		}
	}

	public void insertData() throws IOException {
		log.writeLog(String.valueOf(new Timestamp(System.currentTimeMillis())) + ": Inserting row in " + tableName);
		File myObj = new File("database/" + database + "/" + tableName + "_values.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(myObj, true));
		if (myObj.length() != 0) {
			writer.append("\n");
		}
		String row = "";
		for (int i = 0; i < values.length; i++) {
			row = row + values[i];
			if (i < values.length - 1) {
				row = row + "-";
			}
		}
		writer.append(row);
		writer.close();
	}
}
