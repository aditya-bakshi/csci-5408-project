package com.database.management.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Scanner;

/**
 * 
 * @author Aditya
 *
 */
public class DBController {
	static Scanner sc = new Scanner(System.in);
	static boolean isLoggedIn = false;
	static String database;
	ExecutionData executionData = null;
	
	Log log = new Log();

	public void run() {
		try {
			if (!isLoggedIn) {
				Login login = new Login();
				System.out.println("**************************");
				System.out.println("Welcome to Dalhousie DBMS");
				System.out.println("**************************");
				System.out.println("Enter your username or type EXIT: ");
				Scanner scan = new Scanner(System.in);
				
				log.writeLog(String.valueOf(new Timestamp(System.currentTimeMillis())) + ": Login Page");
				
				String username = scan.next();

				if (username.equalsIgnoreCase("exit")) {
					log.writeLog(String.valueOf(new Timestamp(System.currentTimeMillis())) + ": Exiting");
					log.writeLog("*********************************************************");
					System.exit(0);
				}

				System.out.println("Enter password");
				String password = scan.next();

				log.writeLog(String.valueOf(new Timestamp(System.currentTimeMillis())) + ": Checking Credentials");
				isLoggedIn = login.authenticate(username, password);

				if (isLoggedIn) {
					log.writeLog(String.valueOf(new Timestamp(System.currentTimeMillis())) + ": Logged in");
					Scanner sc = new Scanner(System.in);
					System.out.println("Create or Select Database:");
					String query = sc.nextLine().toLowerCase();
					log.writeLog(String.valueOf(new Timestamp(System.currentTimeMillis())) + ": " + query);

					if (query.contains("create")) {
						log.writeLog(String.valueOf(new Timestamp(System.currentTimeMillis())) + ": Create Database");
						log.writeLog(String.valueOf(new Timestamp(System.currentTimeMillis())) + ": " + query);
						executionData = new CreateDatabase();
						executionData.execute(query);
						query = sc.nextLine().toLowerCase();
						log.writeLog(String.valueOf(new Timestamp(System.currentTimeMillis())) + ": " + query);
					}
					UseDatabase obj = new UseDatabase();
					database = obj.execute(query);

				}
			}

			if (isLoggedIn) {
				String opNumber = sc.nextLine();
				log.writeLog(String.valueOf(new Timestamp(System.currentTimeMillis())) + ": " + opNumber);
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
			} else if (opNumber.equals("exit")) {
				log.writeLog(String.valueOf(new Timestamp(System.currentTimeMillis())) + ": Exiting");
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
