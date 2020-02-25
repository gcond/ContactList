package ro.jademy.contactlist.service;

import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.Contact;
import ro.jademy.contactlist.model.PhoneNumber;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FileContactService implements ContactService {

    private File contactsFile;

    public File getContactsFile() {
        return contactsFile;
    }

    private Path backupDir = Paths.get("backups");

    public Path getBackupDir() {
        return backupDir;
    }

    private List<Contact> contacts = new ArrayList<>();

    public FileContactService(File contactsFile) {
        this.contactsFile = contactsFile;
    }

    public FileContactService(String contactsFileName) {
        this(new File(contactsFileName));
    }

    @Override
    public List<Contact> getContacts() {

        // check if contacts is empty
        if (contacts.isEmpty()) {
            contacts.addAll(readFromFile());
        }

        // else return the current list of contacts
        return contacts;
    }

    @Override
    public Map<Character, List<Contact>> groupByInitial(List<Contact> list) {
        return list.stream().sorted(Comparator.comparing((Contact contact1) -> contact1.getFirstName()).thenComparing(contact2 -> contact2.getLastName()))
                .collect(Collectors.groupingBy(contact -> contact.getFirstName().charAt(0), TreeMap::new,
                        Collectors.toList()));
    }

    @Override
    public List<Contact> getFavorites() {
        return getContacts().stream().filter(fav -> fav.isFavorite())
                .sorted(Comparator.comparing((Contact contact1) -> contact1.getFirstName()).thenComparing(contact2 -> contact2.getLastName()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Contact> getContactById(int contactId) {
        return contacts.stream().filter(c -> c.getContactId() == contactId).findFirst();
    }

    @Override
    public void addContact(Contact contact, String number) {
        // add contact to contact list
        contacts.add(contact);

        // overwrite the whole list of contacts in the file
        updateFile();
        backupFile();
    }

    @Override
    public void editContact(String firstName, String lastName, String email, Integer age, Map<String,
            PhoneNumber> phoneNumbers, Address address, String jobTitle, Company company, boolean isFavorite, int contactId) {
        Optional<Contact> contactOpt = getContactById(contactId);

        // edit the contact only if the contact was found
        if (contactOpt.isPresent()) {
            Contact contact = contactOpt.get();
            
            // overwrite the whole list of contacts in the file
            updateFile();
            backupFile();
        }
    }

    @Override
    public void removeContact(int contactId) {
        Optional<Contact> contactOpt = getContactById(contactId);

        // remove the contact only if found
        if (contactOpt.isPresent()) {
            Contact contact = contactOpt.get();
            contacts.remove(contact);
        }

        // overwrite the whole list of contacts in the file
        updateFile();
        backupFile();
    }

    @Override
    public void removeAllContacts() {
        contacts.clear();
    }


    @Override
    public List<Contact> getSearchResult(String searchQuery, List<Contact> list) {
        return list.stream().filter(contact -> contact.getFirstName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                contact.getLastName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                contact.getEmail().toLowerCase().contains(searchQuery.toLowerCase()))/* ||*/
//                contact.getAge().toString().contains(searchInput) ||
//                contact.getCompany().getName().toLowerCase().contains(searchInput.toLowerCase()))
                .sorted(Comparator.comparing((Contact contact1) -> contact1.getFirstName()).thenComparing(contact2 -> contact2.getLastName()))
                .collect(Collectors.toList());
    }

    private List<Contact> readFromFile() {
        if (!new File("contacts.csv").isFile()) {
            System.out.println("File \"contacts.csv\" not found!");
            restoreFromBackup(fileForBackup(), "contacts.csv");
            System.out.println("The file \"contacts.csv\" was created and restored from last backup.");
        }

        List<Contact> contacts = new ArrayList<>();
        try {
            contacts.addAll(Files.lines(Paths.get("contacts.csv")).skip(1).map(line -> createContactFromLine(line)).collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    private Contact createContactFromLine(String line) {
        List<String> fields = Arrays.asList(line.split("[|]"));
        String firstName = fields.get(0);
        String lastName = fields.get(1);
        String phonesList = fields.get(2);
        String email = fields.get(3);
        boolean isFavorite = Boolean.parseBoolean(fields.get(4));
        int id = Integer.parseInt(fields.get(5));

        String[] phones = phonesList.split(">");
        String key;
        String countryCode;
        String number;
        Map<String, PhoneNumber> phonesNumbers = new LinkedHashMap<>();
        for (String phone : phones) {
            List<String> phoneNumbers = Arrays.asList(phone.split("_"));
            key = phoneNumbers.get(0);
            countryCode = phoneNumbers.get(1);
            number = phoneNumbers.get(2);
            phonesNumbers.put(key, new PhoneNumber(countryCode, number));
        }

        return new Contact(firstName, lastName, email, phonesNumbers, isFavorite, id);
    }

    private void updateFile() {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter("contacts.csv", false));
            String phoneNumbers = "";
            bw.write("F_NAME|L_NAME|PHONE_NUMBERS|EMAIL|IS_FAVORITE|ID");
            for (Contact contact : contacts) {
                for (Map.Entry<String, PhoneNumber> entry : contact.getPhoneNumbers().entrySet()) {
                    phoneNumbers = contact.getPhoneNumbers().entrySet().stream()
                            .map(entry1 -> entry1.getKey() + "_" + entry1.getValue().getCountryCode() + "_" + entry1.getValue().getNumber())
                            .collect(Collectors.toList())
                            .toString().replace("[", "").replace(", ", ">").replace("]", "");
                }
                bw.newLine();
                bw.write(contact.getFirstName() + "|" + contact.getLastName() + "|" + phoneNumbers + "|" + contact.getEmail() +
                        "|" + contact.isFavorite() + "|" + contact.getContactId());
                bw.flush();
            }
        } catch (IOException ex) {
            System.out.println("File not found\n" + ex);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                System.out.println("Could not close stream.");
            }
        }
    }

    public void addContactToFile(String firstName, String lastName, String phoneType, String countryCode,
                                 String phoneNumber, String email,
                                 boolean isFavorite, int contactId) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter("contacts.csv", true));
            bw.newLine();
            bw.write(firstName + "|" + lastName + "|" + phoneType + "_" + countryCode + "_" + phoneNumber + "|" + email +
                    "|" + isFavorite + "|" + contactId);
            bw.flush();
        } catch (IOException ex) {
            System.out.println("File not found\n" + ex);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                System.out.println("Could not close stream.");
            }
        }
    }

    @Override
    public void backupFile() {
        if (!new File("backups").isDirectory()) {
            new File("backups").mkdir();
        }
        File[] backupFiles = backupDir.toFile().listFiles();
        if (backupFiles != null) {
            Optional<File> earliestBackup =
                    Arrays.stream(backupFiles).min(Comparator.comparingLong(file -> file.lastModified()));
            if (earliestBackup.isPresent() && backupFiles.length > 4) {
                earliestBackup.get().delete();
            }
        }
        Path source = Paths.get("contacts.csv");
        Path target =
                Paths.get("backups/backup__" +
                        new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS").format(Calendar.getInstance().getTime()) +
                        "__" + System.currentTimeMillis() + ".csv");
        try {
            Files.copy(source, target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void restoreFromBackup(String source, String target) {
        Path sourcePath = Paths.get(source);
        Path targetPath = Paths.get(target);
        try {
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String fileForBackup() {
        if (!new File("backups").isDirectory()) {
                new File("backups").mkdir();
            }

        File backupsDir = new File("backups");
        if (backupsDir.isDirectory()) {
            if (backupsDir.exists() && backupsDir.list().length == 0) {
                try {
                    new File("backups/emptybackup.csv").createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        File[] backupFiles = backupDir.toFile().listFiles();
        if (backupFiles != null) {
            Optional<File> lastBackup =
                    Arrays.stream(backupFiles).max(Comparator.comparingLong(file -> file.lastModified()));
            if (lastBackup.isPresent()) {
                return lastBackup.get().toString();
            }
        }
        return null;
    }
}
