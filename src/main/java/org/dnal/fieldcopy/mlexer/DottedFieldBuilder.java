package org.dnal.fieldcopy.mlexer;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DottedFieldBuilder {
    private final int lastSrcIndex;
    private final int lastDestIndex;
    private List<String> outlist1 = new ArrayList<>();
    private List<String> outlist2 = new ArrayList<>();
    private int maxN;

    public DottedFieldBuilder(String srcText, List<String> srcPieces, String destText, List<String> destPieces) {
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        list1.add(srcText);
        list2.add(destText);
        if (srcPieces != null) {
            list1.addAll(srcPieces);
        }
        if (destPieces != null) {
            list2.addAll(destPieces);
        }

        //remove . elements
        list1 = removePeriod(list1);
        list2 = removePeriod(list2);

        lastSrcIndex = list1.size() - 1;
        lastDestIndex = list2.size() - 1;

        int n1 = CollectionUtils.isEmpty(list1) ? 0 : list1.size();
        int n2 = CollectionUtils.isEmpty(list2) ? 0 : list2.size();
        int n = Math.max(n1, n2);
        this.maxN = n;

        int i1 = n1 - 1;
        int i2 = n2 - 1;
        for (int k = n - 1; k >= 0; k--) {
            String s1 = null;
            if (i1 >= 0 && i1 < list1.size()) {
                s1 = list1.get(i1--);
            }
            String s2 = null;
            if (i2 >= 0 && i2 < list2.size()) {
                s2 = list2.get(i2--);
            }

            outlist1.add(s1);
            outlist2.add(s2);
        }

        Collections.reverse(outlist1);
        Collections.reverse(outlist2);
    }

    private List<String> removePeriod(List<String> list1) {
        return list1.stream().filter(x -> x != null && !x.equals(".")).collect(Collectors.toList());
    }

    public int getMax() {
        return maxN;
    }

    public String getIthSrc(int i) {
        return outlist1.get(i);
    }

    public String getIthDest(int i) {
        return outlist2.get(i);
    }

    public int getLastSrcIndex() {
        return lastSrcIndex;
    }

    public int getLastDestIndex() {
        return lastDestIndex;
    }

    public List<String> getSrcFieldList() {
        return outlist1;
    }
    public List<String> getDestFieldList() {
        return outlist2;
    }
}
