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

public class RuleTestBase extends BaseTest {


    //--
    protected ErrorMessageBuilder customMessageBuilder;


    protected ValidationResults runOK(ValidateBuilder vb, Home home) {
        Validator runner = vb.build();
        runner.setCustomErrorMessageBuilder(customMessageBuilder);
        ValidationResults res = runner.validate(home);
        assertEquals(true, res.hasNoErrors());
        return res;
    }

    protected ValidationResults runFail(ValidateBuilder vb, Home home, int size) {
        Validator runner = vb.build();
        runner.setCustomErrorMessageBuilder(customMessageBuilder);
        ValidationResults res = runner.validate(home);
        chkFail(res, size);
        return res;
    }
    protected void runFailWithException(ValidateBuilder vb, Home home, int size) {
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

    protected void chkFail(ValidationResults res, int expected) {
        for(FieldError err: res.errL) {
            log(err.errMsg);
        }
        assertEquals(false, res.hasNoErrors());
        assertEquals(expected, res.errL.size());
    }
    protected void chkValueErr(ValidationResults res, int index, String expected) {
        assertEquals(false, res.hasNoErrors());
        FieldError err = res.errL.get(index);
        assertEquals(true, err.errMsg.contains(expected));
    }
}
