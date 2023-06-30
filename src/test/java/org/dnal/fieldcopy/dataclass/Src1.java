package org.dnal.fieldcopy.dataclass;

import java.util.List;
import java.util.Optional;

public class Src1 {
    public int n1;
    public String s2;
    public Inner1 inner1;
    public Color col1;
    public Integer n2;

    public int getN1() {
        return n1;
    }

    public void setN1(int n1) {
        this.n1 = n1;
    }

    public String getS2() {
        return s2;
    }

    public void setS2(String s2) {
        this.s2 = s2;
    }

    public Inner1 getInner1() {
        return inner1;
    }

    public void setInner1(Inner1 inner1) {
        this.inner1 = inner1;
    }

    public Color getCol1() {
        return col1;
    }

    public void setCol1(Color col1) {
        this.col1 = col1;
    }

    public Optional<List<String>> profiles;

    public Integer getN2() {
        return n2;
    }

    public void setN2(Integer n2) {
        this.n2 = n2;
    }
}
