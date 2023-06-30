package org.dnal.fieldcopy.bdd.core;


import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.util.StringUtil;

public class DeliaSnippetRunner implements SnippetRunner {
    private final FieldCopyLog log;
    private final FieldCopyLog fieldCopyLog;

    public DeliaSnippetRunner(FieldCopyLog log, FieldCopyLog fieldCopyLog) {
        this.log = log;
        this.fieldCopyLog = fieldCopyLog;
    }

//    @Override
//    public void setConnectionProvider(ConnectionProvider connProvider) {
//        this.connProvider = connProvider;
//    }

    @Override
    public BDDSnippetResult execute(BDDSnippet snippet, BDDSnippetResult previousRes, SnippetContext ctx) {
        BDDSnippetResult res = new BDDSnippetResult();

//        if (previousRes != null && previousRes.sess != null) {
//            dao = new DeliaGenericDao(previousRes.sess.getDelia(), previousRes.sess);
//            sess = previousRes.sess;
//        } else {
//            ConnectionDefinition connDef = connProvider.getConnectionDef();
//            dao = new DeliaGenericDao(connDef, deliaLog);
//        }

        String src = StringUtil.flattenEx(snippet.lines, "\n");
        if (!src.isEmpty()) {
//            if (sess == null) {
//                if (!dao.initialize(src)) {
//                    res.ok = false;
//                    //add errs
//                    return res;
//                }
//                sess = dao.getMostRecentSession();
//                res.sess = sess;
            } else {
//                ResultValue resValue = dao.getDelia().continueExecution(src, sess);
//                res.resValue = resValue;
//                res.sess = sess;
//                if (!resValue.ok) {
//                    res.ok = false;
//                    //add errs
//                    return res;
//                }
//            }
        }

//        Delia delia = sess.getDelia();
//        if (DBType.MEM.equals(delia.getDBInterface().getDBType())) {
//            for (DTypeName typeName : sess.getExecutionContext().registry.getAll()) {
//                DBHelper.createTable(delia.getDBInterface(), typeName.getTypeName());
//            }
//        }

//        //build name hints. The parent side of a DValue relation doesn't exist in the db
//        //so we need to propogate the field name so the tests can produce identical parent relation field
//        for (DTypeName typeName : sess.getExecutionContext().registry.getAll()) {
//            DType type = sess.getExecutionContext().registry.getType(typeName);
//            if (type.isStructShape()) {
//                DStructType structType = (DStructType) type;
//                for(TypePair pair: structType.getAllFields()) {
//                    if (DRuleHelper.isParentRelation(structType, pair)) {
//                        RelationInfo relinfo = DRuleHelper.findMatchingRuleInfo(structType, pair);
//                        String key = String.format("%s.%s", relinfo.otherSide.nearType.getName(), relinfo.otherSide.fieldName);
//                        key = key.toLowerCase(Locale.ROOT); //postgres uses lower-case
//                        res.nameHintMap.put(key, relinfo.fieldName);
//                    }
//                }
//            }
//        }

        res.ok = true;
        return res;
    }
}
