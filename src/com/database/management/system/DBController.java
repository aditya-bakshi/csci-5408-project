package com.database.management.system;

import java.util.Scanner;

public class DBController {
    static Scanner sc = new Scanner(System.in);
    //Define all msg on console
    public void run() {
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

    private void chooseOperation(int opNumber) {
        if (opNumber < 7 && opNumber >0) {
            String query = sc.next();
            ExecutionData executionData = null;
            switch (opNumber) {
                case 1: executionData = new CreateData();
                case 2:executionData = new SelectData();
                case 3:executionData = new UpdateData();
                case 4: executionData = new AlterData();
                case 5 : executionData =new DeleteData();
                case 6: System.exit(0);
            }
            executionData.execute(query);
        } else {
            System.out.println("Wrong input provided");
        }
    }
}
