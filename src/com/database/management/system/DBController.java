package com.database.management.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class DBController {
	static Scanner sc = new Scanner(System.in);

	public void run() throws FileNotFoundException, IOException {
		Scanner sc = new Scanner(System.in);
		String opNumber = sc.nextLine();
		chooseOperation(opNumber.toLowerCase(), sc);
	}

	private void chooseOperation(String opNumber, Scanner sc) throws FileNotFoundException, IOException {
//		Scanner ssc = new Scanner(System.in);
//		String query = ssc.nextLine();
		String query = opNumber;
		System.out.println(query);
		ExecutionData executionData = null;
		if (opNumber.contains("select")) {
			executionData = new SelectData();
		} else if (opNumber.contains("create table")) {
			executionData = new CreateTable();
		} else if (opNumber.contains("insert")) {
			executionData = new InsertData();
		} else if (opNumber.contains("update")) {
			executionData = new UpdateData();
		} else if (opNumber.contains("alter")) {
			executionData = new AlterData();
		} else if (opNumber.contains("delete")) {
			executionData = new DeleteData();
		} else if (opNumber.equals("exit")) {
			System.exit(0);
		} else {
			System.out.println("Wrong input provided");
		}
		if (executionData != null) {
			executionData.execute(query);
			run();
		}

	}
}
