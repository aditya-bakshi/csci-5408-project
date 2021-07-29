package com.database.management.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class DBController {
    static Scanner sc = new Scanner(System.in);
    //Define all msg on console
    public void run() throws FileNotFoundException, IOException {
        System.out.println("Select one of the following :");
        System.out.println("1. Create query");
        System.out.println("2. Select query");
        System.out.println("3. Update query");
        System.out.println("4. Alter query");
        System.out.println("5. Delete query");
        System.out.println("6. Exit");


        int opNumber = sc.nextInt();
        chooseOperation(opNumber);
    }

    private void chooseOperation(int opNumber) throws FileNotFoundException, IOException {
        if (opNumber < 7 && opNumber >0) {
        	Scanner sc = new Scanner(System.in);
            String query = sc.nextLine();
            ExecutionData executionData = null;
            switch (opNumber) {
                case 1: 
                	executionData = new CreateData();
                	break;
                case 2:
                	executionData = new SelectData();
                	break;
                case 3:
                	executionData = new UpdateData();
                	break;
                case 4: 
                	executionData = new AlterData();
                	break;
                case 5 : 
                	executionData =new DeleteData();
                	break;
                case 6: System.exit(0);
            }
            executionData.execute(query);
        } else {
        	System.out.println("Wrong input provided");
        }
    }
}
