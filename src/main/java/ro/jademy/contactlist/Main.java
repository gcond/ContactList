package ro.jademy.contactlist;

import ro.jademy.contactlist.service.DBContactService;
import ro.jademy.contactlist.service.FileContactService;
import ro.jademy.contactlist.utils.JDBCUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.print("\n1. Work with files...\n2. Work with databases...\n\nYour option: ");

        Scanner sc = new Scanner(System.in);
        String option = sc.nextLine();
        while (!option.equals("1") && !option.equals("2")) {
            System.out.print("Invalid option!\nPlease enter your option again (1-2): ");
            option = sc.nextLine();
        }
        switch (option) {
            case "1":
                Menu menuFile = new Menu(new FileContactService("contacts.csv"));
                menuFile.menuChoice();
                break;
            case "2":
                Menu menuDB;
                try(Connection conn = new JDBCUtil().getConnection()) {
                    menuDB = new Menu(new DBContactService(conn));
                    menuDB.menuChoice();
                } catch (SQLException e) {
                    System.out.println("\nConnection failure...\n" + e);
                }
                break;
        }
    }
}
