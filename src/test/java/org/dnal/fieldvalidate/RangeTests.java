package org.dnal.fieldvalidate;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldvalidate.code.*;
import org.dnal.fieldvalidate.dto.Home;
import org.junit.Test;

/**
 * TODO
 */

public class RangeTests extends RuleTestBase {

    @Test
    public void testRange() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("points").notNull().range(1,10);

        Home home = new Home();
        home.setPoints(51);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "range(1,10)");

        home.setPoints(0);
        res = runFail(vb, home, 1);
        chkValueErr(res, 0, "range(1,10)");

        home.setPoints(1);
        res = runOK(vb, home);
        home.setPoints(10);
        res = runOK(vb, home);
    }
    @Test
    public void testRangeBad() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("lastName").notNull().range(1, 50);

        Home home = new Home();
        home.setLastName("bob");
        runFailWithException(vb, home, 1);
    }
    @Test
    public void testRangeLong() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("id").notNull().range(10,50);

        Home home = new Home();
        home.setId(60);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "range(10,50)");

        home.setId(50);
        res = runOK(vb, home);
    }
    @Test
    public void testRangeDouble() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("weight").notNull().range(10.0, 50.0);

        Home home = new Home();
        home.setWeight(60.0);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "range(10.0,50.0)");

        home.setWeight(50.0);
        res = runOK(vb, home);
    }

    @Test
    public void testRangeBoundaries() {
        chkRange(8, false);
        chkRange(9, false);
        chkRange(10, true);
        chkRange(11, true);
        chkRange(48, true);
        chkRange(49, true);
        chkRange(50, true);
        chkRange(51, false);
    }




    //--
    private void chkRange(int k, boolean shouldPass) {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("points").notNull().range(10,50);

        log(String.format("chk %d", k));
        Home home = new Home();
        home.setPoints(k);
        if (shouldPass) {
            this.runOK(vb, home);
        } else {
            ValidationResults res = runFail(vb, home, 1);
            chkValueErr(res, 0, "range(10,50)");
        }
    }

}
