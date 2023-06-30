package org.dnal.fieldcopy.dataclass;

import java.time.LocalDate;
import java.util.Optional;

public class Child1 {
    private boolean flag1;
    private String city;
    private Color someColor;
    private LocalDate moveDate;
    private Optional<String> optCity;

    public boolean isFlag1() {
        return flag1;
    }

    public void setFlag1(boolean flag1) {
        this.flag1 = flag1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Color getSomeColor() {
        return someColor;
    }

    public void setSomeColor(Color someColor) {
        this.someColor = someColor;
    }

    public LocalDate getMoveDate() {
        return moveDate;
    }

    public void setMoveDate(LocalDate moveDate) {
        this.moveDate = moveDate;
    }

    public Optional<String> getOptCity() {
        return optCity;
    }

    public void setOptCity(Optional<String> optCity) {
        this.optCity = optCity;
    }
}
