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

        // create a contact list of users
        Map<String, PhoneNumber> u1Phones = new HashMap<>();
        u1Phones.put("Mobile", new PhoneNumber("+40", "722123123"));
        u1Phones.put("Work", new PhoneNumber("+40", "213139175"));

        Map<String, PhoneNumber> u2Phones = new HashMap<>();
        u2Phones.put("Mobile", new PhoneNumber("+40", "722987987"));
        u2Phones.put("Work", new PhoneNumber("+40", "213139175"));

        Map<String, PhoneNumber> u3Phones = new HashMap<>();
        u3Phones.put("Mobile", new PhoneNumber("+40", "740341341"));
        u3Phones.put("Work", new PhoneNumber("+40", "735835675"));

        Map<String, PhoneNumber> u4Phones = new HashMap<>();
        u4Phones.put("Mobile", new PhoneNumber("+40", "745233433"));
        u4Phones.put("Work", new PhoneNumber("+40", "735835675"));

        Map<String, PhoneNumber> u5Phones = new HashMap<>();
        u5Phones.put("Mobile", new PhoneNumber("+40", "740452852"));
        u5Phones.put("Work", new PhoneNumber("+40", "735835675"));

        Map<String, PhoneNumber> u6Phones = new HashMap<>();
        u6Phones.put("Mobile", new PhoneNumber("+40", "722987987"));
        u6Phones.put("Work", new PhoneNumber("+40", "213139175"));



        List<User> userList = new ArrayList<>();
        userList.add(new User("Marius", "Manole", "marius.manole@email.com", 41, u1Phones,
                new Address("Nicolae Balcescu Boulevard", "2nd", 1, "Ground Floor", "010051", "Bucharest", "Romania"),
                "Actor",
                new Company("Teatrul National Bucuresti", new Address("Nicolae Balcescu Boulevard", "2nd", 1, "Ground" +
                        " Floor",
                        "010051", "Bucharest", "Romania")), true));
        userList.add(new User("Mihai", "Calin", "mihai.calin@email.com", 51, u2Phones,
                new Address("Nicolae Balcescu Boulevard", "2nd", 1, "Ground Floor", "010051", "Bucharest", "Romania"),
                "Actor",
                new Company("Teatrul National Bucuresti", new Address("Nicolae Balcescu Boulevard", "2nd", 1,
                        "Ground Floor",
                        "010051", "Bucharest", "Romania")), false));
        userList.add(new User("Alexandru", "Darie", "alexandru.darie@email.com", 60, u3Phones,
                new Address("Schitu Magureanu Boulevard", "1st", 1, "Ground Floor", "050025", "Bucharest", "Romania"),
                "Director",
                new Company("Teatrul Bulandra", new Address("Schitu Magureanu Boulevard", "1st", 1,
                        "Ground Floor",
                        "050025", "Bucharest", "Romania")), false));
        userList.add(new User("Oana", "Pellea", "oana.pellea@email.com", 57, u4Phones,
                new Address("Schitu Magureanu Boulevard", "1st", 1, "Ground Floor", "050025", "Bucharest", "Romania"),
                "Actress",
                new Company("Teatrul Bulandra", new Address("Schitu Magureanu Boulevard", "1st", 1,
                        "Ground Floor",
                        "050025", "Bucharest", "Romania")), false));
        userList.add(new User("Razvan", "Vasilescu", "razvan.vasilescu@email.com", 65, u5Phones,
                new Address("Schitu Magureanu Boulevard", "1st", 1, "Ground Floor", "050025", "Bucharest", "Romania"),
                "Actor",
                new Company("Teatrul Bulandra", new Address("Schitu Magureanu Boulevard", "1st", 1,
                        "Ground Floor",
                        "050025", "Bucharest", "Romania")), false));
        userList.add(new User("Alexandru", "Dabija", "alexandru.dabija@email.com", 64, u6Phones,
                new Address("Nicolae Balcescu Boulevard", "2nd", 1, "Ground Floor", "010051", "Bucharest", "Romania"),
                "Director",
                new Company("Teatrul National Bucuresti", new Address("Nicolae Balcescu Boulevard", "2nd", 1, "Ground" +
                        " Floor",
                        "010051", "Bucharest", "Romania")), true));


        // list contact list in natural order

        userList.stream().sorted(Comparator.comparing(user -> user.getFirstName()))
                .sorted(Comparator.comparing(user -> user.getLastName()))
                .forEach(user -> System.out.println(user));

        // list contact list by a given criteria


        System.out.print("Criteria: ");
        String criteria = sc.nextLine();
        userList.stream().filter(cn -> cn.getCompany().getName().equals(criteria))
                .sorted(Comparator.comparing(user -> user.getFirstName()))
                .sorted(Comparator.comparing(user -> user.getLastName()))
                .forEach(user -> System.out.println(user));


        // display a favorites list

//        char firstLetter = userList.stream().filter(user -> user.getFirstName().charAt(0));

//        userList.stream().filter(fav -> fav.isFavorite())
//                .sorted(Comparator.comparing(user -> user.getFirstName()))
//                .sorted(Comparator.comparing(user -> user.getLastName()))
//                .forEach(user -> System.out.println(user));


        // search by a given or multiple criteria

        System.out.print("What do you want to find: ");
        String searchRequest = sc.nextLine();
        userList.stream().filter(user -> user.getFirstName().toLowerCase().contains(searchRequest.toLowerCase()) ||
                user.getLastName().toLowerCase().contains(searchRequest.toLowerCase()) ||
                user.getEmail().toLowerCase().contains(searchRequest.toLowerCase()) ||
                user.getAge().toString().contains(searchRequest) ||
                user.getCompany().getName().toLowerCase().contains(searchRequest.toLowerCase()))
                .forEach(user -> System.out.println(user));

        // display some statistics for the contact list

//        System.out.println(userList.size() + " Contacts");

        long actorsNo = userList.stream().filter(user -> user.getJobTitle().equals("Actor") || user.getJobTitle().equals(
                        "Actress")).count();
        long directorsNo =
                userList.stream().filter(user -> user.getJobTitle().equals("Director")).count();
        System.out.println("There are " + actorsNo + " actors and " + directorsNo + " directors in my contacts.");

    }
}