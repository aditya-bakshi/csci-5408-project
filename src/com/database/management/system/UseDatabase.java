package com.database.management.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UseDatabase {
	static String database;

	public String execute(String query) throws FileNotFoundException, IOException {
		if (!validateQuery(query)) {
			throw new RuntimeException("Invalid query");
		}
		return database;
	}

	public boolean validateQuery(String query) throws FileNotFoundException {
		String[] elements = query.split(" ");

		if (elements.length != 2) {
			return false;
		}

		if (!elements[0].contains("use")) {
			return false;
		}

		database = elements[1];
		if (database.contains(";")) {
			database = database.replaceAll(";", "");
		}

		File file = new File("database/" + database);
		if (!file.exists()) {
			return false;
		}

		return true;
	}

}
