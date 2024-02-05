package org.dnal.fieldcopy.dataclass;

import java.time.ZonedDateTime;

//all fields are strings
public class CustomerMiniStr {
    private String firstName;
    private String numPoints;
    private String zdt;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getNumPoints() {
        return numPoints;
    }

    public void setNumPoints(String numPoints) {
        this.numPoints = numPoints;
    }

    public String getZdt() {
        return zdt;
    }

    public void setZdt(String zdt) {
        this.zdt = zdt;
    }
}
