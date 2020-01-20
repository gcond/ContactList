package ro.jademy.contactlist;

import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

//        Map<String, PhoneNumber> phoneNumbers = new LinkedHashMap<>();
//        phoneNumbers.put("Mobile", new PhoneNumber("+40", "722123123"));
//        phoneNumbers.put("Work", new PhoneNumber("+40", "722321321"));
//
//        Set<Map.Entry<String, PhoneNumber>> entries = phoneNumbers.entrySet();
//        for (Map.Entry<String, PhoneNumber> entry : entries) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
//        }


        // Add contact
//        System.out.print("First Name: ");
//        String firstName = sc.nextLine();
//        System.out.print("Last Name: ");
//        String lastName = sc.nextLine();
//        System.out.print("Email: ");
//        String email = sc.nextLine();
//        System.out.print("Age: ");
//        int age = sc.nextInt();
//        sc.nextLine();
//        System.out.print("Phone type: ");
//        String phoneType = sc.nextLine();
//        System.out.print("Coutry Code: ");
//        String countryCode = sc.nextLine();
//        System.out.print("Phone Number: ");
//        String phoneNumber = sc.nextLine();
//        Map<String, PhoneNumber> newContactPhones = new LinkedHashMap<>();
//        newContactPhones.put(phoneType, new PhoneNumber(countryCode, phoneNumber));
//        System.out.print("Street Name: ");
//        String streetName = sc.nextLine();
//        System.out.print("Street No: ");
//        String streetNo = sc.nextLine();
//        System.out.print("Apartment No: ");
//        int aptNo = sc.nextInt();
//        sc.nextLine();
//        System.out.print("Floor: ");
//        String floor = sc.nextLine();
//        System.out.print("Zip Code: ");
//        String zipCode = sc.nextLine();
//        System.out.print("City: ");
//        String city = sc.nextLine();
//        System.out.print("Country: ");
//        String country = sc.nextLine();
//        Address newContactAddr = new Address(streetName, streetNo, aptNo, floor, zipCode, city, country);
//        System.out.print("Job Title: ");
//        String jobTitle = sc.nextLine();
//        System.out.print("Company Name: ");
//        String companyName = sc.nextLine();
//        System.out.print("Company Street Name: ");
//        String compStreetName = sc.nextLine();
//        System.out.print("Company Street No: ");
//        String compStreetNo = sc.nextLine();
//        System.out.print("Company Apartment No: ");
//        int compAptNo = sc.nextInt();
//        sc.nextLine();
//        System.out.print("Company Floor: ");
//        String compFloor = sc.nextLine();
//        System.out.print("Company Zip Code: ");
//        String compZipCode = sc.nextLine();
//        System.out.print("Company City: ");
//        String compCity = sc.nextLine();
//        System.out.print("Company Country: ");
//        String compCountry = sc.nextLine();
//        Address compAddress = new Address(compStreetName, compStreetNo, compAptNo, compFloor, compZipCode, compCity,
//                compCountry);
//        Company newContactComp = new Company(companyName, compAddress);
//        System.out.print("Added to Favourite: ");
//        boolean isFavourite = sc.nextBoolean();
//        User newContact = new User(firstName, lastName, email, age, newContactPhones, newContactAddr, jobTitle,
//                newContactComp, isFavourite);
//        contacts().add(newContact);


        // list contact list in natural order

        Map<Character, List<User>> groupByInitial =
                contacts().stream().sorted(Comparator.comparing((User user1) -> user1.getFirstName()).thenComparing(user2 -> user2.getLastName()))
                        .collect(Collectors.groupingBy(user -> user.getFirstName().charAt(0), TreeMap::new,
                                Collectors.toList()));

        OptionalInt maxLengh =
                contacts().stream().map(u1 -> (u1.getFirstName() + u1.getLastName()))
                        .mapToInt(s -> s.length())
                        .max();

        Map<Integer, User> numberingUser = new HashMap<>();
        int index = 1;
        for (Map.Entry<Character, List<User>> listEntry : groupByInitial.entrySet()) {

            System.out.printf("\n\033[32;1m%s\033[0m%n\033[35;1m%s\033[0m%n", listEntry.getKey(),
                    new String(new char[maxLengh.getAsInt() + 4]).replace('\0', '='));
//            listEntry.getValue().forEach(u -> System.out.println(u.getFirstName() + " " + u.getLastName()));


            for (User user : listEntry.getValue()) {
                System.out.printf("%s%s %s %s%n\033[35;1m%s\033[0m%n", index, ".", user.getFirstName(),
                        user.getLastName(),
                        new String(new char[maxLengh.getAsInt() + 4]).replace('\0', '-'));
                numberingUser.put(index++, user);
            }
            System.out.printf("%" + (maxLengh.getAsInt() - 7) + "s" + "%s%n", listEntry.getValue().size(), " " +
                    "contact" + "(s)");
        }

        System.out.print("\nEnter the contact order no: ");
        int choice = sc.nextInt();
        sc.nextLine();
        for (Map.Entry<Integer, User> userEntry : numberingUser.entrySet()) {
            int contactNumber = userEntry.getKey();
            if (choice == contactNumber) {
                System.out.println(userEntry.getValue());
            }
        }

//        contacts().stream().sorted(Comparator.comparing((User user1) -> user1.getFirstName()).thenComparing((User user2) -> user2.getLastName()))
//                .forEach(user -> System.out.println(user));

        // list contact list by a given criteria


//        System.out.print("Criteria: ");
//        String criteria = sc.nextLine();
//        contacts().stream().filter(cn -> cn.getCompany().getName().equals(criteria))
//                .sorted(Comparator.comparing((User user1) -> user1.getFirstName()).thenComparing((User user2) -> user2.getLastName()))
//                .forEach(user -> System.out.println(user));


        // display a favorites list

//        contacts().stream().filter(fav -> fav.isFavorite())
//                .sorted(Comparator.comparing((User user1) -> user1.getFirstName()).thenComparing(user2 -> user2.getLastName()))
//                .forEach(user -> System.out.println(user));


        // search by a given or multiple criteria

//        System.out.print("What do you want to find: ");
//        String searchRequest = sc.nextLine();
//        contacts().stream().filter(user -> user.getFirstName().toLowerCase().contains(searchRequest.toLowerCase()) ||
//                user.getLastName().toLowerCase().contains(searchRequest.toLowerCase()) ||
//                user.getEmail().toLowerCase().contains(searchRequest.toLowerCase()) ||
//                user.getAge().toString().contains(searchRequest) ||
//                user.getCompany().getName().toLowerCase().contains(searchRequest.toLowerCase()))
//                .sorted(Comparator.comparing((User user1) -> user1.getFirstName()).thenComparing(user2 -> user2.getLastName()))
//                .forEach(user -> System.out.println(user));

        // display some statistics for the contact list

//        long actorsNo = contacts().stream().filter(user -> user.getJobTitle().equals("Actor") || user.getJobTitle().equals(
//        "Actress")).count();
//        long directorsNo =
//                contacts().stream().filter(user -> user.getJobTitle().equals("Director")).count();
//        System.out.println("There are " + actorsNo + " actors and " + directorsNo + " directors in my contacts.");

//        IntSummaryStatistics statsbyAge = contacts().stream().collect(Collectors.summarizingInt(user -> user.getAge()));
//        System.out.println("Number of the contacts in the list: " + statsbyAge.getCount());

    }

    // create a contact list of users
    public static List<User> contacts() {
        Map<String, PhoneNumber> u1Phones = new LinkedHashMap<>();
        u1Phones.put("Mobile", new PhoneNumber("+40", "722123123"));
        u1Phones.put("Work", new PhoneNumber("+40", "213139175"));

        Map<String, PhoneNumber> u2Phones = new LinkedHashMap<>();
        u2Phones.put("Mobile", new PhoneNumber("+40", "722987987"));
        u2Phones.put("Work", new PhoneNumber("+40", "213139175"));

        Map<String, PhoneNumber> u3Phones = new LinkedHashMap<>();
        u3Phones.put("Mobile", new PhoneNumber("+40", "740341341"));
        u3Phones.put("Work", new PhoneNumber("+40", "735835675"));

        Map<String, PhoneNumber> u4Phones = new LinkedHashMap<>();
        u4Phones.put("Mobile", new PhoneNumber("+40", "745233433"));
        u4Phones.put("Work", new PhoneNumber("+40", "735835675"));

        Map<String, PhoneNumber> u5Phones = new LinkedHashMap<>();
        u5Phones.put("Mobile", new PhoneNumber("+40", "740452852"));
        u5Phones.put("Work", new PhoneNumber("+40", "735835675"));

        Map<String, PhoneNumber> u6Phones = new LinkedHashMap<>();
        u6Phones.put("Mobile", new PhoneNumber("+40", "722987987"));
        u6Phones.put("Work", new PhoneNumber("+40", "213139175"));


        List<User> userList = new ArrayList<>();
        userList.add(new User("Marius", "Manole", "marius.manole@email.com", 41, u1Phones,
                new Address("Nicolae Balcescu Boulevard", "2nd", null, null, "010051", "Bucharest", "Romania"),
                "Actor",
                new Company("Teatrul National Bucuresti", new Address("Nicolae Balcescu Boulevard", "2nd", null, null,
                        "010051", "Bucharest", "Romania")), true));
        userList.add(new User("Mihai", "Calin", "mihai.calin@email.com", 51, u2Phones,
                new Address("Nicolae Balcescu Boulevard", "2nd", null, null, "010051", "Bucharest", "Romania"),
                "Actor",
                new Company("Teatrul National Bucuresti", new Address("Nicolae Balcescu Boulevard", "2nd", null,
                        null,
                        "010051", "Bucharest", "Romania")), false));
        userList.add(new User("Alexandru", "Darie", "alexandru.darie@email.com", 60, u3Phones,
                new Address("Schitu Magureanu Boulevard", "1st", null, null, "050025", "Bucharest", "Romania"),
                "Director",
                new Company("Teatrul Bulandra", new Address("Schitu Magureanu Boulevard", "1st", null,
                        null,
                        "050025", "Bucharest", "Romania")), false));
        userList.add(new User("Oana", "Pellea", "oana.pellea@email.com", 57, u4Phones,
                new Address("Schitu Magureanu Boulevard", "1st", null, null, "050025", "Bucharest", "Romania"),
                "Actress",
                new Company("Teatrul Bulandra", new Address("Schitu Magureanu Boulevard", "1st", null,
                        null,
                        "050025", "Bucharest", "Romania")), false));
        userList.add(new User("Razvan", "Vasilescu", "razvan.vasilescu@email.com", 65, u5Phones,
                new Address("Schitu Magureanu Boulevard", "1st", null, null, "050025", "Bucharest", "Romania"),
                "Actor",
                new Company("Teatrul Bulandra", new Address("Schitu Magureanu Boulevard", "1st", null,
                        null,
                        "050025", "Bucharest", "Romania")), false));
        userList.add(new User("Alexandru", "Dabija", "alexandru.dabija@email.com", 64, u6Phones,
                new Address("Nicolae Balcescu Boulevard", "2nd", null, null, "010051", "Bucharest", "Romania"),
                "Director",
                new Company("Teatrul National Bucuresti", new Address("Nicolae Balcescu Boulevard", "2nd", null,
                        null,
                        "010051", "Bucharest", "Romania")), true));

        return userList;
    }
}
