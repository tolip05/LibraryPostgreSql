package com.cc.db.util;

import com.cc.db.statement.DBStatement;
import com.cc.logging.ILog;

public class DbStatementLogUtilizer {

    private static final String MSG_TRANSACTION_ROLLBACK_FORMAT_STRING = 	"Transaction rollbacked executing %s.\n" +
            "SQL:\t%s";

    private static final String MSG_DB_SATATEMENT_CLASS_NAME = 	"Class name  %s.";



    //logs classes in transaction to console
    public static void printDBStatementClassesInTransaction(ILog LOG, DBStatement[] statements){
        if(statements == null) return;

        LOG.debug("Transaction classes =====================================");
        for(int i = 0; i < statements.length ; i++){
            if(statements[i] == null) continue;
            LOG.debug(String.format(MSG_DB_SATATEMENT_CLASS_NAME,
                    statements[i].getClass().getName()));
        }
        LOG.debug("End of Transaction classes ===============================");
    }

    /**
     * Print exception stack & Exception Message to console
     * @param ex - Exception to be printed
     */
    public static void printExceptionToConsole(ILog LOG, Exception ex){
        if(ex != null){
            LOG.debug("Exception Messsage =============================================\n" + ex.getMessage());
            LOG.debug("Exception StackTrace ===========================================\n");
            ex.printStackTrace();
            LOG.error(ex);
        }
    }


    /**
     * Logs Slq statement when an error ocurrs
     * @param LOG
     * @param statements
     */
    public static void printErrSqlStatement(ILog LOG, DBStatement[] statements){
        for(int i = 0; i < statements.length ; i++){
            LOG.error(String.format(MSG_TRANSACTION_ROLLBACK_FORMAT_STRING,
                    statements[i].getClass().getName(),
                    statements[i].sqlForLog()));
        }
    }

    /**
     * Log the SQL statement
     * @param LOG
     * @param statements
     */
    public static void printSqlStatement(ILog LOG, DBStatement[] statements){
        for(int i = 0; i < statements.length ; i++){
            LOG.debug(statements[i].sqlForLog());
        }
    }



    public static void printErrSqlStatement(ILog LOG, DBStatement statement){
        DBStatement dbst  [] = new DBStatement[1];
        dbst[0] = statement;
        printErrSqlStatement(LOG, dbst);
    }

    public static void printSqlStatement(ILog LOG, DBStatement statement){
        DBStatement dbst  [] = new DBStatement[1];
        dbst[0] = statement;
        printSqlStatement(LOG, dbst);
    }

}