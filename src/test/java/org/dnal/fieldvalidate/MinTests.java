package org.dnal.fieldvalidate;

import static org.junit.Assert.assertEquals;

import org.apache.commons.beanutils.PropertyUtils;
import org.dnal.fieldcopy.BaseTest;
import org.dnal.fieldvalidate.code.*;
import org.dnal.fieldvalidate.dto.Address;
import org.dnal.fieldvalidate.dto.Color;
import org.dnal.fieldvalidate.dto.Home;
import org.junit.Test;

import java.util.List;

/**
 * TODO
 */

public class MinTests extends BaseTest {

    @Test
    public void testMin() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("points").notNull().min(50);

        Home home = new Home();
        home.setPoints(30);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "min(50)");

        home.setPoints(50);
        res = runOK(vb, home);
    }
    @Test
    public void testMinLong() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("id").notNull().min(50);

        Home home = new Home();
        home.setId(30);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "min(50)");

        home.setId(50);
        res = runOK(vb, home);
    }
    @Test
    public void testMinDouble() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("weight").notNull().min(50.0);

        Home home = new Home();
        home.setWeight(30.0);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "min(50.0)");

        home.setWeight(50.0);
        res = runOK(vb, home);
    }

    @Test
    public void testMinBoundaries() {
        chkMin(48, false);
        chkMin(49, false);
        chkMin(50, true);
        chkMin(51, true);
    }


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


    //--
    private ErrorMessageBuilder customMessageBuilder;


    private ValidationResults runOK(ValidateBuilder vb, Home home) {
        Validator runner = vb.build();
        runner.setCustomErrorMessageBuilder(customMessageBuilder);
        ValidationResults res = runner.validate(home);
        assertEquals(true, res.hasNoErrors());
        return res;
    }

    private ValidationResults runFail(ValidateBuilder vb, Home home, int size) {
        Validator runner = vb.build();
        runner.setCustomErrorMessageBuilder(customMessageBuilder);
        ValidationResults res = runner.validate(home);
        chkFail(res, size);
        return res;
    }
    private void runFailWithException(ValidateBuilder vb, Home home, int size) {
        Validator runner = vb.build();
        runner.setCustomErrorMessageBuilder(customMessageBuilder);
        boolean thrown = false;
        try {
            runner.validate(home);
        } catch (FieldValidateException e) {
//            e.printStackTrace();
            System.out.println("EXCEPTION: " + e.getMessage());
            thrown = true;
        }
        assertEquals(true, thrown);
    }

    private void chkFail(ValidationResults res, int expected) {
        for(FieldError err: res.errL) {
            log(err.errMsg);
        }
        assertEquals(false, res.hasNoErrors());
        assertEquals(expected, res.errL.size());
    }
    private void chkValueErr(ValidationResults res, int index, String expected) {
        assertEquals(false, res.hasNoErrors());
        FieldError err = res.errL.get(index);
        assertEquals(true, err.errMsg.contains(expected));
    }
    private void chkMin(int k, boolean shouldPass) {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("points").notNull().min(50);

        log(String.format("chk %d", k));
        Home home = new Home();
        home.setPoints(k);
        if (shouldPass) {
            this.runOK(vb, home);
        } else {
            ValidationResults res = runFail(vb, home, 1);
            chkValueErr(res, 0, "min(50)");
        }
    }

}
