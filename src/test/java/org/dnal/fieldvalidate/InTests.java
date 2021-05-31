package org.dnal.fieldvalidate;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldvalidate.code.*;
import org.dnal.fieldvalidate.dto.Home;
import org.junit.Test;

/**
 * TODO
 */

public class InTests extends RuleTestBase {


    @Test
    public void testIn() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("points").notNull().in(3,4,5);

        Home home = new Home();
        home.setPoints(51);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "in(3,4,5)");

        home.setPoints(5);
        res = runOK(vb, home);
    }
    @Test
    public void testInDouble() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("weight").notNull().in(3.1,4.2,5.3);

        Home home = new Home();
        home.setWeight(51.0);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "in(3.1,4.2,5.3)");

        home.setWeight(3.1);
        res = runOK(vb, home);
    }
    @Test
    public void testInDoubleDelta() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("weight").notNull().in(3.1,4.2,5.3).delta(0.1);

        Home home = new Home();
        home.setWeight(3.21);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "in(3.1,4.2,5.3)");

        home.setWeight(3.11);
        res = runOK(vb, home);
    }

    @Test
    public void testInString() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("lastName").notNull().in("Jones", "Smith");

        Home home = new Home();
        home.setLastName("Wilson");
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "in(Jones,Smith)");

        home.setLastName("Smith");
        res = runOK(vb, home);
    }

    @Test
    public void testInLong() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("id").notNull().in(3L,4L,5L);

        Home home = new Home();
        home.setId(30);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "in(3,4,5)");

        home.setId(5);
        res = runOK(vb, home);
    }

    @Test
    public void testInBoundaries() {
        chkIn(-1, false);
        chkIn(0, false);
        chkIn(1, false);
        chkIn(2, false);
        chkIn(3, true);
        chkIn(4, true);
        chkIn(5, true);
        chkIn(6, false);
    }



    //--
    private void chkIn(int k, boolean shouldPass) {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("points").notNull().in(3,4,5);

        log(String.format("chk %d", k));
        Home home = new Home();
        home.setPoints(k);
        if (shouldPass) {
            this.runOK(vb, home);
        } else {
            ValidationResults res = runFail(vb, home, 1);
            chkValueErr(res, 0, "in(3,4,5)");
        }
    }

}
