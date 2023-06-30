package org.dnal.fieldcopy.dataclass;

public class Address {
    private String street1;
    private String city;
    private Customer backRef; //bad idea!
    private boolean flag1;

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Customer getBackRef() {
        return backRef;
    }

    public void setBackRef(Customer backRef) {
        this.backRef = backRef;
    }

    public boolean isFlag1() {
        return flag1;
    }

    public void setFlag1(boolean flag1) {
        this.flag1 = flag1;
    }
}
