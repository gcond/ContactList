package ro.jademy.contactlist.model;

import java.util.*;
import java.util.stream.Collectors;

public class Contact implements Cloneable {

    private static final String DEFAULT_PHONE_NUMBER_GROUP = "Mobile";

    private String firstName;
    private String lastName;
    private String email;
    private Integer age;

    private Map<String, PhoneNumber> phoneNumbers;

    private Address address;

    private String jobTitle;
    private Company company;

    private boolean isFavorite;

    private Integer contactId;

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public Map<String, PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }
    public void setPhoneNumbers(Map<String, PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
    public void setNumber(String phoneNumber) {
        if (phoneNumbers.values().stream().findAny().isPresent()) {
            this.phoneNumbers.values().stream().findAny().get().setNumber(phoneNumber);
        }
    }

    public Address getAddress() {
        return address;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public Company getCompany() {
        return company;
    }
    public void setCompany(Company company) {
        this.company = company;
    }
    public void setCompanyName(String companyName) {
        this.company.setName(companyName);
    }

    public boolean isFavorite() {
        return isFavorite;
    }
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public Integer getContactId() {
        return contactId;
    }

    public Contact(String firstName, String lastName, String email, Integer age, Map<String, PhoneNumber> phoneNumbers,
                   Address address, String jobTitle, Company company, boolean isFavorite, Integer contactId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
        this.phoneNumbers = phoneNumbers;
        this.address = address;
        this.jobTitle = jobTitle;
        this.company = company;
        this.isFavorite = isFavorite;
        this.contactId = contactId;
    }

    public Contact(Contact contact) {
        this.firstName = contact.firstName;
        this.lastName = contact.lastName;
        this.email = contact.email;
        this.age = contact.age;
        this.phoneNumbers = copyPhoneNumbers(contact.getPhoneNumbers());
        this.address = contact.address;
        this.jobTitle = contact.jobTitle;
        this.company = contact.company;
        this.isFavorite = contact.isFavorite;
        this.contactId = contact.contactId;
    }

    private Map<String, PhoneNumber> copyPhoneNumbers(Map<String, PhoneNumber> original) {
        Map<String, PhoneNumber> newPhones = new LinkedHashMap<>();
        for (Map.Entry<String, PhoneNumber> entry : original.entrySet()) {
            newPhones.put(entry.getKey(), new PhoneNumber(entry.getValue()));
        }

        return newPhones;
    }

//    public static Contact newInstance(Contact contact) {
//        return new Contact(contact);
//    }
//
//    public Contact clone() throws CloneNotSupportedException {
//        Contact contact = (Contact) super.clone();
//        contact.phoneNumbers = new LinkedHashMap<>(phoneNumbers);
//        return contact;
//    }

    public Contact(String firstName, String lastName, String email, Integer age, Map<String, PhoneNumber> phoneNumbers
            , Address address, String jobTitle, Company company, Integer contactId) {
        this(firstName, lastName, email, age, phoneNumbers, address, jobTitle, company, false, contactId);
    }

    public Contact(String firstName, String lastName, String email, Map<String, PhoneNumber> phoneNumbers
            , boolean isFavorite, Integer contactId) {
        this(firstName, lastName, email, null, phoneNumbers, null, null, null, isFavorite
                , contactId);
    }



    public Contact(String firstName, String lastName, String email, PhoneNumber phoneNumber, boolean isFavorite,
                   Integer contactId) {

        this(firstName, lastName, email, null, new LinkedHashMap<>(), null, null, null,
                isFavorite, contactId);

        this.phoneNumbers.put(DEFAULT_PHONE_NUMBER_GROUP, phoneNumber); // add the phone number to a default phone number group
    }

    public Contact(String firstName, String lastName, String email, PhoneNumber phoneNumber, Integer contactId) { // simple constructor,
        // but requiring a PhoneNumber object

        this(firstName, lastName, email, phoneNumber, false, contactId);
    }

    public Contact(String firstName, String lastName, String email, String phoneNumber, Integer contactId) { // simplest
        // constructor,
        // requiring only the minimal necessary information in literal form

        this(firstName, lastName, email, new PhoneNumber(phoneNumber), false, contactId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return isFavorite == contact.isFavorite &&
                Objects.equals(firstName, contact.firstName) &&
                Objects.equals(lastName, contact.lastName) &&
                Objects.equals(email, contact.email) &&
                Objects.equals(age, contact.age) &&
                Objects.equals(phoneNumbers, contact.phoneNumbers) &&
                Objects.equals(address, contact.address) &&
                Objects.equals(jobTitle, contact.jobTitle) &&
                Objects.equals(company, contact.company) &&
                Objects.equals(contactId, contact.contactId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, age, phoneNumbers, address, jobTitle, company, isFavorite, contactId);
    }

//    @Override
//    public String toString() {
//        return "\033[32;1;4m\n" + firstName + " " + lastName + (isFavorite ? " ★\033[0m" : "\033[0m") + "\n" /*+
//                new String(new char[firstName.length() + lastName.length() + (isFavorite ? 3 : 1)]).replace('\0',
//                        '-') + "\033[0m\n" */+
//                "\033[35;2mPhone Numbers: \n\033[0m" + getPhoneNumbersAsString() + "\n" +
//                (email != null && !email.equals("") ? "\033[35;2mEmail: \n\033[0m" + email + "\n" : "") +
//                "\033[35;2mAge: \n\033[0m" + age + "\n" +
//                (address.getStreetNumber() != null ||
//                        address.getStreetName() != null ||
//                        address.getApartmentNumber() != null ||
//                        address.getFloor() != null ||
//                        address.getZipCode() != null ||
//                        address.getCity() != null ||
//                        address.getCountry() != null ? "\033[35;2mAddress: \n\033[0m" + address.toString() + "\n" : "") +
//                (jobTitle != null ? "\033[35;2mJobTitle: \n\033[0m" + jobTitle + "\n" : "") +
//                company + "\n";
//    }

    @Override
    public String toString() {
        return "\033[32;1;4m\n" + contactId + " " + firstName + " " + lastName + (isFavorite ? " ★\033[0m" : "\033[0m") +
                "\n" /*+
                new String(new char[firstName.length() + lastName.length() + (isFavorite ? 3 : 1)]).replace('\0',
                        '-') + "\033[0m\n" */+
                "\033[35;2mPhone Numbers: \n\033[0m" + getPhoneNumbersAsString() + "\n" +
                (email != null && !email.equals("") ? "\033[35;2mEmail: \n\033[0m" + email + "\n" : "");
    }




    public List<String> getPhoneNumbersList() {
        return phoneNumbers.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.toList());
    }

    public String getPhoneNumbersAsString() {
        return getPhoneNumbersList().toString().replace("[", "").replace(", ", "\n").replace("]", "");
    }
}
