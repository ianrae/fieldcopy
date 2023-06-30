package org.dnal.fieldcopy.newcodegen.vardef;

import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.types.ClassTypeHelper;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.util.ClassNameUtil;
import org.dnal.fieldcopy.util.StringUtil;

public class JavaGetSetGenerator {
    private final FieldCopyOptions options;

    public JavaGetSetGenerator(FieldCopyOptions options) {
        this.options = options;
    }

    //tmp5.get().setCode(tmp3);
    public QVarDefine genSet(QVar srcVar, String destVarName, QField destfield, boolean destVarIsOptional) {
        String destText = destVarName;
        if (destVarIsOptional) {
            destText = String.format("%s.get()", destVarName);
        }

        //        dest.setProd(src.getProd());                        //src N -> dest N
//        dest.setOptProd(Optional.of(src.getProd()));        //src N -> dest O
//        dest.setProd(src.getOptProd().orElse(null));  //src O -> dest N
//        dest.setOptProd(src.getOptProd());                  //src O -> dest O


        boolean srcIsOptional = srcVar.fti.isOptional();
        boolean destIsOptional = destfield.fti.isOptional();
        String srcVarName = srcVar.varName;
        String setStr = doBuildSetter(destfield);
        String sourceText;
        if (!srcIsOptional && !destIsOptional) {
            sourceText = srcVarName;
        } else if (!srcIsOptional && destIsOptional) {
            sourceText = String.format("Optional.ofNullable(%s)", srcVarName);
        } else if (srcIsOptional && !destIsOptional) {
            sourceText = String.format("(ctx.isNullOrEmpty(%s)) ? null : %s.orElse(null)", srcVarName, srcVarName);
//            sourceText = String.format("%s.orElse(null)", srcVarName);
        } else {
            sourceText = srcVarName;
        }

        String ss;
        if (destfield.isPublicField) {
            ss = String.format("%s.%s = %s;", destText, setStr, sourceText);
        } else {
            ss = String.format("%s.%s(%s);", destText, setStr, sourceText);
        }

        log(String.format("S: v:%s src:%b dest:%b", destVarIsOptional, srcIsOptional, destIsOptional));
        return createQVarDefine(ss, destVarName, destfield);
    }

    //gen   tmp3.getCode() ready for assignment into destVarName
    public QVarDefine genVar(QVar srcVar, QField qfield, boolean srcVarIsOptional, boolean destIsOptional, String destVarName) {
        String srcVarName = srcVar.varName;
        //note srcVar.fti is the parent class
        String s = doBuildGetter(qfield);
        boolean srcIsOptional = qfield.fti.isOptional();

        String sourceText = srcVarName;
        if (srcVarIsOptional) {
            sourceText = String.format("%s.get()", srcVar.varName);
        }

        String ss;
        if (!srcIsOptional && !destIsOptional) {
            ss = String.format("%s.%s", sourceText, s);
        } else if (!srcIsOptional && destIsOptional) {
            ss = String.format("Optional.ofNullable(%s.%s)", sourceText, s);
        } else if (srcIsOptional && !destIsOptional) {
//            //this is safer than orElse(null) because it can handle src var being null as well
//            String condExpr;
//            if (srcVar.fti.isOptional()) {
//                condExpr = String.format("ctx.isNullOrEmpty(%s)", srcVarName);
//            } else {
//                condExpr = String.format("%s == null", srcVarName);
//            }
//
//            String defaultVal = destIsOptional ? "Optional.empty()" : "null";
//            String basicGetStr = String.format("%s.%s", sourceText, s);
//            String s2 = String.format("(%s) ? %s : %s", condExpr, defaultVal, basicGetStr);
//            ss = s2;
            ss = String.format("%s.%s.orElse(null)", sourceText, s);
        } else {
            ss = String.format("%s.%s", sourceText, s);
        }
        ss = handleList(qfield.fti, ss);

        log(String.format("X: v:%s src:%b dest:%b", srcVar.fti.isOptional(), srcIsOptional, destIsOptional));
        return createQVarDefine(ss, destVarName, qfield);
    }

    private String handleList(FieldTypeInformation fti, String ss) {
        if (fti.isList() && options.createNewListWhenCopying) {
            //src._list == null ? null : new ArrayList<>(src._list)
            String className = ClassNameUtil.renderClassName(fti.getFirstActual());
            String tmp = String.format("ctx.createEmptyList(%s, %s.class)", ss, className);
            ss = String.format("%s == null ? null : %s", ss, tmp);
//            getFrag.varName = ""; //force

            //we already do this by default srcSpec.addImportIfNotAlreadyPresent("java.util.ArrayList");
        }

        return ss;
    }

    //String tmp3 = (ctx.isNullOrEmpty(tmp2)) ? null : tmp2.get().getCode();",
    public QVarDefine safeGenVar(QVar srcVar, QField qfield, boolean srcVarIsOptional, boolean destIsOptional, String destVarName, boolean isFinalSubObj,
                                 boolean forceFlag) {
        if (forceFlag) {
            return doSafeGenVarEx(forceFlag, srcVar, qfield, srcVarIsOptional, destIsOptional, destVarName, isFinalSubObj, false);
        }
        return doSafeGenVar(srcVar, qfield, srcVarIsOptional, destIsOptional, destVarName, isFinalSubObj, false);
    }


    public boolean needsAutoCreate(FieldTypeInformation fti, boolean isFinalSubObj) {
        if (isFinalSubObj || !ClassTypeHelper.isStructType(fti)) {
            return false;
        }
        return true;
    }

    //OptProduct tmp4 = (dest.getProd() == null) ? new OptProduct() : dest.getProd();
    public QVarDefine getVarAutoCreate(QVar srcVar, QField qfield, boolean srcVarIsOptional, boolean destIsOptional, String destVarName, boolean isFinalSubObj) {
        return doSafeGenVar(srcVar, qfield, srcVarIsOptional, destIsOptional, destVarName, isFinalSubObj, true);
    }

    private QVarDefine doSafeGenVar(QVar srcVar, QField qfield, boolean srcVarIsOptional, boolean destIsOptional, String destVarName,
                                    boolean isFinalSubObj, boolean autoCreate) {
        QVarDefine varDef = genVar(srcVar, qfield, srcVarIsOptional, destIsOptional, destVarName);
        String basicGetStr;
        if (srcVarIsOptional) {
            basicGetStr = String.format("%s.get().%s", srcVar.varName, doBuildGetter(qfield));
        } else {
            basicGetStr = String.format("%s.%s", srcVar.varName, doBuildGetter(qfield));
        }

        if (isFinalSubObj || !ClassTypeHelper.isStructType(qfield.fti)) {
            varDef.srcText = basicGetStr;
            return varDef;
        }

        String condExpr;
        if (srcVar.fti.isOptional()) {
            condExpr = String.format("ctx.isNullOrEmpty(%s)", basicGetStr);
        } else {
            condExpr = String.format("%s == null", basicGetStr);
        }

        String defaultVal = destIsOptional ? "Optional.empty()" : "null";
        if (autoCreate) {
            Class<?> clazz = qfield.fti.getEffectiveType();
            String newString = String.format("new %s()", clazz.getSimpleName());
            defaultVal = destIsOptional ? String.format("Optional.of(%s)", newString) : newString;
        }

        String s = String.format("(%s) ? %s : %s", condExpr, defaultVal, varDef.srcText);

        boolean srcIsOptional = qfield.fti.isOptional();
        log(String.format("A: v:%s src:%b dest:%b", srcVar.fti.isOptional(), srcIsOptional, destIsOptional));

        varDef.srcText = s;
        return varDef;
    }

    private QVarDefine doSafeGenVarEx(boolean forceFlag, QVar srcVar, QField qfield, boolean srcVarIsOptional, boolean destIsOptional, String destVarName,
                                      boolean isFinalSubObj, boolean autoCreate) {
        QVarDefine varDef = genVar(srcVar, qfield, srcVarIsOptional, destIsOptional, destVarName);
        String basicGetStr = String.format("%s.%s", srcVar.varName, doBuildGetter(qfield));
        if (!forceFlag && (isFinalSubObj || !ClassTypeHelper.isStructType(qfield.fti))) {
            varDef.srcText = basicGetStr;
            return varDef;
        }

        String condExpr;
        if (srcVar.fti.isOptional()) {
            condExpr = String.format("ctx.isNullOrEmpty(%s)", srcVar.varName);
        } else {
            condExpr = String.format("%s == null", srcVar.varName);
        }

        String defaultVal = destIsOptional ? "Optional.empty()" : "null";
        if (autoCreate) {
            Class<?> clazz = qfield.fti.getEffectiveType();
            String newString = String.format("new %s()", clazz.getSimpleName());
            defaultVal = destIsOptional ? String.format("Optional.ofNullable(%s)", newString) : newString;
        }

        String s = String.format("(%s) ? %s : %s", condExpr, defaultVal, varDef.srcText);

        boolean srcIsOptional = qfield.fti.isOptional();
        log(String.format("A: v:%s src:%b dest:%b", srcVar.fti.isOptional(), srcIsOptional, destIsOptional));

        varDef.srcText = s;
        return varDef;
    }

    private void log(String s) {
        System.out.println(s);
    }

    private QVarDefine createQVarDefine(String ss, String destVarName, QField qfield) {
        QVar qvar = new QVar(destVarName, qfield.fti);
        QVarDefine varDef = new QVarDefine(qvar, ss);
        return varDef;
    }

    private String doBuildGetter(QField qfield) {
        if (qfield.isPublicField) {
            return qfield.fieldName;
        } else if (qfield.useIsGetter) {
            return String.format("is%s()", StringUtil.uppify(qfield.fieldName));
        } else {
            return String.format("get%s()", StringUtil.uppify(qfield.fieldName));
        }
    }

    private String doBuildSetter(QField qfield) {
        if (qfield.isPublicField) {
            return qfield.fieldName;
        } else {
            return String.format("set%s", StringUtil.uppify(qfield.fieldName));
        }
    }

}
