package com.cc.db.util;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionExecutor {

    public static Long execute(TransactionCallBack transactionCallBackMethod) throws SQLException {
        Connection connection = DBUtil.openConnection();
        Long newlyCreatedId;
        try {
            newlyCreatedId = transactionCallBackMethod.execute(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            DBUtil.rollBackAndCloseConnection(connection);
            throw e;
        }
        DBUtil.commitAndCloseConnection(connection);
        return newlyCreatedId;
    }
}