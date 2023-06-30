package org.dnal.fieldcopy.dataclass;

import java.util.Optional;

public class Region {
    private String code;
    private Optional<String> optCode;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Optional<String> getOptCode() {
        return optCode;
    }

    public void setOptCode(Optional<String> optCode) {
        this.optCode = optCode;
    }
}
