package org.dnal.fieldcopy.dataclass;

import java.util.Optional;

public class OptProduct {
    private Region region;
    private Optional<Region> optRegion;
    private String code2;

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Optional<Region> getOptRegion() {
        return optRegion;
    }

    public void setOptRegion(Optional<Region> optRegion) {
        this.optRegion = optRegion;
    }

    public String getCode2() {
        return code2;
    }

    public void setCode2(String code2) {
        this.code2 = code2;
    }
}
