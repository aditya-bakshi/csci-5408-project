package com.database.management.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class DBController {
	static Scanner sc = new Scanner(System.in);
	static boolean isLoggedIn = false;
	static String database;
	ExecutionData executionData = null;

	public void run() {
		try {
			if (!isLoggedIn) {
				Login login = new Login();
				System.out.println("**************************");
				System.out.println("Welcome to Dalhousie DBMS");
				System.out.println("**************************");
				System.out.println("Enter your username or type EXIT: ");
				Scanner scan = new Scanner(System.in);
				String username = scan.next();

				if (username.equalsIgnoreCase("exit")) {
					System.exit(0);
				}

				System.out.println("Enter password");
				String password = scan.next();

				isLoggedIn = login.authenticate(username, password);

				if (isLoggedIn) {
					Scanner sc = new Scanner(System.in);
					System.out.println("Create or Select Database:");
					String query = sc.nextLine().toLowerCase();

					if (query.contains("create")) {
						executionData = new CreateDatabase();
						executionData.execute(query);
						query = sc.nextLine().toLowerCase();
					}
					UseDatabase obj = new UseDatabase();
					database = obj.execute(query);

				}
			}

			if (isLoggedIn) {
				String opNumber = sc.nextLine();
				chooseOperation(opNumber.toLowerCase(), sc);
			} else {
				run();
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			run();
		}
	}

	private void chooseOperation(String opNumber, Scanner sc) {
		try {
//		Scanner ssc = new Scanner(System.in);
//		String query = ssc.nextLine();
			String query = opNumber;
			System.out.println(query);
//		ExecutionData executionData = null;
			if (opNumber.contains("select")) {
				executionData = new SelectData(database);
			} else if (opNumber.contains("create table")) {
				executionData = new CreateTable(database);
			} else if (opNumber.contains("create database")) {
				executionData = new CreateDatabase();
			} else if (opNumber.contains("insert")) {
				executionData = new InsertData(database);
			} else if (opNumber.contains("update")) {
				executionData = new UpdateData(database);
			} else if (opNumber.contains("alter")) {
				executionData = new AlterData();
			} else if (opNumber.contains("delete")) {
				executionData = new DeleteData(database);
			} else if (opNumber.contains("dump")) {
				executionData = new Datadump();
			} else if (opNumber.equals("exit")) {
				System.exit(0);
			} else {
				System.out.println("Wrong input provided");
			}
			if (executionData != null) {
				executionData.execute(query);
				run();
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
			run();
		}
	}
}
