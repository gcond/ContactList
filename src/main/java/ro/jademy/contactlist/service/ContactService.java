package ro.jademy.contactlist.service;

import ro.jademy.contactlist.model.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ContactService {

    List<Contact> getContacts();

    Optional<Contact> getContactById(int contactId);

    void addContact(Contact contact, String number);

    void editContact(String firstName,
                     String lastName,
                     String email,
                     Integer age, Map<PhoneTypes, PhoneNumber> phoneNumbers,
                     Address address,
                     String jobTitle,
                     Company company,
                     boolean isFavorite, int contactId);

    void removeContact(int contactId);

    void removeAllContacts();

    List<Contact> getSearchResult(String query, List<Contact> list);

    Map<Character, List<Contact>> groupByInitial(List<Contact> list);

    List<Contact> getFavorites();

    Path backupsDir();
    void doBackup();
    void restoreFromBackup(String source);

}
