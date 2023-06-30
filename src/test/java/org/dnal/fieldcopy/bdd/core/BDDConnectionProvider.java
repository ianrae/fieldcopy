//package org.dnal.fieldcopy.bdd.core;
//
//import org.delia.ConnectionDefinitionBuilder;
//import org.delia.DeliaSession;
//import org.delia.db.DBType;
//import org.delia.db.sql.ConnectionDefinition;
//import org.delia.log.DeliaLog;
//import org.delia.seede.db.DBSchemaBuilder;
//import org.delia.seede.db.h2.H2SchemaDetector;
//import org.delia.seede.db.postgres.PostgresSchemaDetector;
//import org.delia.seede.h2.H2ConnectionHelper;
//import org.delia.seede.postgres.PostgresConnectionHelper;
//
//public class BDDConnectionProvider implements ConnectionProvider {
//
//    private final DBType dbType;
//    private ConnectionDefinition currentConnDef;
//
//    public BDDConnectionProvider(DBType dbType) {
//        this.dbType = dbType;
//    }
//
//    @Override
//    public ConnectionDefinition getConnectionDef() {
//        if (currentConnDef == null) {
//            currentConnDef = doGtConnectionDef();
//        }
//        return currentConnDef;
//    }
//    private ConnectionDefinition doGtConnectionDef() {
//        switch(dbType) {
//            case MEM:
//                return ConnectionDefinitionBuilder.createMEM();
//            case H2:
//                return H2ConnectionHelper.getTestDB();
//            case POSTGRES:
//                return PostgresConnectionHelper.getTestDB();
//        }
//        return null;
//    }
//
//    @Override
//    public DBSchemaBuilder getSchemaBuilder(DeliaSession sess, DeliaLog log) {
//        switch(dbType) {
//            case MEM:
//                return new MEMSchemaBuilder(sess);
//            case H2:
//                return new H2SchemaDetector(getConnectionDef(), log);
//            case POSTGRES:
//                return new PostgresSchemaDetector(getConnectionDef(), log);
//        }
//        return null;
//    }
//
//    @Override
//    public DBType getDBType() {
//        return dbType;
//    }
//
//}
