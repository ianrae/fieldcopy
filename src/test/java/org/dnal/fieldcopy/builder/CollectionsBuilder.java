package org.dnal.fieldcopy.builder;

import org.dnal.fieldcopy.dataclass.Collections1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.types.JavaCollection;
import org.dnal.fieldcopy.types.JavaPrimitive;

import java.util.Locale;

public class CollectionsBuilder {

    public CopySpec buildSpec(JavaCollection prim) {
        Class<?> srcClass = Collections1.class;
        Class<?> destClass = Collections1.class;

        String fieldName = String.format("_%s", JavaCollection.lowify(prim));
        CopySpec spec = new CopySpec(srcClass, destClass);
        NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, fieldName, fieldName);
        spec.fields.add(fspec);

        return spec;
    }

}
