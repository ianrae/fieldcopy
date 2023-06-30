package org.dnal.fieldcopy.dataclass;

import java.util.Optional;

//used for testing combinations of optional/not-optional sub-objects
public class OptCategory {
    private OptProduct prod;
    private Optional<OptProduct> optProd;
    private String code1;

    public OptProduct getProd() {
        return prod;
    }

    public void setProd(OptProduct prod) {
        this.prod = prod;
    }

    public Optional<OptProduct> getOptProd() {
        return optProd;
    }

    public void setOptProd(Optional<OptProduct> optProd) {
        this.optProd = optProd;
    }

    public String getCode1() {
        return code1;
    }

    public void setCode1(String code1) {
        this.code1 = code1;
    }
}
