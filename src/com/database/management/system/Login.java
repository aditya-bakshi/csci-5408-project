package com.database.management.system;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Login {

	public boolean authenticate(String username, String password) throws IOException {
//		Path currentRelativePath = Paths.get("");
//		String s = currentRelativePath.toAbsolutePath().toString();
		List<String> file = Files.readAllLines(Paths.get("authenticate/users.txt"));
		for (String data : file) {
			String[] columnData = data.split("#");
			String user = columnData[0].split("=")[1];
			if (user.equals(username)) {
				String pass = columnData[1].split("=")[1].replaceAll("@", "");
				if (pass.equals(password)) {
					String question = columnData[2].split("=")[1];
					String answer = columnData[3].split("=")[1];
					System.out.println(question);
					Scanner scan = new Scanner(System.in);
					String ans = scan.next();
					if (ans.equals(answer)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
