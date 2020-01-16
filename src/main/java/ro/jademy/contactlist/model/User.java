package ro.jademy.contactlist.model;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class User {

    private String firstName;
    private String lastName;
    private String email;
    private Integer age;

    private Map<String, PhoneNumber> phoneNumbers;
    private Address address;

    private String jobTitle;
    private Company company;

    private boolean isFavorite;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }

    public Map<String, PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public User(String firstName, String lastName, String email, Integer age, Map<String, PhoneNumber> phoneNumbers,
                Address address, String jobTitle, Company company, boolean isFavorite) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
        this.phoneNumbers = phoneNumbers;
        this.address = address;
        this.jobTitle = jobTitle;
        this.company = company;
        this.isFavorite = isFavorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isFavorite == user.isFavorite &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(email, user.email) &&
                Objects.equals(age, user.age) &&
                Objects.equals(phoneNumbers, user.phoneNumbers) &&
                Objects.equals(address, user.address) &&
                Objects.equals(jobTitle, user.jobTitle) &&
                Objects.equals(company, user.company);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, age, phoneNumbers, address, jobTitle, company, isFavorite);
    }

    String underline = new String(new char[15]).replace('\0', '-');

    @Override
    public String toString() {
        return firstName + " " + lastName + "\n" +
                underline + "\n" +
                "PhoneNumbers: " + getPhoneNumbersList() + "\n" +
                "Email: " + email + "\n" +
                "Age: " + age + "\n" +
                "Address: " + address.toString() + "\n" +
                "JobTitle: " + jobTitle + "\n" +
                company + "\n" +
                "isFavorite: " + isFavorite + "\n";
    }

    public String getPhoneNumbersList() {
        Set<Map.Entry<String, PhoneNumber>> entries = phoneNumbers.entrySet();
        for (Map.Entry<String, PhoneNumber> entry : entries) {
            return entry.getKey() + ": " + entry.getValue();
        }
        return null;
    }

    public void letter (){
        System.out.println(firstName.toUpperCase().charAt(0));
    }
}
