package org.dnal.fieldcopy.builder;


import org.dnal.fieldcopy.dataclass.Address;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;

public class AddressSpecBuilder {

    public CopySpec buildSpec(int n) {
        Class<?> srcClass = Address.class;
        Class<?> destClass = Address.class;

        CopySpec spec = new CopySpec(srcClass, destClass);
        if (n >= 1) {
            NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "street1", "street1");
            spec.fields.add(fspec);
        }
        if (n >= 2) {
            NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "city", "city");
            spec.fields.add(fspec);
        }

        return spec;
    }

    //TODO add Region later
    public CopySpec addBackRef(CopySpec spec) {
        Class<?> srcClass = Address.class;
        Class<?> destClass = Address.class;

        NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "backRef", "backRef");
        spec.fields.add(fspec);

        return spec;
    }

}
