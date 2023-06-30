package org.dnal.fieldcopy.codegen;

import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.FieldSpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.util.ReflectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AutoFieldSpecCreator {

    public int createAutoFields(CopySpec spec, List<String> fieldsToExclude) {
        ReflectionUtil helper = new ReflectionUtil();
        List<String> allSrcFieldNames = helper.getAllFieldNames(spec.srcClass);
        List<String> allDestFieldNames = helper.getAllFieldNames(spec.destClass);

        //src and dest have exactly same name, then it's a candidate for auto-copy
        List<String> candidates = allSrcFieldNames.stream().filter(x -> allDestFieldNames.contains(x)).collect(Collectors.toList());
        //remove except list
        if (spec.autoExcludeFields.size() > 0) {
            candidates = candidates.stream().filter(x -> !spec.autoExcludeFields.contains(x)).collect(Collectors.toList());
        }

        List<String> finalList = buildFinalList(spec.fields, candidates);

        for(String fieldName: finalList) {
            NormalFieldSpec nspec = new NormalFieldSpec(spec.srcClass, spec.destClass, fieldName, fieldName);
            nspec.convLangSrc = String.format("%s -> %s", fieldName, fieldName);
            spec.fields.add(nspec);
        }
        return finalList.size();
    }

    private List<String> buildFinalList(List<FieldSpec> fields, List<String> candidates) {
        List<String> finalList = new ArrayList<>(candidates);

        //only auto-copy if there isn't an explicit copy command that mentions candidate as either src or dest
        for(FieldSpec fspec: fields) {
            if (fspec instanceof NormalFieldSpec) {
                NormalFieldSpec nspec = (NormalFieldSpec) fspec;
                String srcField = nspec.srcText; //TODO handle functions later such as birthDate.year
                if (finalList.contains(srcField)) {
                    finalList.remove(srcField);
                }
                String destField = nspec.destText; //TODO handle functions later such as birthDate.year
                if (finalList.contains(destField)) {
                    finalList.remove(destField);
                }
            }
        }

        //return sorted alphabetically
        finalList = finalList.stream().sorted().collect(Collectors.toList());
        return finalList;
    }
}
