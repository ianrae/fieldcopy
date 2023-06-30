package org.dnal.fieldcopy.newcodegen;

import org.dnal.fieldcopy.codegen.FldXBuilder;
import org.dnal.fieldcopy.codegen.FldXValidator;
import org.dnal.fieldcopy.codegen.JavaSrcSpec;
import org.dnal.fieldcopy.fieldspec.FieldSpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;

/**
 * Build the fldXs for a NormalFieldSpec
 */
public class FieldSpecBuilder {
    private final FieldCopyOptions options;
    private FldXBuilder fldXBuilder;

    public FieldSpecBuilder(FieldCopyOptions options) {
        this.options = options;
        this.fldXBuilder = new FldXBuilder(options);
    }

    public void buildFieldSpec(FieldSpec fspec, JavaSrcSpec srcSpec) {
        if (fspec instanceof NormalFieldSpec) {
            NormalFieldSpec nspec = (NormalFieldSpec) fspec;

            //build the fldX lists (one per element, eg. addr.city)
            nspec.destFldX = fldXBuilder.newBuildFldX(nspec.destClass, nspec.destText, nspec.dfBuilder, false);
            if (nspec.srcTextIsValue) {
                nspec.srcFldX = fldXBuilder.buildValueFld(nspec.srcClass, nspec.srcText, nspec.dfBuilder, nspec.destFldX);
            } else {
                nspec.srcFldX = fldXBuilder.newBuildFldX(nspec.srcClass, nspec.srcText, nspec.dfBuilder, true);
            }

            //validation. During codegen we can validate values somewhat to give early error detection.
            //eg. '2022-08-22' -> someDate may be invalid for given date format
            FldXValidator validator = new FldXValidator(options);
            //only src side can contain value objects
            boolean ok = validator.validate(nspec.srcFldX); //will throw if validation error detected
        }
    }

}
