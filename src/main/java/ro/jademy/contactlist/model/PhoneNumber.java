package ro.jademy.contactlist.model;

import java.util.Objects;

public class PhoneNumber {

    private String countryCode; // ex: +40
    private String number; // ex: 7401234

    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }

    public PhoneNumber(String number) {
        this.countryCode = "+40"; // default country code
        this.number = number;
    }

    public PhoneNumber(String countryCode, String number) {
        this.countryCode = countryCode;
        this.number = number;
    }

    public PhoneNumber(PhoneNumber original) {
        this.countryCode = original.countryCode;
        this.number = original.number;
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
