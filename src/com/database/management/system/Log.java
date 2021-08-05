package com.database.management.system;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author Aditya
 *
 */
public class Log {
	static File myObj = new File("logs/logs.txt");
	static BufferedWriter writer;

	public void writeLog(String string) throws IOException {
		writer = new BufferedWriter(new FileWriter(myObj, true));
		if (myObj.length() != 0) {
			writer.append("\n");
		}
		writer.append(string);
		writer.close();
	}
}
