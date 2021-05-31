package org.dnal.fieldvalidate.dto;

import java.util.ArrayList;
import java.util.List;

public class Home {
    private int points;
    private String[] names;
    private String lastName;
    private long id;
    private Double weight;
    private Address addr;
    private List<Integer> zones = new ArrayList<>();
    private Integer[] arSizes;
    private Color color;
    private String colorStr;

    public Address getAddr() {
        return addr;
    }

    public void setAddr(Address addr) {
        this.addr = addr;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<Integer> getZones() {
        return zones;
    }

    public void setZones(List<Integer> zones) {
        this.zones = zones;
    }

    public Integer[] getArSizes() {
        return arSizes;
    }

    public void setArSizes(Integer[] arSizes) {
        this.arSizes = arSizes;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getColorStr() {
        return colorStr;
    }

    public void setColorStr(String colorStr) {
        this.colorStr = colorStr;
    }
}
