package org.dnal.fieldcopy.util;

import java.util.ArrayList;
import java.util.List;

public class StrListCreator {
    private List<String> list = new ArrayList<>();

    public void o(String fmt, String...args) {
        String s = String.format(fmt, args);
//        s += "\n";
        list.add(s);
    }
    /**
     * can't use o() if args contains % chars, so use this method
     * @param s string to output
     * @return the input string s
     */
    public void addStr(String s) {
//        s += "\n";
        list.add(s);
    }

    public void oIndented(int indent, String fmt, String...args) {
        String s = String.format(fmt, args);
        if (indent > 0) {
            s = StringUtil.createSpace(indent) + s;
        }
        list.add(s);
    }
    /**
     * can't use o() if args contains % chars, so use this method
     * @param s string to output
     * @return the input string s
     */
    public void addStrIndented(int indent, String s) {
        if (indent > 0) {
            s = StringUtil.createSpace(indent) + s;
        }
        list.add(s);
    }

    public void nl() {
        addStr("");
    }


    public List<String> getLines() {
        return list;
    }

}
