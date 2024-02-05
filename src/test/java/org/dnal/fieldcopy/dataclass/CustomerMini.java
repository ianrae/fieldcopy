package org.dnal.fieldcopy.dataclass;

import java.time.ZonedDateTime;

public class CustomerMini {
    private String firstName;
    private int numPoints;
    private ZonedDateTime zdt;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getNumPoints() {
        return numPoints;
    }

    public void setNumPoints(int numPoints) {
        this.numPoints = numPoints;
    }

    public ZonedDateTime getZdt() {
        return zdt;
    }

    public void setZdt(ZonedDateTime zdt) {
        this.zdt = zdt;
    }
}
