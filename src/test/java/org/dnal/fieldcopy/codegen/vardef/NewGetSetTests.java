package org.dnal.fieldcopy.codegen.vardef;


import org.dnal.fieldcopy.dataclass.OptCategory;
import org.dnal.fieldcopy.dataclass.OptProduct;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.newcodegen.vardef.JavaGetSetGenerator;
import org.dnal.fieldcopy.newcodegen.vardef.QField;
import org.dnal.fieldcopy.newcodegen.vardef.QVar;
import org.dnal.fieldcopy.newcodegen.vardef.QVarDefine;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.rtests.RTestBase;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NewGetSetTests extends RTestBase {

//        dest.setProd(src.getProd());                        //src N -> dest N
//        dest.setOptProd(Optional.of(src.getProd()));        //src N -> dest O
//        dest.setProd(src.getOptProd().orElse(null));  //src O -> dest N
//        dest.setOptProd(src.getOptProd());                  //src O -> dest O
    @Test
    public void test1A() {
        //v:false src:true dest:false
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.region.code", "optProd.region.code");
        SingleFld fld = getFirstOne(spec);
        chkIt(fld, "src.getOptProd().orElse(null)", OptProduct.class, false, false);
        chkIt(fld, "src.optProd.orElse(null)", OptProduct.class, false, true);
        chkIt(fld, "src.isOptProd().orElse(null)", OptProduct.class, true, false);
        chkIt(fld, "src.optProd.orElse(null)", OptProduct.class, true, true);
    }

    @Test
    public void test1B() {
        //v:false src:false dest:false
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "prod.region.code");
        SingleFld fld = getFirstOne(spec);
        chkIt(fld, "src.getProd()", OptProduct.class, false, false);
        chkIt(fld, "src.prod", OptProduct.class, false, true);
        chkIt(fld, "src.isProd()", OptProduct.class, true, false);
        chkIt(fld, "src.prod", OptProduct.class, true, true);
    }

    @Test
    public void test2A() {
        //v:true src:true dest:false
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.region.code", "optProd.region.code");
        SingleFld fld = getFirstOne(spec);
        QVar qvar = buildOptionalSrcVar();
        srcIsOptional = true;

        chkIt2(qvar, fld, "tmp2.get().getOptProd().orElse(null)", OptProduct.class, false, false);
        chkIt2(qvar, fld, "tmp2.get().optProd.orElse(null)", OptProduct.class, false, true);
        chkIt2(qvar, fld, "tmp2.get().isOptProd().orElse(null)", OptProduct.class, true, false);
        chkIt2(qvar, fld, "tmp2.get().optProd.orElse(null)", OptProduct.class, true, true);
    }

    @Test
    public void test2B() {
        //v:true src:true dest:false
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "prod.region.code");
        SingleFld fld = getFirstOne(spec);
        QVar qvar = buildOptionalSrcVar();
        srcIsOptional = true;

        chkIt2(qvar, fld, "tmp2.get().getProd()", OptProduct.class, false, false);
        chkIt2(qvar, fld, "tmp2.get().prod", OptProduct.class, false, true);
        chkIt2(qvar, fld, "tmp2.get().isProd()", OptProduct.class, true, false);
        chkIt2(qvar, fld, "tmp2.get().prod", OptProduct.class, true, true);
    }

    @Test
    public void test3A() {
        //v:false src:true dest:true
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.region.code", "optProd.region.code");
        SingleFld fld = getFirstOne(spec);
        destIsOptional = true;
        chkIt(fld, "src.getOptProd()", OptProduct.class, false, false);
        chkIt(fld, "src.optProd", OptProduct.class, false, true);
        chkIt(fld, "src.isOptProd()", OptProduct.class, true, false);
        chkIt(fld, "src.optProd", OptProduct.class, true, true);
    }

    @Test
    public void test3B() {
        //v:false src:false dest:true
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "prod.region.code");
        SingleFld fld = getFirstOne(spec);
        destIsOptional = true;
        chkIt(fld, "Optional.ofNullable(src.getProd())", OptProduct.class, false, false);
        chkIt(fld, "Optional.ofNullable(src.prod)", OptProduct.class, false, true);
        chkIt(fld, "Optional.ofNullable(src.isProd())", OptProduct.class, true, false);
        chkIt(fld, "Optional.ofNullable(src.prod)", OptProduct.class, true, true);
    }

    @Test
    public void test4A() {
        //v:true src:true dest:true
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.region.code", "optProd.region.code");
        SingleFld fld = getFirstOne(spec);
        QVar qvar = buildOptionalSrcVar();
        destIsOptional = true;
        srcIsOptional = true;

        chkIt2(qvar, fld, "tmp2.get().getOptProd()", OptProduct.class, false, false);
        chkIt2(qvar, fld, "tmp2.get().optProd", OptProduct.class, false, true);
        chkIt2(qvar, fld, "tmp2.get().isOptProd()", OptProduct.class, true, false);
        chkIt2(qvar, fld, "tmp2.get().optProd", OptProduct.class, true, true);
    }

    @Test
    public void test4B() {
        //v:true src:false dest:true
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "prod.region.code");
        SingleFld fld = getFirstOne(spec);
        QVar qvar = buildOptionalSrcVar();
        destIsOptional = true;
        srcIsOptional = true;

        chkIt2(qvar, fld, "Optional.ofNullable(tmp2.get().getProd())", OptProduct.class, false, false);
        chkIt2(qvar, fld, "Optional.ofNullable(tmp2.get().prod)", OptProduct.class, false, true);
        chkIt2(qvar, fld, "Optional.ofNullable(tmp2.get().isProd())", OptProduct.class, true, false);
        chkIt2(qvar, fld, "Optional.ofNullable(tmp2.get().prod)", OptProduct.class, true, true);
    }

    //-- safe-get ---
    @Test
    public void testSafeGet1() {
        //v:false src:true dest:false
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.region.code", "optProd.region.code");
        SingleFld fld = getFirstOne(spec);
        QVar qvar = buildNonOptionalSrcVar();
        chkSafeGet(qvar, fld, "(src.getOptProd() == null) ? null : src.getOptProd().orElse(null)", OptProduct.class, false, false);
        chkSafeGet(qvar, fld, "(src.optProd == null) ? null : src.optProd.orElse(null)", OptProduct.class, false, true);
    }

    @Test
    public void testSafeGet2() {
        //v:true src:true dest:false
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.region.code", "optProd.region.code");
        SingleFld fld = getFirstOne(spec);
        QVar qvar = buildOptionalSrcVar();
        srcIsOptional = true;
        chkSafeGet(qvar, fld, "(ctx.isNullOrEmpty(tmp2.get().getOptProd())) ? null : tmp2.get().getOptProd().orElse(null)", OptProduct.class, false, false);
        chkSafeGet(qvar, fld, "(ctx.isNullOrEmpty(tmp2.get().optProd)) ? null : tmp2.get().optProd.orElse(null)", OptProduct.class, false, true);
    }

    //-- auto-create ---
    @Test
    public void testAutoCreate1A() {
        //v:false src:false dest:false
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.region.code", "optProd.region.code");
        SingleFld fld = getFirstOne(spec);
        QVar qvar = buildNonOptionalSrcVar();
        chkAutoCreate(qvar, fld, "(src.getOptProd() == null) ? new OptProduct() : src.getOptProd().orElse(null)", OptProduct.class, false, false);
        chkAutoCreate(qvar, fld, "(src.optProd == null) ? new OptProduct() : src.optProd.orElse(null)", OptProduct.class, false, true);
    }

    @Test
    public void testAutoCreate1B() {
        //v:false src:false dest:false
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "prod.region.code");
        SingleFld fld = getFirstOne(spec);
        QVar qvar = buildNonOptionalSrcVar();
        chkAutoCreate(qvar, fld, "(src.getProd() == null) ? new OptProduct() : src.getProd()", OptProduct.class, false, false);
        chkAutoCreate(qvar, fld, "(src.prod == null) ? new OptProduct() : src.prod", OptProduct.class, false, true);
    }

    @Test
    public void testAutoCreate2A() {
        //v:false src:true dest:true
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.region.code", "optProd.region.code");
        SingleFld fld = getFirstOne(spec);
        QVar qvar = buildNonOptionalSrcVar();
        destIsOptional = true;
        chkAutoCreate(qvar, fld, "(src.getOptProd() == null) ? Optional.of(new OptProduct()) : src.getOptProd()", OptProduct.class, false, false);
//        chkAutoCreate(qvar, fld, "(src.optProd == null) ? Optional.ofNullable(new OptProduct()) : src.optProd", OptProduct.class, false, true);
    }

    @Test
    public void testAutoCreate2B() {
        //v:false src:false dest:true
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "prod.region.code");
        SingleFld fld = getFirstOne(spec);
        QVar qvar = buildNonOptionalSrcVar();
        destIsOptional = true;
        chkAutoCreate(qvar, fld, "(src.getProd() == null) ? Optional.of(new OptProduct()) : Optional.ofNullable(src.getProd())", OptProduct.class, false, false);
//        chkAutoCreate(qvar, fld, "(src.optProd == null) ? Optional.ofNullable(new OptProduct()) : src.optProd", OptProduct.class, false, true);
    }

    //--- set ---
    @Test
    public void testSet1A() {
        //v:false src:false dest:true
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.region.code", "optProd.region.code");
        SingleFld fld = getFirstOne(spec);
        QVar qvar = buildNonOptionalSrcVar("tmp3");

        chkSet(qvar, fld, "dest.setOptProd(Optional.ofNullable(tmp3));", OptProduct.class, false);
        chkSet(qvar, fld, "dest.optProd = Optional.ofNullable(tmp3);", OptProduct.class, true);
    }
    @Test
    public void testSet1B() {
        //v:false src:false dest:false
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "prod.region.code");
        SingleFld fld = getFirstOne(spec);
        QVar qvar = buildNonOptionalSrcVar("tmp3");

        chkSet(qvar, fld, "dest.setProd(tmp3);", OptProduct.class, false);
        chkSet(qvar, fld, "dest.prod = tmp3;", OptProduct.class, true);
    }

    @Test
    public void testSet2A() {
        //v:true src:false dest:true
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.region.code", "optProd.region.code");
        SingleFld fld = getFirstOne(spec);
        QVar qvar = buildNonOptionalSrcVar("tmp3");
        destIsOptional = true;

        chkSet(qvar, fld, "dest.get().setOptProd(Optional.ofNullable(tmp3));", OptProduct.class, false);
        chkSet(qvar, fld, "dest.get().optProd = Optional.ofNullable(tmp3);", OptProduct.class, true);
    }
    @Test
    public void testSet2B() {
        //v:true src:false dest:false
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "prod.region.code");
        SingleFld fld = getFirstOne(spec);
        QVar qvar = buildNonOptionalSrcVar("tmp3");
        destIsOptional = true;

        chkSet(qvar, fld, "dest.get().setProd(tmp3);", OptProduct.class, false);
        chkSet(qvar, fld, "dest.get().prod = tmp3;", OptProduct.class, true);
    }


    @Test
    public void testSet3B() {
        //v:false src:true dest:false
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "prod.region.code");
        SingleFld fld = getFirstOne(spec);
        QVar qvar = buildOptionalSrcVar("tmp3");

        chkSet(qvar, fld, "dest.setProd((ctx.isNullOrEmpty(tmp3)) ? null : tmp3.orElse(null));", OptProduct.class, false);
        chkSet(qvar, fld, "dest.prod = (ctx.isNullOrEmpty(tmp3)) ? null : tmp3.orElse(null);", OptProduct.class, true);
    }

    //-------------------
    protected boolean srcIsOptional = false;
    protected boolean destIsOptional = false;
    protected boolean isFinalSubObj = false;

    private void chkIt(SingleFld fld, String expected, Class<?> fieldClass, boolean useIsGetter, boolean isPublicField) {
        QVar qvar = buildNonOptionalSrcVar();
        chkIt2(qvar, fld, expected, fieldClass, useIsGetter, isPublicField);
    }

    private void chkIt2(QVar qvar, SingleFld fld, String expected, Class<?> fieldClass, boolean useIsGetter, boolean isPublicField) {
        QField qfield = new QField(fld.fieldName, fld.fieldTypeInfo, useIsGetter, isPublicField);

        JavaGetSetGenerator getsetGen = createGen();
        String destVarName = "tmp1";
        QVarDefine varDef = getsetGen.genVar(qvar, qfield, srcIsOptional, destIsOptional, destVarName);

        assertEquals("tmp1", varDef.qvar.varName);
        Class<?> clazz = qfield.fti.getEffectiveType();
        assertEquals(fieldClass, clazz);
        assertEquals(expected, varDef.srcText);
    }

    private JavaGetSetGenerator createGen() {
        JavaGetSetGenerator getsetGen = new JavaGetSetGenerator(new FieldCopyOptions());
        return getsetGen;
    }

    private SingleFld getFirstOne(CopySpec spec) {
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        SingleFld fld = nspec.srcFldX.getFirst();
        return fld;
    }

    private void chkSafeGet(QVar qvar, SingleFld fld, String expected, Class<?> fieldClass, boolean useIsGetter, boolean isPublicField) {
        QField qfield = new QField(fld.fieldName, fld.fieldTypeInfo, useIsGetter, isPublicField);

        JavaGetSetGenerator getsetGen = createGen();
        String destVarName = "tmp1";
        QVarDefine varDef = getsetGen.safeGenVar(qvar, qfield, srcIsOptional, destIsOptional, destVarName, isFinalSubObj, false);

        assertEquals("tmp1", varDef.qvar.varName);
        assertEquals(fieldClass, varDef.qvar.fti.getFirstActual());
        assertEquals(expected, varDef.srcText);
    }

    private void chkAutoCreate(QVar qvar, SingleFld fld, String expected, Class<?> fieldClass, boolean useIsGetter, boolean isPublicField) {
        QField qfield = new QField(fld.fieldName, fld.fieldTypeInfo, useIsGetter, isPublicField);

        JavaGetSetGenerator getsetGen = createGen();
        String destVarName = "tmp1";
        QVarDefine varDef = getsetGen.getVarAutoCreate(qvar, qfield, srcIsOptional, destIsOptional, destVarName, isFinalSubObj);

        assertEquals("tmp1", varDef.qvar.varName);
        assertEquals(fieldClass, varDef.qvar.fti.getEffectiveType());
        assertEquals(expected, varDef.srcText);
    }

    private void chkSet(QVar qvar, SingleFld fld, String expected, Class<?> fieldClass, boolean isPublicField) {
        QField qfield = new QField(fld.fieldName, fld.fieldTypeInfo, false, isPublicField);

        JavaGetSetGenerator getsetGen = createGen();
        String destVarName = "dest";
        QVarDefine varDef = getsetGen.genSet(qvar, destVarName, qfield, destIsOptional);

        assertEquals("dest", varDef.qvar.varName);
        assertEquals(fieldClass, varDef.qvar.fti.getEffectiveType());
        assertEquals(expected, varDef.srcText);
    }

    private QVar buildNonOptionalSrcVar() {
        return buildNonOptionalSrcVar("src");
    }

    private QVar buildNonOptionalSrcVar(String varName) {
        QVar qvar = new QVar(varName, new FieldTypeInformationImpl(OptCategory.class));
        return qvar;
    }

    private QVar buildOptionalSrcVar() {
        return buildOptionalSrcVar("tmp2");
    }
    private QVar buildOptionalSrcVar(String varName) {
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.region.code", "optProd.region.code");
        SingleFld fld = getFirstOne(spec);
        QVar qvar = new QVar(varName, fld.fieldTypeInfo); //so its optional
        return qvar;
    }


}
