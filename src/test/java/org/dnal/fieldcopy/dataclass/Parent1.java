package org.dnal.fieldcopy.dataclass;

import java.util.Optional;

public class Parent1 {
    private Child1 child1;
    private Optional<Child1> maybeChild;

    public Child1 getChild1() {
        return child1;
    }

    public void setChild1(Child1 child1) {
        this.child1 = child1;
    }

    public Optional<Child1> getMaybeChild() {
        return maybeChild;
    }

    public void setMaybeChild(Optional<Child1> maybeChild) {
        this.maybeChild = maybeChild;
    }
}
