package ro.jademy.contactlist;

import ro.jademy.contactlist.model.Contact;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.PhoneTypes;
import ro.jademy.contactlist.service.ContactService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

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
        List<String> menuItems = Arrays.asList("1. Mobile", "2. Work", "3. Home");
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
            menuChoice = sc.next();
            switch (menuChoice) {
                case "1":
                    printContactsNaturalOrder();
                    showPhoneBookMenu();
                    break;
                case "2":
                    System.out.print("Search: ");
                    String searchRequest = sc.nextLine();
                    contactService.getSearchResult(searchRequest, contactService.getContacts()).forEach(System.out::println);
                    showPhoneBookMenu();
                    break;
                case "3":
                    printContactsNaturalOrder();
                    System.out.print("\nYour option: ");
                    validateIntInsert();
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
                    contactService.getFavorites().forEach(System.out::println);
                    if (contactService.getFavorites().isEmpty()) {
                        System.out.println("\nNo favorite contact!");
                    }
                    showPhoneBookMenu();
                    break;
                case "5":
                    System.out.print("First Name: ");
                    String firstName = sc.nextLine();
                    try {
                        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
                    } catch (StringIndexOutOfBoundsException e) {
                        System.out.println("No first name entered!");
                    }
                    System.out.print("Last Name: ");
                    String lastName = sc.nextLine();
                    try {
                        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
                    } catch (StringIndexOutOfBoundsException e) {
                        System.out.println("No last name entered!");
                    }
                    System.out.print("Email: ");
                    String email = sc.nextLine();
                    validateEmail(email);
                    System.out.print("Phone Number: ");
                    String phoneNumber = sc.nextLine();
                    Contact newContact = new Contact(firstName, lastName, email, phoneNumber);
                    contactService.addContact(newContact, phoneNumber);
                    showPhoneBookMenu();
                    break;
                case "6":
                    System.out.print("Find contact you want to edit: ");
                    searchRequest = sc.nextLine();
                    List<Contact> contactsToEdit = new ArrayList<>(contactService.getSearchResult(searchRequest,
                            contactService.getContacts()));
                    contactsToEdit.forEach(System.out::println);
                    if (!contactsToEdit.isEmpty()) {
                        System.out.print("Contact to edit: ");
                        validateIntInsert();
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
                    validateIntInsert();
                    choice = sc.nextInt();
                    sc.nextLine();
                    if (contactService.getContactById(choice).isPresent()) {
                        System.out.println("Are you sure you want to delete the contact " +
                                contactService.getContactById(choice).get().getFirstName() + " " +
                                contactService.getContactById(choice).get().getLastName() + "?");
                        if (validateConfirmation()) {
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
                                contactService.doBackup();
                                break;
                            case "2":
                                System.out.print("Backup date: ");
                                String backupDate = sc.nextLine();
                                File[] backupFiles = contactService.backupsDir().toFile().listFiles();
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
                                        contactService.restoreFromBackup(backupFile);
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
                default:
                    System.out.print("Invalid option!\nPlease enter your option again (1-9): ");
                    menuChoice = sc.next();
            }
        } while (!menuChoice.equals("9"));
    }

    private void printContactsNaturalOrder() {
        OptionalInt maxLength =
                contactService.getContacts().stream().map(c1 -> (c1.getContactId() + c1.getFirstName() + c1.getLastName()))
                        .mapToInt(String::length)
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
            editMenuChoice = sc.next();
            while (!editMenuChoice.equals("1") && !editMenuChoice.equals("2") && !editMenuChoice.equals("3") && !editMenuChoice.equals(
                    "4") && !editMenuChoice.equals("5") && !editMenuChoice.equals("6") && !editMenuChoice.equals("7") && !editMenuChoice.equals("8")) {
                System.out.print("Invalid option!\nPlease enter your option again (1-8): ");
                editMenuChoice = sc.nextLine();
            }
            switch (editMenuChoice) {
                case "1":
                    int count = 0;
                    int menuWidth = 0;
                    PhoneTypes phoneKind;
                    Map<Integer, PhoneTypes> phonesType = new LinkedHashMap<>();
                    System.out.println("What number do you want to edit: ");
                    for (Map.Entry<PhoneTypes, PhoneNumber> entry : contact.getPhoneNumbers().entrySet()) {
                        count++;
                        phonesType.put(count, entry.getKey());
                        menuWidth =
                                phonesType.entrySet().stream().mapToInt(e -> (e.getKey() + ". " + e.getValue()).length()).summaryStatistics().getMax() + 5;
                    }
                    System.out.print("\033[32;1m+" + new String(new char[menuWidth]).replace('\0', '=') + "+\033[0m" + "\n");
                    for (Map.Entry<Integer, PhoneTypes> entry : phonesType.entrySet()) {
                        System.out.println("\033[32;1m|\033[0m" + " " + entry.getKey() + ". " + entry.getValue().getDisplayPhoneType() +
                                new String(new char[menuWidth - (entry.getKey() + ". " + entry.getValue()).length() - 1]).replace('\0',
                                        ' ') + "\033[32;1m|\033[0m");
                    }
                    System.out.println(("\033[32;1m+" + new String(new char[menuWidth]).replace('\0', '=')) + "+\033[0m");

                    validateIntInsert();
                    int typeOfPhone = sc.nextInt();
                    sc.nextLine();
                    int phoneToEditType;
                    for (Map.Entry<Integer, PhoneTypes> phoneEntry : phonesType.entrySet()) {
                        phoneToEditType = phoneEntry.getKey();
                        if (typeOfPhone == phoneToEditType) {
                            phoneKind = phoneEntry.getValue();
                            for (Map.Entry<PhoneTypes, PhoneNumber> entry : contact.getPhoneNumbers().entrySet()) {
                                if (phoneKind.equals(entry.getKey())) {
                                    System.out.println("Do you want to edit country code?");
                                    if (validateConfirmation()) {
                                        System.out.print("Enter new country code: ");
                                        String newCountryCode = sc.nextLine();
                                        entry.getValue().setCountryCode(newCountryCode);
                                    }
                                    System.out.println("Do you want to edit number?");
                                    if (validateConfirmation()) {
                                        System.out.print("Enter new number: ");
                                        String newNumber = sc.nextLine();
                                        entry.getValue().setNumber(newNumber);
                                    }
                                }
                            }

                        }
                    }
                    if (typeOfPhone < phonesType.keySet().stream().mapToInt(entry -> entry).summaryStatistics().getMin() ||
                            typeOfPhone > phonesType.keySet().stream().mapToInt(entry -> entry).summaryStatistics().getMax()) {
                        System.out.println("Invalid option");
                    }
                    break;
                case "2":
                    System.out.print("Enter phone number type (1-3): \n");
                    PhoneTypes phoneType = null;
                    showPhoneTypeMenu();
                    String choice = sc.nextLine();
                    while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3")) {
                        System.out.print("Invalid option!\nPlease enter your option again (1-3): ");
                        choice = sc.nextLine();
                    }
                    switch (choice) {
                        case "1":
                            phoneType = PhoneTypes.valueOf("Mobile".toUpperCase());
                            break;
                        case "2":
                            phoneType = PhoneTypes.valueOf("Work".toUpperCase());
                            break;
                        case "3":
                            phoneType = PhoneTypes.valueOf("Home".toUpperCase());
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
                    phonesType = new LinkedHashMap<>();
                    PhoneTypes keyToRemove = null;
                    System.out.println("What number do you want to remove: ");
                    for (Map.Entry<PhoneTypes, PhoneNumber> entry : contact.getPhoneNumbers().entrySet()) {
                        count++;
                        phonesType.put(count, entry.getKey());
                        menuWidth =
                                phonesType.entrySet().stream().mapToInt(e -> (e.getKey() + ". " + e.getValue()).length()).summaryStatistics().getMax() + 5;
                    }
                    System.out.print("\033[32;1m+" + new String(new char[menuWidth]).replace('\0', '=') + "+\033[0m" + "\n");
                    for (Map.Entry<Integer, PhoneTypes> entry : phonesType.entrySet()) {
                        System.out.println("\033[32;1m|\033[0m" + " " + entry.getKey() + ". " + entry.getValue().getDisplayPhoneType() +
                                new String(new char[menuWidth - (entry.getKey() + ". " + entry.getValue()).length() - 1]).replace('\0',
                                        ' ') + "\033[32;1m|\033[0m");
                    }
                    System.out.println(("\033[32;1m+" + new String(new char[menuWidth]).replace('\0', '=')) + "+\033[0m");

                    validateIntInsert();
                    typeOfPhone = sc.nextInt();
                    sc.nextLine();
                    boolean isChecked = false;
                    for (Map.Entry<Integer, PhoneTypes> phoneEntry : phonesType.entrySet()) {
                        if (typeOfPhone == phoneEntry.getKey()) {
                            isChecked = true;
                            phoneKind = phoneEntry.getValue();
                            for (Map.Entry<PhoneTypes, PhoneNumber> entry : contact.getPhoneNumbers().entrySet()) {
                                if (phoneKind.equals(entry.getKey())) {
                                    System.out.println("Are you sure you want to delete the " + entry.getKey().getDisplayPhoneType() + " " +
                                            "number?");
                                    if (validateConfirmation()) {
                                        keyToRemove = entry.getKey();
                                    }
                                }
                            }
                            contact.getPhoneNumbers().remove(keyToRemove);
                        }
                    }
                    if (!isChecked) {
                        System.out.println("No valid number!");
                    }
                    break;
                case "4":
                    System.out.println("Do you want to edit the first name?");
                    if (validateConfirmation()) {
                        System.out.println("Enter first name: ");
                        String newFirstName = sc.nextLine();
                        contact.setFirstName(newFirstName);
                    }
                    System.out.println("Do you want to edit the last name?");
                    if (validateConfirmation()) {
                        System.out.println("Enter last name: ");
                        String newLastName = sc.nextLine();
                        contact.setLastName(newLastName);
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
        int menuWidth = menuItems.stream().mapToInt(String::length).summaryStatistics().getMax() + 5;
        String side = "\033[32;1m|\033[0m";
        System.out.print("\033[32;1m\n+" + new String(new char[menuWidth]).replace('\0', '=') + "+\033[0m" + "\n");
        for (String menuItem : menuItems) {
            System.out.println(side + " " + menuItem + new String(new char[menuWidth - menuItem.length() - 1]).replace('\0',
                    ' ') + side);
        }
        System.out.print("\033[32;1m+" + new String(new char[menuWidth]).replace('\0', '=') + "+\033[0m" + "\n");
    }

    private void validateIntInsert() {
        while (!sc.hasNextInt()) {
            System.out.println("Please enter a valid option!");
            sc.next();
        }
    }

    private boolean validateConfirmation() {
        boolean isValidated = false;
        String confirmation = sc.nextLine();
        while (!confirmation.equals("Y".toLowerCase()) && !confirmation.equals("N".toLowerCase())) {
            System.out.println("Please enter 'y' or 'n'!");
            confirmation = sc.nextLine();
        }
        if (confirmation.equals("Y".toLowerCase())) {
            isValidated = true;
        }
        return isValidated;
    }

    private void validateEmail(String email) {
        while (!Pattern.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", email)) {
            System.out.println("Enter a valid email:");
            email = sc.nextLine();
        }
    }
}
