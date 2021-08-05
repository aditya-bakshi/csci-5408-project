package com.database.management.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.util.Scanner;

/**
 * 
 * @author Aditya
 *
 */
public class DBController {
	Scanner sc = new Scanner(System.in);
	boolean isLoggedIn = false;
	String database;
	ExecutionData executionData = null;
	String username = null;
	List<ExecutionData> eds = new ArrayList<>();
	boolean isTransaction = false;		
	
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
				username = scan.next();
				
				log.writeLog(String.valueOf(new Timestamp(System.currentTimeMillis())) + ": Login Page");
				
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
						executionData = new CreateDatabase(query);
						executionData.execute();
						query = sc.nextLine().toLowerCase();
						log.writeLog(String.valueOf(new Timestamp(System.currentTimeMillis())) + ": " + query);
					}
					UseDatabase obj = new UseDatabase(query);
					database = obj.execute();

				}
			}

			if (isLoggedIn) {
				String opNumber = sc.nextLine();
				log.writeLog(String.valueOf(new Timestamp(System.currentTimeMillis())) + ": " + opNumber);
				chooseOperation(opNumber.toLowerCase(), sc, username);
				
			} else {
				run();
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			run();
		}
	}

	private void chooseOperation(String opNumber, Scanner sc, String username) {
		try {
//		Scanner ssc = new Scanner(System.in);
//		String query = ssc.nextLine();
			String query = opNumber;
//			System.out.println(query);
//		ExecutionData executionData = null;
			if (opNumber.contains("select")) {
				executionData = new SelectData(database, query);
				if(executionData.validateQuery(query)){
					eds.add(executionData);
				}
			} else if (opNumber.contains("create table")) {
				executionData = new CreateTable(database, query);
				if(executionData.validateQuery(query)){
					eds.add(executionData);
				}
			} else if (opNumber.contains("create database")) {
				executionData = new CreateDatabase(query);
				if(executionData.validateQuery(query)){
					eds.add(executionData);
				}
			} else if (opNumber.contains("insert")) {
				executionData = new InsertData(database, query);
				if(executionData.validateQuery(query)){
					eds.add(executionData);
				}
			} else if (opNumber.contains("update")) {
				executionData = new UpdateData(database, query);
				if(executionData.validateQuery(query)){
					eds.add(executionData);
				}
			} else if (opNumber.contains("alter")) {
				executionData = new AlterData();
				if(executionData.validateQuery(query)){
					eds.add(executionData);
				}
			} else if (opNumber.contains("delete")) {
				executionData = new DeleteData(database, query);
				if(executionData.validateQuery(query)){
					eds.add(executionData);
				}
			} else if (opNumber.contains("commit")) {
				isTransaction = false;
			} else if (opNumber.contains("begin")) {
				isTransaction = true;
			} else if (opNumber.contains("rollback")) {
				eds.clear();
				isTransaction = false;
			} else if (opNumber.contains("dump")) {
				executionData = new Datadump(query);
				executionData.execute();
			} else if (opNumber.contains("erd")) {
				executionData = new Erd(query);
				executionData.execute();
			} else if (opNumber.equals("exit")) {
				log.writeLog(String.valueOf(new Timestamp(System.currentTimeMillis())) + ": Exiting");
				System.exit(0);
			} else {
				System.out.println("Wrong input provided");
			}
			if (!isTransaction) {
				for (ExecutionData ed: eds){
					ed.execute();
				}
				eds.clear();
			}
			run();

		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			run();
		}
	}
}
