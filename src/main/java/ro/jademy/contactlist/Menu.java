package ro.jademy.contactlist;

import ro.jademy.contactlist.model.Contact;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.service.ContactService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;

public class Menu {

    private Scanner sc = new Scanner(System.in);
    private ContactService contactService;

    public Menu(ContactService contactService) {
        this.contactService = contactService;
    }

    private void showPhoneBookMenu() {
        List<String> menuItems = Arrays.asList("1. Contacts", "2. Search", "3. Contact details", "4. Favorites",
                "5. Add new contact", "6. Edit contact", "7. Delete contact", "8. Backup/Restore", "9. Exit");
        menu(menuItems);
    }

    private void showEditMenu() {
        List<String> menuItems = Arrays.asList("1. Edit phone number", "2. Add phone number", "3. Remove phone number",
                "4. Edit contact name", "5. Edit email address", "6. Add to fovorites", "7. Remove from favorites",
                "8. Back to Phonebook Menu");
        menu(menuItems);
    }

    private void showPhoneTypeMenu() {
        List<String> menuItems = Arrays.asList("1. Mobile", "2. Work", "3. Home",
                "4. Other");
        menu(menuItems);
    }

    private void backupAndRestoreMenu() {
        List<String> menuItems = Arrays.asList("1. Backup", "2. Restore", "3. Back to Phonebook Menu");
        menu(menuItems);
    }

    public void menuChoice() {
        showPhoneBookMenu();
        String menuChoice;
        do {
            System.out.print("Your option (1-9): ");
            menuChoice = sc.nextLine();
            while (!menuChoice.equals("1") && !menuChoice.equals("2") && !menuChoice.equals("3") && !menuChoice.equals("4") &&
                    !menuChoice.equals("5") && !menuChoice.equals("6") && !menuChoice.equals("7") && !menuChoice.equals("8") &&
                    !menuChoice.equals("9")) {
                System.out.print("Invalid option!\nPlease enter your option again (1-9): ");
                menuChoice = sc.nextLine();
            }
            switch (menuChoice) {
                case "1":
                    printContactsNaturalOrder();
                    showPhoneBookMenu();
                    break;
                case "2":
                    System.out.print("Search: ");
                    String searchRequest = sc.nextLine();
                    contactService.getSearchResult(searchRequest, contactService.getContacts()).stream().forEach(entry -> System.out.println(entry));
                    showPhoneBookMenu();
                    break;
                case "3":
                    printContactsNaturalOrder();
                    System.out.print("\nYour option: ");
                    while (!sc.hasNextInt()) {
                        System.out.println("Please enter a valid option!");
                        sc.next();
                    }
                    int choice = sc.nextInt();
                    sc.nextLine();
                    if (contactService.getContactById(choice).isPresent()) {
                        System.out.println(contactService.getContactById(choice).get());
                    } else {
                        System.out.println("No contact with this ID!");
                    }
                    showPhoneBookMenu();
                    break;
                case "4":
                    contactService.getFavorites().stream().forEach(contact -> System.out.println(contact));
                    if (contactService.getFavorites().isEmpty()) {
                        System.out.println("\nNo favorite contact!");
                    }
                    showPhoneBookMenu();
                    break;
                case "5":
                    System.out.print("First Name: ");
                    String firstName = sc.nextLine();
                    String firstNameCapitalized =
                            firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
                    System.out.print("Last Name: ");
                    String lastName = sc.nextLine();
                    String lastNameCapitalized = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
                    System.out.print("Email: ");
                    String email = sc.nextLine();
                    System.out.print("Phone Number: ");
                    String phoneNumber = sc.nextLine();
                    int id = contactService.getContacts().stream().mapToInt(contact -> contact.getContactId()).summaryStatistics().getMax();
                    Contact newContact = new Contact(firstNameCapitalized, lastNameCapitalized, email, new PhoneNumber(phoneNumber), false
                            , id + 1);
                    if (id < 0) {
                        newContact = new Contact(firstNameCapitalized, lastNameCapitalized, email, new PhoneNumber(phoneNumber), false,
                                id + 2147483647 + 2);
                    }
                    contactService.addContact(newContact, phoneNumber);
                    showPhoneBookMenu();
                    break;
                case "6":
                    System.out.print("Find contact you want to edit: ");
                    searchRequest = sc.nextLine();
                    List<Contact> contactsToEdit = new ArrayList<>(contactService.getSearchResult(searchRequest,
                            contactService.getContacts()));
                    contactsToEdit.forEach(contact -> System.out.println(contact));
                    if (!contactsToEdit.isEmpty()) {
                        System.out.print("Contact to edit: ");
                        while (!sc.hasNextInt()) {
                            System.out.println("Please enter a valid option!");
                            sc.next();
                        }
                        choice = sc.nextInt();
                        sc.nextLine();
                        boolean isChecked = contactsToEdit.stream().anyMatch(contact -> choice == contact.getContactId());
                        Optional<Contact> contactOptional = contactService.getContactById(choice);
                        if (contactOptional.isPresent() && isChecked) {
                            System.out.println("What to edit to " + contactOptional.get().getFirstName() +
                                    " " + contactOptional.get().getLastName() + "?");
                            Contact editedContact = editContactOptions(contactOptional.get());
                            if (!contactOptional.get().equals(editedContact)) {
                                contactService.editContact(editedContact.getFirstName(), editedContact.getLastName(),
                                        editedContact.getEmail(), editedContact.getAge(), editedContact.getPhoneNumbers(),
                                        editedContact.getAddress(), editedContact.getJobTitle(), editedContact.getCompany(),
                                        editedContact.isFavorite(), editedContact.getContactId());
                            }
                        } else {
                            System.out.println("No contact with this ID in the search results!");
                            showPhoneBookMenu();
                        }
                    } else {
                        System.out.println("No contact found!");
                        showPhoneBookMenu();
                    }
                    break;
                case "7":
                    printContactsNaturalOrder();
                    System.out.print("Your option: ");
                    while (!sc.hasNextInt()) {
                        System.out.println("Please enter a valid option!");
                        sc.next();
                    }
                    choice = sc.nextInt();
                    sc.nextLine();
                    if (contactService.getContactById(choice).isPresent()) {
                        System.out.println("Are you sure you want to delete the contact " +
                                contactService.getContactById(choice).get().getFirstName() + " " +
                                contactService.getContactById(choice).get().getLastName() + "?");
                        String confirmation = sc.nextLine();
                        while (!confirmation.equals("Y".toLowerCase()) && !confirmation.equals("N".toLowerCase())) {
                            System.out.println("Please enter 'y' or 'n'!");
                            confirmation = sc.nextLine();
                        }
                        if (confirmation.equals("Y".toLowerCase())) {
                            contactService.removeContact(choice);
                        }
                    } else {
                        System.out.println("No contact with this ID!");
                    }
                    showPhoneBookMenu();
                    break;
                case "8":
                    String option;
                    do {
                        backupAndRestoreMenu();
                        System.out.print("Your option: ");
                        option = sc.nextLine();
                        while (!option.equals("1") && !option.equals("2") && !option.equals("3")) {
                            System.out.print("Invalid option!\nPlease enter your option again (1-3): ");
                            option = sc.nextLine();
                        }
                        switch (option) {
                            case "1":
                                contactService.backupFile();
                                break;
                            case "2":
                                System.out.print("Backup date: ");
                                String backupDate = sc.nextLine();
                                Path backupDir = Paths.get("backups");
                                File[] backupFiles = backupDir.toFile().listFiles();
                                final Boolean[] isChecked = {false};
                                Arrays.stream(Objects.requireNonNull(backupFiles)).sorted(Comparator.reverseOrder())
                                        .forEach(file -> {
                                            try {
                                                String backupFileDate =
                                                        new SimpleDateFormat("yyMMdd").format(Files.readAttributes(Paths.get(file.toString()),
                                                                BasicFileAttributes.class).lastModifiedTime().toMillis());
                                                if (backupDate.equals(backupFileDate)) {
                                                    isChecked[0] = true;
                                                    System.out.println(file);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        });
                                if (isChecked[0]) {
                                    System.out.print("Backup file: ");
                                    String backupFile = sc.nextLine();
                                    if (new File(backupFile).isFile()) {
                                        contactService.restoreFromBackup(backupFile, "contacts.csv");
                                        contactService.removeAllContacts();
                                        contactService.getContacts();
                                    } else {
                                        System.out.println("This backup file wasn't found.");
                                    }
                                } else {
                                    System.out.println("No backup files at the specified date!");
                                }
                                break;
                            default:
                                showPhoneBookMenu();
                        }
                    } while (!option.equals("3"));
            }
        } while (!menuChoice.equals("9"));
    }

    private void printContactsNaturalOrder() {
        OptionalInt maxLength =
                contactService.getContacts().stream().map(c1 -> (c1.getContactId() + c1.getFirstName() + c1.getLastName()))
                        .mapToInt(s -> s.length())
                        .max();
        for (Map.Entry<Character, List<Contact>> listEntry : contactService.groupByInitial(contactService.getContacts()).entrySet()) {
            if (maxLength.isPresent()) {
                System.out.printf("\n\033[32;1m%s\033[0m%n\033[35;1m%s\033[0m%n", listEntry.getKey(),
                        new String(new char[maxLength.getAsInt() + 2]).replace('\0', '='));

                listEntry.getValue().forEach(c -> System.out.printf("%s %s %s%n\033[35;1m%s\033[0m%n", c.getContactId(),
                        c.getFirstName(), c.getLastName(), new String(new char[maxLength.getAsInt() + 2]).replace('\0',
                                '-')));

                System.out.printf("%" + (maxLength.getAsInt() - 9) + "s" + "%s%n", listEntry.getValue().size(), " " +
                        "contact(s)");
            }
        }
    }

    private Contact editContactOptions(Contact contact) {
        Contact editedContact = new Contact(contact);

        String editMenuChoice;
        do {
            showEditMenu();
            System.out.print("Your option (1-8): ");
            editMenuChoice = sc.nextLine();
            while (!editMenuChoice.equals("1") && !editMenuChoice.equals("2") && !editMenuChoice.equals("3") && !editMenuChoice.equals(
                    "4") && !editMenuChoice.equals("5") && !editMenuChoice.equals("6") && !editMenuChoice.equals("7") && !editMenuChoice.equals("8")) {
                System.out.print("Invalid option!\nPlease enter your option again (1-8): ");
                editMenuChoice = sc.nextLine();
            }
            switch (editMenuChoice) {
                case "1":
                    int count = 0;
                    int menuWidth = 0;
                    String phoneKind;
                    Map<Integer, String> phonesType = new LinkedHashMap<>();
                    System.out.println("What number do you want to edit: ");
                    for (Map.Entry<String, PhoneNumber> entry : contact.getPhoneNumbers().entrySet()) {
                        count++;
                        phonesType.put(count, entry.getKey());
                        menuWidth =
                                phonesType.entrySet().stream().mapToInt(e -> (e.getKey() + ". " + e.getValue()).length()).summaryStatistics().getMax() + 5;
                    }
                    System.out.print("\033[32;1m+" + new String(new char[menuWidth]).replace('\0', '=') + "+\033[0m" + "\n");
                    for (Map.Entry<Integer, String> entry : phonesType.entrySet()) {
                        System.out.println("\033[32;1m|\033[0m" + " " + entry.getKey() + ". " + entry.getValue() +
                                new String(new char[menuWidth - (entry.getKey() + ". " + entry.getValue()).length() - 1]).replace('\0',
                                        ' ') + "\033[32;1m|\033[0m");
                    }
                    System.out.println(("\033[32;1m+" + new String(new char[menuWidth]).replace('\0', '=')) + "+\033[0m");

                    while (!sc.hasNextInt()) {
                        System.out.println("Please enter a valid option!");
                        sc.next();
                    }
                    int typeOfPhone = sc.nextInt();
                    sc.nextLine();
                    for (Map.Entry<Integer, String> phoneEntry : phonesType.entrySet()) {
                        if (typeOfPhone == phoneEntry.getKey()) {
                            phoneKind = phoneEntry.getValue();
                            System.out.print("Enter new number: ");
                            String newNumber = sc.nextLine();
                            for (Map.Entry<String, PhoneNumber> entry : contact.getPhoneNumbers().entrySet()) {
                                if (phoneKind.equals(entry.getKey())) {
                                    entry.getValue().setNumber(newNumber);
                                }
                            }
                        } else {
                            System.out.println("No valid number!");
                        }
                    }
                    break;
                case "2":
                    System.out.print("Enter phone number type (1-4): \n");
                    String phoneType = "";
                    showPhoneTypeMenu();
                    String choice = sc.nextLine();
                    while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3") && !choice.equals(
                            "4")) {
                        System.out.print("Invalid option!\nPlease enter your option again (1-4): ");
                        choice = sc.nextLine();
                    }
                    switch (choice) {
                        case "1":
                            phoneType = "Mobile";
                            break;
                        case "2":
                            phoneType = "Work";
                            break;
                        case "3":
                            phoneType = "Home";
                            break;
                        case "4":
                            System.out.print("Enter phone number type: ");
                            phoneType = sc.nextLine();
                            break;
                    }
                    System.out.print("Enter country code: ");
                    String countryCode = sc.nextLine();
                    System.out.print("Enter phone number: ");
                    String phoneNumber = sc.nextLine();
                    contact.getPhoneNumbers().put(phoneType, new PhoneNumber(countryCode, phoneNumber));
                    break;
                case "3":
                    count = 0;
                    menuWidth = 0;
                    phonesType = new LinkedHashMap<>()
                    ;
                    System.out.println("What number do you want to remove: ");
                    for (Map.Entry<String, PhoneNumber> entry : contact.getPhoneNumbers().entrySet()) {
                        count++;
                        phonesType.put(count, entry.getKey());
                        menuWidth =
                                phonesType.entrySet().stream().mapToInt(e -> (e.getKey() + ". " + e.getValue()).length()).summaryStatistics().getMax() + 5;
                    }
                    System.out.print("\033[32;1m+" + new String(new char[menuWidth]).replace('\0', '=') + "+\033[0m" + "\n");
                    for (Map.Entry<Integer, String> entry : phonesType.entrySet()) {
                        System.out.println("\033[32;1m|\033[0m" + " " + entry.getKey() + ". " + entry.getValue() +
                                new String(new char[menuWidth - (entry.getKey() + ". " + entry.getValue()).length() - 1]).replace('\0',
                                        ' ') + "\033[32;1m|\033[0m");
                    }
                    System.out.println(("\033[32;1m+" + new String(new char[menuWidth]).replace('\0', '=')) + "+\033[0m");

                    while (!sc.hasNextInt()) {
                        System.out.println("Please enter a valid option!");
                        sc.next();
                    }
                    typeOfPhone = sc.nextInt();
                    sc.nextLine();
                    for (Map.Entry<Integer, String> phoneEntry : phonesType.entrySet()) {
                        if (typeOfPhone == phoneEntry.getKey()) {
                            phoneKind = phoneEntry.getValue();
                            for (Map.Entry<String, PhoneNumber> entry : contact.getPhoneNumbers().entrySet()) {
                                if (phoneKind.equals(entry.getKey())) {
                                    System.out.println("Are you sure you want to delete the " + entry.getKey() + " " +
                                            "number?");
                                    String confirmation = sc.nextLine();
                                    while (!confirmation.equals("Y".toLowerCase()) && !confirmation.equals("N".toLowerCase())) {
                                        System.out.println("Please enter 'y' or 'n'!");
                                        confirmation = sc.nextLine();
                                    }
                                    if (confirmation.equals("Y".toLowerCase())) {
                                        contact.getPhoneNumbers().remove(entry.getKey());
                                    }
                                }
                            }
                        } else {
                            System.out.println("No valid number!");
                        }
                    }
                    break;
                case "4":
                    String firstName = "";
                    String lastName = "";
                    System.out.println("Do you want to edit the first name?");
                    String confirmation = sc.nextLine();
                    while (!confirmation.equals("Y".toLowerCase()) && !confirmation.equals("N".toLowerCase())) {
                        System.out.println("Please enter 'y' or 'n'!");
                        confirmation = sc.nextLine();
                    }
                    if (confirmation.equals("Y".toLowerCase())) {
                        System.out.print("Enter first name: ");
                        firstName = sc.nextLine();
                        contact.setFirstName(firstName);
                    }
                    System.out.println("Do you want to edit the last name?");
                    confirmation = sc.nextLine();
                    while (!confirmation.equals("Y".toLowerCase()) && !confirmation.equals("N".toLowerCase())) {
                        System.out.println("Please enter 'y' or 'n'!");
                        confirmation = sc.nextLine();
                    }
                    if (confirmation.equals("Y".toLowerCase())) {
                        System.out.print("Enter last name: ");
                        lastName = sc.nextLine();
                        contact.setLastName(lastName);
                    }
                    break;
                case "5":
                    System.out.print("Enter new email address: ");
                    String newEmail = sc.nextLine();
                    contact.setEmail(newEmail);
                    break;
                case "6":
                    contact.setFavorite(true);
                    break;
                case "7":
                    contact.setFavorite(false);
                    break;
                default:
                    showPhoneBookMenu();
            }
        } while (!editMenuChoice.equals("8"));
        return editedContact;
    }

    private void menu(List<String> menuItems) {
        int menuWidth = menuItems.stream().mapToInt(entry -> entry.length()).summaryStatistics().getMax() + 5;
        String side = "\033[32;1m|\033[0m";
        System.out.print("\033[32;1m\n+" + new String(new char[menuWidth]).replace('\0', '=') + "+\033[0m" + "\n");
        for (String menuItem : menuItems) {
            System.out.println(side + " " + menuItem + new String(new char[menuWidth - menuItem.length() - 1]).replace('\0',
                    ' ') + side);
        }
        System.out.print("\033[32;1m+" + new String(new char[menuWidth]).replace('\0', '=') + "+\033[0m" + "\n");
    }
}
