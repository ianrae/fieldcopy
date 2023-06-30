package org.dnal.fieldcopy.fieldspec;

import java.util.ArrayList;
import java.util.List;

public class FldChain {
    public List<SingleFld> flds = new ArrayList<>();

    public boolean isSingle() {
        return flds.size() == 1;
    }
    public int size() {
        return flds.size();
    }

    public SingleFld getFirst() {
        return flds.get(0);
    }
    public SingleFld getLast() {
        int n = flds.size();
        return flds.get(n - 1);
    }
    public boolean isEmpty() {
        return flds.isEmpty();
    }
}
