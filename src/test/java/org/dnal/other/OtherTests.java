package org.dnal.other;

import org.dnal.fieldcopy.dataclass.*;
import org.dnal.fieldcopy.types.ClassTypeHelper;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static java.util.Objects.isNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OtherTests {

    @Test
    public void test() {
        assertEquals(1, 1);
    }

    @Test
    public void testEnum() {
        Color color = Color.BLUE;

        //java.lang.IllegalArgumentException if invalid
        String s = "BLUE";
        Color col = Color.valueOf(s);
        assertEquals(color, col);

        col = Enum.valueOf(Color.class, s);
        assertEquals(color, col);

        byte bb = 44;
        assertEquals(44, bb);
    }

    @Test
    public void testAddrCity() {
//        Customer src = new Customer();
//        Customer dest = new Customer();
//
//        Address tmp1 = src.getAddr();
//        String tmp2 = tmp1.getCity();
//
//        Address tmp3 = (isNull(dest.getAddr())) ? new Address() : dest.getAddr();
////        Address tmp4 = getOrCreate(dest.getAddr(), Address.class);
//        tmp3.setCity(tmp2);
//
//        dest.setAddr(tmp3);
    }

    @Test
    public void test2() {
        Class<?> clazz = Address.class;
//        Type type = clazz.getGenericSuperclass();
//        boolean x = clazz.isSynthetic();

        assertEquals(false, ClassTypeHelper.isStructType(int.class));
        assertEquals(false, ClassTypeHelper.isStructType(Integer.class));
        assertEquals(false, ClassTypeHelper.isStructType(String.class));
        assertEquals(true, ClassTypeHelper.isStructType(Address.class));
    }

    @Test
    public void test3() {
        Class<?> clazz = String.class;
        assertEquals(true, clazz.isAssignableFrom(String.class));
        assertEquals(false, clazz.isAssignableFrom(Integer.class));

        //Address tmp = zoneAddr;
        clazz = Address.class;
        assertEquals(true, clazz.isAssignableFrom(ZoneAddress.class));

        //ZoneAddress tmp = addr; //not allowed
        clazz = ZoneAddress.class;
        assertEquals(false, clazz.isAssignableFrom(Address.class));

    }

    @Test
    public void testOptToOpt() {
//        OptCategory src = null;
//        OptCategory dest = null;
//
//        dest.setProd(src.getProd());                        //src N -> dest N
//        dest.setOptProd(Optional.of(src.getProd()));        //src N -> dest O
//        dest.setProd(src.getOptProd().orElse(null));  //src O -> dest N
//        dest.setOptProd(src.getOptProd());                  //src O -> dest O

    }

}
