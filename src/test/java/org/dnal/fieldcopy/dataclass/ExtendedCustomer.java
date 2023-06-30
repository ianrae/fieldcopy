package org.dnal.fieldcopy.dataclass;

public class ExtendedCustomer extends Customer {
    private String favColor;
    private ZoneAddress zoneAddr;

    public String getFavColor() {
        return favColor;
    }

    public void setFavColor(String favColor) {
        this.favColor = favColor;
    }

    public ZoneAddress getZoneAddr() {
        return zoneAddr;
    }

    public void setZoneAddr(ZoneAddress zoneAddr) {
        this.zoneAddr = zoneAddr;
    }
}
