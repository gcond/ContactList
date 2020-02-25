package ro.jademy.contactlist;

import ro.jademy.contactlist.service.FileContactService;

public class Main {

    public static void main(String[] args) {

        Menu menu = new Menu(new FileContactService("contacts.csv"));
        menu.menuChoice();
    }
}
