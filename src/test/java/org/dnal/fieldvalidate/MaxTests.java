package org.dnal.fieldvalidate;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldvalidate.code.*;
import org.dnal.fieldvalidate.dto.Home;
import org.junit.Test;

/**
 * TODO
 */

public class MaxTests extends RuleTestBase {

    @Test
    public void testMax() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("points").notNull().max(50);

        Home home = new Home();
        home.setPoints(51);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "max(50)");

        home.setPoints(50);
        res = runOK(vb, home);
    }
    @Test
    public void testMaxBad() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("lastName").notNull().max(50);

        Home home = new Home();
        home.setLastName("bob");
        runFailWithException(vb, home, 1);
    }
    @Test
    public void testMaxLong() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("id").notNull().max(50);

        Home home = new Home();
        home.setId(60);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "max(50)");

        home.setId(50);
        res = runOK(vb, home);
    }
    @Test
    public void testMaxDouble() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("weight").notNull().max(50.0);

        Home home = new Home();
        home.setWeight(60.0);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "max(50.0)");

        home.setWeight(50.0);
        res = runOK(vb, home);
    }

    @Test
    public void testMaxBoundaries() {
        chkMax(48, true);
        chkMax(49, true);
        chkMax(50, true);
        chkMax(51, false);
    }




    //--
    private void chkMax(int k, boolean shouldPass) {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("points").notNull().max(50);

        log(String.format("chk %d", k));
        Home home = new Home();
        home.setPoints(k);
        if (shouldPass) {
            this.runOK(vb, home);
        } else {
            ValidationResults res = runFail(vb, home, 1);
            chkValueErr(res, 0, "max(50)");
        }
    }

}
