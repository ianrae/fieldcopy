package org.dnal.fieldcopy.builder;


import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;

public class CustomerSpecBuilder {

    public CopySpec buildSpec(int n) {
        Class<?> srcClass = Customer.class;
        Class<?> destClass = Customer.class;

        CopySpec spec = new CopySpec(srcClass, destClass);
        if (n >= 1) {
            NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "firstName", "firstName");
            spec.fields.add(fspec);
        }
        if (n >= 2) {
            NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "lastName", "lastName");
            spec.fields.add(fspec);
        }
        if (n >= 3) {
            NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "numPoints", "numPoints");
            spec.fields.add(fspec);
        }

        return spec;
    }

    public CopySpec addCustom(CopySpec spec) {
        Class<?> srcClass = Customer.class;
        Class<?> destClass = Customer.class;

        NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "firstName", "firstName");
        fspec.isCustom = true;
        spec.fields.add(fspec);

        return spec;
    }
    public CopySpec addObject(CopySpec spec) {
        Class<?> srcClass = Customer.class;
        Class<?> destClass = Customer.class;

        NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "addr", "addr");
        spec.fields.add(fspec);

        return spec;
    }

    public CopySpec buildListSpec(String srcField, String destField) {
        Class<?> srcClass = Customer.class;
        Class<?> destClass = Customer.class;

        CopySpec spec = new CopySpec(srcClass, destClass);
        NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, srcField, destField);
        spec.fields.add(fspec);

        return spec;
    }

    public CopySpec buildDateSpec(int n) {
        Class<?> srcClass = Customer.class;
        Class<?> destClass = Customer.class;

        CopySpec spec = new CopySpec(srcClass, destClass);
        if (n >= 1) {
            NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "date", "dateStr");
            spec.fields.add(fspec);
        }
        if (n >= 2) {
            NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "time", "timeStr");
            spec.fields.add(fspec);
        }
        if (n >= 3) {
            NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "ldt", "dateTimeStr");
            spec.fields.add(fspec);
        }
        if (n >= 4) {
            NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "zdt", "zonedDateTimeStr");
            spec.fields.add(fspec);
        }

        return spec;
    }
    public CopySpec buildDateSpecReverse(int n) {
        Class<?> srcClass = Customer.class;
        Class<?> destClass = Customer.class;

        CopySpec spec = new CopySpec(srcClass, destClass);
        if (n >= 1) {
            NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "dateStr", "date");
            spec.fields.add(fspec);
        }
        if (n >= 2) {
            NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "timeStr", "time");
            spec.fields.add(fspec);
        }
        if (n >= 3) {
            NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "dateTimeStr", "ldt");
            spec.fields.add(fspec);
        }
        if (n >= 4) {
            NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "zonedDateTimeStr", "zdt");
            spec.fields.add(fspec);
        }

        return spec;
    }

}
