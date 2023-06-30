package org.dnal.fieldcopy.fieldspec;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CopySpec {
    public Class<?> srcClass;
    public Class<?> destClass;
    public Optional<String> converterName = Optional.empty(); //calculated named
    public Optional<String> converterNameForUsing = Optional.empty();
    public boolean autoFlag;
    public List<String> autoExcludeFields = new ArrayList<>(); //only used when autoField=true
    public List<FieldSpec> fields = new ArrayList<>();
    public String actualClassName;

    public CopySpec(Class<?> srcClass, Class<?> destClass) {
        this.srcClass = srcClass;
        this.destClass = destClass;
    }

    public List<NormalFieldSpec> buildCustomList() {
        List<NormalFieldSpec> list = new ArrayList<>();
        for (FieldSpec fieldSpec : fields) {
            if (fieldSpec instanceof NormalFieldSpec) {
                NormalFieldSpec nspec = (NormalFieldSpec) fieldSpec;
                if (nspec.isCustom) {
                    list.add(nspec);
                }
            }
        }
        return list;
    }

    public boolean hasCustomFields() {
        return buildCustomList().size() > 0;
    }

    public String buildFilePathAndSetActualClassName(String dir, String className, String suffix) {
        actualClassName = buildActualClassName(className, suffix);
        return String.format("%s/%s.java", dir, actualClassName, suffix);
    }

    public String buildActualClassName(String className, String suffix) {
        if (converterName.isPresent()) {
            return String.format("%s%s", converterName.get(), suffix);
        } else {
            return String.format("%s%s", className, suffix);
        }
    }
}
