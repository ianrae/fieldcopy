package org.dnal.fieldcopy.codegen;

import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.util.StrListCreator;

public interface ExtractGen {
    VarExpr genGetSrcValue(StrListCreator sc, GenSrcResult srcResult, SingleFld srcFld, String varName, String varType, NormalFieldSpec nspec, JavaSrcSpec srcSpec);

    void genDestAssign(NormalFieldSpec nspec, StrListCreator sc, JavaSrcSpec srcSpec, GenResult result);

    GenSrcResult genSrc(NormalFieldSpec nspec, int index, StrListCreator sc, GenSrcResult srcResult, JavaSrcSpec srcSpec);

    GenResult genDest(NormalFieldSpec nspec, int index, StrListCreator sc, GenSrcResult srcResult, JavaSrcSpec srcSpec);
}
