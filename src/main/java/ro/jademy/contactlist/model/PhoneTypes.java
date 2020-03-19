package ro.jademy.contactlist.model;

public enum PhoneTypes {

    MOBILE("Mobile"),
    WORK("Work"),
    HOME("Home");

    private String displayPhoneType;

    public String getDisplayPhoneType() {
        return displayPhoneType;
    }

    PhoneTypes(String displayPhoneType) {
        this.displayPhoneType = displayPhoneType;
    }
}
