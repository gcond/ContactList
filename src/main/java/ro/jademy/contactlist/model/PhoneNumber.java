package ro.jademy.contactlist.model;

import java.util.Objects;

public class PhoneNumber {

    private String countryCode; // ex: +40
    private String number; // ex: 740123456

    public String getCountryCode() {
        return countryCode;
    }

    public String getNumber() {
        return number;
    }

    public PhoneNumber(String countryCode, String number) {
        this.countryCode = countryCode;
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
        return Objects.equals(countryCode, that.countryCode) &&
                Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, number);
    }

    @Override
    public String toString() {
        return countryCode + number;
    }
}
