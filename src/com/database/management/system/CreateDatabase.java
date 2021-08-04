package com.database.management.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CreateDatabase implements ExecutionData {
	static String database;

	@Override
	public void execute(String query) throws FileNotFoundException {
		if (validateQuery(query)) {
			File theDir = new File("database/" + database);
			if (!theDir.exists()){
			    theDir.mkdirs();
			} else {
				throw new RuntimeException("Error");
			}
		}

	}

	@Override
	public boolean validateQuery(String query) throws FileNotFoundException {
		String[] elements = query.split(" ");
		if (elements.length != 3) {
			return false;
		}
		if (!elements[0].equalsIgnoreCase("create")) {
			return false;
		}
		if (!elements[1].equalsIgnoreCase("database")) {
			return false;
		}
		database = elements[2].toLowerCase();
		if (database.contains(";")) {
			database = database.replaceAll(";", "");
		}
		return true;
	}

}
