package org.dnal.fieldcopy.registry;

import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;
import org.dnal.fieldcopy.types.TypeTree;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CustomerTests extends ICRTestBase {

    //basic list w/o types
    public static class MyCustConverter1 implements ObjectConverter<List, List> {
        private FieldTypeInformation srcInfo;
        private FieldTypeInformation destInfo;

        public MyCustConverter1() {
            TypeTree typeTree = new TypeTree();
            typeTree.addPair(List.class, String.class);
            srcInfo = new FieldTypeInformationImpl(List.class, null, typeTree);

            typeTree = new TypeTree();
            typeTree.addPair(List.class, Integer.class);
            destInfo = new FieldTypeInformationImpl(List.class, null, typeTree);
        }

        @Override
        public FieldTypeInformation getSourceFieldTypeInfo() {
            return srcInfo;
        }

        @Override
        public FieldTypeInformation getDestinationFieldTypeInfo() {
            return destInfo;
        }

        @Override
        public List convert(List src, List dest, ConverterContext ctx) {
            List<String> tmp1 = src;
            List<Integer> list2 = new ArrayList<>();
            for (String el3 : tmp1) {
                Integer tmp4 = Integer.parseInt(el3);
                list2.add(tmp4);
            }
            return list2;
        }
    }

    @Test
    public void testScalarFieldWithGetter() {
        CopySpec spec = specBuilder.buildSpec(1);
        List<String> lines = doGen(spec, null);

        String[] ar = {
                "String tmp1 = src.getFirstName();",
                "dest.setFirstName(tmp1);"
        };
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testSubObj() {
        CopySpec spec = specBuilder.buildSpec(0);
        specBuilder.addObject(spec);
        CopySpec spec2 = addrSpecBuilder.buildSpec(1);
        List<String> lines = doGen(spec, spec2);

        String[] ar = {
                "Address tmp1 = src.getAddr();",
                "if (tmp1 != null) {",
                "ObjectConverter<Address,Address> conv2 = ctx.locate(Address.class, Address.class);",
                "Address tmp3 = conv2.convert(tmp1, new Address(), ctx);",
                "dest.setAddr(tmp3);",
                "}"
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Address");
    }

    @Test
    public void testListConverter1() {
        CopySpec spec = specBuilder.buildListSpec("roles", "points");
        customConverter1 = new MyCustConverter1();
        options.createNewListWhenCopying = false;
        List<String> lines = doGen(spec, null);

        String[] ar = {
                "List<String> tmp1 = src.getRoles();",
                "if (tmp1 != null) {",
                "ObjectConverter<List<String>,List<Integer>> conv2 = ctx.locate(FieldTypeInformationImpl.createForList(String.class), FieldTypeInformationImpl.createForList(Integer.class));",
                "List<Integer> tmp3 = conv2.convert(tmp1, new ArrayList(), ctx);",
                "dest.setPoints(tmp3);",
                "}"
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.util.List");
    }


//    @Test
//    public void testNoConverterError() {
//        CopySpec spec = specBuilder.buildSpecForUnsupportedConversion();
//
//        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
//            List<String> lines = doGen(spec);
//        });
//        log(thrown.getMessage());
//        chkExecption(thrown, "Cannot convert 'int n1' to 'Inner1 inner1'");
//    }

    //============
}
