package ro.jademy.contactlist.service;

import ro.jademy.contactlist.model.*;
import ro.jademy.contactlist.utils.JDBCUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DBContactService implements ContactService {

    private Connection conn;
    private List<Contact> contacts = new ArrayList<>();

    public DBContactService(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Contact> getContacts() {
        if (contacts.isEmpty()) {
            contacts.addAll(getContactsFromDB());
        }
        return contacts;
    }

    @Override
    public Optional<Contact> getContactById(int contactId) {
        return contacts.stream().filter(c -> c.getContactId() == contactId).findFirst();
    }

    @Override
    public void addContact(Contact contact, String number) {
        contacts.add(contact);

        String firstName = contact.getFirstName();
        String lastName = contact.getLastName();
        String email = contact.getEmail();
        boolean isFavorite = contact.isFavorite();
        PhoneTypes phoneType = contact.getPhoneNumbers().keySet().isEmpty() ? PhoneTypes.valueOf("") :
                contact.getPhoneNumbers().keySet().stream()
                        .findFirst().get();
        String countryCode =
                contact.getPhoneNumbers().values().stream()
                        .map(PhoneNumber::getCountryCode)
                        .collect(Collectors.toList())
                        .toString().replace("[", "").replace("]", "");
        String phoneNumber = contact.getPhoneNumbers().isEmpty() ? ""
                : contact.getPhoneNumbers().values().stream()
                .map(PhoneNumber::getNumber).findFirst().get();

        String queryContacts = "INSERT INTO contacts\n" +
                "(first_name,last_name,email,is_favorite)\n" +
                "VALUES(?,?,?,?)";
        String queryPhoneNumbers = "INSERT INTO phonenumbers\n" +
                "(phone_type,country_code,phone_number,contact_id)\n" +
                "VALUES(?,?,?,?)";
        int contactIdInDB = 0;
        boolean isCommitted = true;
        try (PreparedStatement pstmtContacts = conn.prepareStatement(queryContacts, Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);
            pstmtContacts.setString(1, firstName);
            pstmtContacts.setString(2, lastName);
            pstmtContacts.setString(3, email);
            pstmtContacts.setString(4, Boolean.toString(isFavorite));

            int rowAffected = pstmtContacts.executeUpdate();
            if (rowAffected == 1) {
                ResultSet rs = pstmtContacts.getGeneratedKeys();
                while (rs.next()) {
                    contactIdInDB = rs.getInt(1);
                    contact.setContactId(contactIdInDB);
                }
            }
            try (PreparedStatement pstmtPhones = conn.prepareStatement(queryPhoneNumbers)) {
                pstmtPhones.setString(1, phoneType.toString());
                pstmtPhones.setString(2, countryCode);
                pstmtPhones.setString(3, phoneNumber);
                pstmtPhones.setInt(4, contactIdInDB);

                pstmtPhones.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                isCommitted = false;
                System.out.println("Rolled back!\n" + e);
            }
            if (isCommitted) {
                conn.commit();
            } else {
                contacts.remove(contact);
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        doBackup();
    }

    @Override
    public void editContact(String firstName, String lastName, String email, Integer age,
                            Map<PhoneTypes, PhoneNumber> phoneNumbers, Address address,
                            String jobTitle, Company company, boolean isFavorite, int contactId) {

        updateDB(contactId);
        doBackup();
    }

    @Override
    public void removeContact(int contactId) {
        Optional<Contact> contactOpt = getContactById(contactId);
        if (contactOpt.isPresent()) {
            Contact contact = contactOpt.get();
            contacts.remove(contact);
        }

        String query = "DELETE FROM contacts\n" +
                "WHERE contact_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, contactId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        doBackup();
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
                .sorted(Comparator.comparing(Contact::getFirstName).thenComparing(Contact::getLastName))
                .collect(Collectors.toList());
    }

    @Override
    public Map<Character, List<Contact>> groupByInitial(List<Contact> list) {
        return list.stream().sorted(Comparator.comparing(Contact::getFirstName).thenComparing(Contact::getLastName))
                .collect(Collectors.groupingBy(contact -> contact.getFirstName().charAt(0), TreeMap::new,
                        Collectors.toList()));
    }

    @Override
    public List<Contact> getFavorites() {
        return getContacts().stream().filter(Contact::isFavorite)
                .sorted(Comparator.comparing(Contact::getFirstName).thenComparing(Contact::getLastName))
                .collect(Collectors.toList());
    }

    @Override
    public Path backupsDir() {
        return Paths.get("DBbackups");
    }


    @Override
    public void doBackup() {
        if (!new File(backupsDir().toString()).isDirectory()) {
            new File(backupsDir().toString()).mkdir();
        }
        File[] backupFiles = backupsDir().toFile().listFiles();
        if (backupFiles != null) {
            Optional<File> earliestBackup =
                    Arrays.stream(backupFiles).min(Comparator.comparingLong(File::lastModified));
            if (earliestBackup.isPresent() && backupFiles.length > 4) {
                earliestBackup.get().delete();
            }
        }

        String database = "";
        String user = "";
        String password = "";

        try (InputStream input = JDBCUtil.class.getResourceAsStream("/db.properties")) {

            Properties props = new Properties();
            props.load(input);

            database = props.getProperty("db.database");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

        } catch (IOException e) {
            e.printStackTrace();
        }

        String executeBackupCmd =
                "mysqldump -u " + user + " -p" + password + " --result-file=" + backupsDir().toString() +
                        "/contacts_backup__" +
                        new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS").format(Calendar.getInstance().getTime()) +
                        "__" + System.currentTimeMillis() + ".sql --databases " + database;

        try {
            Runtime.getRuntime().exec(executeBackupCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void restoreFromBackup(String source) {
        String database = "";
        String user = "";
        String password = "";

        try (InputStream input = JDBCUtil.class.getResourceAsStream("/db.properties")) {

            Properties props = new Properties();
            props.load(input);

            database = props.getProperty("db.database");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

        } catch (IOException e) {
            e.printStackTrace();
        }

        String executeBackupCmd =
                "/bin/sh -c mysql -u " + user + " -p" + password + " " + database + " < " + source;

        try {
            Runtime.getRuntime().exec(executeBackupCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Contact> getContactsFromDB() {
        List<Contact> contacts = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(queryContactFromDB())) {
            while (rs.next()) {
                int contactId = rs.getInt("c.contact_id");
                String firstName = rs.getString("c.first_name");
                String lastName = rs.getString("c.last_name");
                String email = rs.getString("c.email");
                String keyMobile = rs.getString("pnm.phone_type");
                String countryCodeMobile = rs.getString("pnm.country_code");
                String numberMobile = rs.getString("pnm.phone_number");
                String keyWork = rs.getString("pnw.phone_type");
                String countryCodeWork = rs.getString("pnw.country_code");
                String numberWork = rs.getString("pnw.phone_number");
                String keyHome = rs.getString("pnh.phone_type");
                String countryCodeHome = rs.getString("pnh.country_code");
                String numberHome = rs.getString("pnh.phone_number");
                boolean isFavorite = rs.getBoolean("c.is_favorite");

                Map<PhoneTypes, PhoneNumber> phoneNumbers = new LinkedHashMap<>();
                if (keyMobile != null) {
                    phoneNumbers.put(PhoneTypes.valueOf(keyMobile.toUpperCase()), new PhoneNumber(countryCodeMobile, numberMobile));
                }
                if (keyWork != null) {
                    phoneNumbers.put(PhoneTypes.valueOf(keyWork.toUpperCase()), new PhoneNumber(countryCodeWork, numberWork));
                }
                if (keyHome != null) {
                    phoneNumbers.put(PhoneTypes.valueOf(keyHome.toUpperCase()), new PhoneNumber(countryCodeHome, numberHome));
                }
                contacts.add(new Contact(firstName, lastName, email, phoneNumbers, isFavorite, contactId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    private void updateDB(int editedContactId) {
        Contact originalContact = null;
        String countryCodeMobile = "", countryCodeWork = "", countryCodeHome = "",
                numberMobile = "", numberWork = "", numberHome = "";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(queryContactFromDB() + "WHERE c.contact_id = " + editedContactId + ";")) {
            while (rs.next()) {
                int contactId = rs.getInt("c.contact_id");
                String firstName = rs.getString("c.first_name");
                String lastName = rs.getString("c.last_name");
                String email = rs.getString("c.email");
                String keyMobile = rs.getString("pnm.phone_type");
                countryCodeMobile = rs.getString("pnm.country_code");
                numberMobile = rs.getString("pnm.phone_number");
                String keyWork = rs.getString("pnw.phone_type");
                countryCodeWork = rs.getString("pnw.country_code");
                numberWork = rs.getString("pnw.phone_number");
                String keyHome = rs.getString("pnh.phone_type");
                countryCodeHome = rs.getString("pnh.country_code");
                numberHome = rs.getString("pnh.phone_number");
                boolean isFavorite = rs.getBoolean("c.is_favorite");

                Map<PhoneTypes, PhoneNumber> phoneNumbers = new LinkedHashMap<>();
                if (keyMobile != null) {
                    phoneNumbers.put(PhoneTypes.valueOf(keyMobile.toUpperCase()), new PhoneNumber(countryCodeMobile, numberMobile));
                }
                if (keyWork != null) {
                    phoneNumbers.put(PhoneTypes.valueOf(keyWork.toUpperCase()), new PhoneNumber(countryCodeWork, numberWork));
                }
                if (keyHome != null) {
                    phoneNumbers.put(PhoneTypes.valueOf(keyHome.toUpperCase()), new PhoneNumber(countryCodeHome, numberHome));
                }

                originalContact = new Contact(firstName, lastName, email, phoneNumbers, isFavorite, contactId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        String editedFirstName = null, editedLastName = null, editedEmail = null, editedPhoneTypeMobile = null,
                editedCountryCodeMobile = null, editedNumberMobile = null, editedPhoneTypeWork = null,
                editedCountryCodeWork = null, editedNumberWork = null, editedPhoneTypeHome = null,
                editedCountryCodeHome = null, editedNumberHome = null;
        boolean editedIsFavorite = false;
        int contactId = 0;
        Map<PhoneTypes, PhoneNumber> editedPhoneNumbers;
        for (Contact contact : contacts) {
            if (contact.getContactId().equals(originalContact.getContactId())) {
                editedFirstName = contact.getFirstName();
                editedLastName = contact.getLastName();
                editedPhoneNumbers = contact.getPhoneNumbers();
                editedEmail = contact.getEmail();
                editedIsFavorite = contact.isFavorite();
                contactId = contact.getContactId();
                for (Map.Entry<PhoneTypes, PhoneNumber> entry : editedPhoneNumbers.entrySet()) {
                    switch (entry.getKey()) {
                        case MOBILE:
                            editedPhoneTypeMobile = entry.getKey().toString();
                            editedCountryCodeMobile = entry.getValue().getCountryCode();
                            editedNumberMobile = entry.getValue().getNumber();
                            break;
                        case WORK:
                            editedPhoneTypeWork = entry.getKey().toString();
                            editedCountryCodeWork = entry.getValue().getCountryCode();
                            editedNumberWork = entry.getValue().getNumber();
                            break;
                        case HOME:
                            editedPhoneTypeHome = entry.getKey().toString();
                            editedCountryCodeHome = entry.getValue().getCountryCode();
                            editedNumberHome = entry.getValue().getNumber();
                            break;
                    }
                }

                editPhoneNumber(editedPhoneNumbers, originalContact.getPhoneNumbers(), editedCountryCodeMobile,
                        countryCodeMobile, editedNumberMobile, numberMobile, editedPhoneTypeMobile, contactId);
                editPhoneNumber(editedPhoneNumbers, originalContact.getPhoneNumbers(), editedCountryCodeWork,
                        countryCodeWork, editedNumberWork, numberWork, editedPhoneTypeWork, editedContactId);
                editPhoneNumber(editedPhoneNumbers, originalContact.getPhoneNumbers(), editedCountryCodeHome,
                        countryCodeHome, editedNumberHome, numberHome, editedPhoneTypeHome, editedContactId);

                addPhoneNumber(editedPhoneNumbers, originalContact.getPhoneNumbers(),
                        editedNumberMobile, numberMobile, editedPhoneTypeMobile, contactId);
                addPhoneNumber(editedPhoneNumbers, originalContact.getPhoneNumbers(),
                        editedNumberWork, numberWork, editedPhoneTypeWork, contactId);
                addPhoneNumber(editedPhoneNumbers, originalContact.getPhoneNumbers(),
                        editedNumberHome, numberHome, editedPhoneTypeHome, contactId);

                removePhoneNumber(editedPhoneNumbers, originalContact.getPhoneNumbers(),
                        editedNumberMobile, numberMobile, contactId);
                removePhoneNumber(editedPhoneNumbers, originalContact.getPhoneNumbers(),
                        editedNumberWork, numberWork, contactId);
                removePhoneNumber(editedPhoneNumbers, originalContact.getPhoneNumbers(),
                        editedNumberHome, numberHome, contactId);
            }
        }

        if (!editedFirstName.equals(originalContact.getFirstName())) {
            String queryFirstName = "UPDATE contacts\n" +
                    "SET first_name = ?\n" +
                    "WHERE contact_id = ?;";
            try (PreparedStatement pstmt = conn.prepareStatement(queryFirstName)) {
                pstmt.setString(1, editedFirstName);
                pstmt.setInt(2, contactId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (!editedLastName.equals(originalContact.getLastName())) {
            String queryLastName = "UPDATE contacts\n" +
                    "SET last_name = ?\n" +
                    "WHERE contact_id = ?;";
            try (PreparedStatement pstmt = conn.prepareStatement(queryLastName)) {
                pstmt.setString(1, editedLastName);
                pstmt.setInt(2, contactId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (!editedEmail.equals(originalContact.getEmail())) {
            String queryEmail = "UPDATE contacts\n" +
                    "SET email = ?\n" +
                    "WHERE contact_id = ?;";
            try (PreparedStatement pstmt = conn.prepareStatement(queryEmail)) {
                pstmt.setString(1, editedEmail);
                pstmt.setInt(2, contactId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (!editedIsFavorite == originalContact.isFavorite()) {
            String queryIsFavorite = "UPDATE contacts\n" +
                    "SET is_favorite = ?\n" +
                    "WHERE contact_id = ?;";
            try (PreparedStatement pstmt = conn.prepareStatement(queryIsFavorite)) {
                pstmt.setString(1, Boolean.toString(editedIsFavorite));
                pstmt.setInt(2, contactId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String queryContactFromDB() {
        StringBuilder sb = new StringBuilder();
        try {
            Files.lines(Paths.get("src/main/resources/contactsQuery.sql")).forEach(line -> sb.append(line).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void editPhoneNumber(Map<PhoneTypes, PhoneNumber> editedPhones, Map<PhoneTypes, PhoneNumber> originalPhones,
                                 String editedCountryCode, String originalCountryCode,
                                 String editedNumber, String originalNumber,
                                 String editedPhoneType, int contactId) {
        if (editedPhones.size() == originalPhones.size() &&
                ((editedCountryCode != null && !editedCountryCode.equals(originalCountryCode)) ||
                        (editedNumber != null && !editedNumber.equals(originalNumber)))) {
            String query = "UPDATE phonenumbers\n" +
                    "SET country_code = ?,\n" +
                    "phone_number = ?\n" +
                    "WHERE phone_type = ? AND contact_id = ?;";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, editedCountryCode);
                pstmt.setString(2, editedNumber);
                pstmt.setString(3, editedPhoneType);
                pstmt.setInt(4, contactId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void addPhoneNumber(Map<PhoneTypes, PhoneNumber> editedPhones, Map<PhoneTypes, PhoneNumber> originalPhones,
                                String editedNumber, String originalNumber, String editedPhoneType, int contactId) {
        if (editedPhones.size() > originalPhones.size() && editedNumber != null && !editedNumber.equals(originalNumber)) {
            String query = "INSERT INTO phonenumbers\n" +
                    "(phone_type,phone_number,contact_id)\n" +
                    "VALUES(?,?,?);";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, editedPhoneType);
                pstmt.setString(2, editedNumber);
                pstmt.setInt(3, contactId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void removePhoneNumber(Map<PhoneTypes, PhoneNumber> editedPhones, Map<PhoneTypes, PhoneNumber> originalPhones,
                                   String editedNumber, String originalNumber, int contactId) {
        if (editedPhones.size() < originalPhones.size() && editedNumber == null) {
            String query = "DELETE FROM phonenumbers\n" +
                    "WHERE contact_id = ? AND phone_number = ?;";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, contactId);
                pstmt.setString(2, originalNumber);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
