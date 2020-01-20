package ro.jademy.contactlist.model;

import java.util.Objects;

public class Address {

    private String streetName;
    private String streetNumber;
    private Integer apartmentNumber;
    private String floor;
    private String zipCode;
    private String city;
    private String country;

    public String getStreetName() {
        return streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public Integer getApartmentNumber() {
        return apartmentNumber;
    }

    public String getFloor() {
        return floor;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public Address(String streetName, String streetNumber, Integer apartmentNumber, String floor, String zipCode,
                   String city, String country) {
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.apartmentNumber = apartmentNumber;
        this.floor = floor;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(streetName, address.streetName) &&
                Objects.equals(streetNumber, address.streetNumber) &&
                Objects.equals(apartmentNumber, address.apartmentNumber) &&
                Objects.equals(floor, address.floor) &&
                Objects.equals(zipCode, address.zipCode) &&
                Objects.equals(city, address.city) &&
                Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(streetName, streetNumber, apartmentNumber, floor, zipCode, city, country);
    }

    @Override
    public String toString() {
        return streetNumber + " " +
                streetName +
                (apartmentNumber != null ? ", Apartment no " + apartmentNumber : "") +
                (floor != null ? ", " + floor + " Floor " : "") +
                ", Zip Code " + zipCode +
                ", " + city +
                ", " + country;
    }
}
