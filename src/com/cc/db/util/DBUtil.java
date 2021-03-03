package com.cc.db.util;

import com.cc.db.DBConfig;
import com.cc.db.common.DataBaseDateTimeSelect;
import com.cc.db.common.DataBaseVersionSelect;
import com.cc.db.common.SequenceSelect;
import com.cc.db.statement.DBStatement;
import com.cc.db.statement.FalseStatement;
import com.cc.logging.LOG;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DBUtil {
    public static Connection openConnection() throws SQLException {
        Connection connection = null;
        connection = DBConfig.connectionFactory.getConnection();
        try {
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            rollBackConnection(connection);
            closeConnection(connection);
            LOG.getLog(DBUtil.class)
                    .info("Cannot set auto commit to current connection!");
            LOG.getLog(DBUtil.class)
                    .error("Cannot set auto commit to current connection!");
            throw e;
        }
    }

    /**
     * Executes single statement
     *
     * @param statement
     * @throws SQLException
     */
    public static void execute(DBStatement statement) throws SQLException {
        execute(Arrays.asList(statement));
    }

    /**
     * Executes statement with predefined connection
     *
     * @param statement
     * @param connection
     * @throws SQLException
     */
    public static void execute(DBStatement statement,
                               Connection connection)
            throws SQLException {
        execute(Arrays.asList(statement),
                connection);
    }

    /**
     * Executes array of dbstatements
     *
     * @param statements
     * @throws SQLException
     */
    public static void execute(DBStatement[] statements) throws SQLException {
        execute(Arrays.asList(statements));
    }

    /**
     * Executes list ot dbstatements
     *
     * @param statements
     * @throws SQLException
     */
    public static void execute(List<DBStatement> statements) throws SQLException {
        // Open connection
        Connection connection = openConnection();
        execute(statements,
                connection);
    }

    /**
     * Main and only method for execution of statements
     *
     * @param statements
     * @param connection
     * @throws SQLException
     */
    private static void execute(List<DBStatement> statements,
                                Connection connection)
            throws SQLException {
        // Execute all statements with opened connection
        for (DBStatement statement : statements) {
            try {
                statement.execute(connection);
            } catch (SQLException e) {
                // print error
                LOG.getLog(DBUtil.class)
                        .error(e);
                // Rollback connection if error occurs
                rollBackConnection(connection);
                // Close rollbacked connection
                closeConnection(connection);
                // Throw exception to be passed to client if used with web
                // service
                throw e;
            }
        }
        // If no errors commit and close connection
        commitAndCloseConnection(connection);
    }

    public static void executeFalseStatement(Connection connection) throws SQLException {
        execute(new FalseStatement(),
                connection);
    }

    public static void commitAndCloseConnection(Connection connection) throws SQLException {
        try {
            connection.commit();
        } catch (SQLException e) {
            LOG.getLog(DBUtil.class)
                    .info("Cannot commit connection!");
            LOG.getLog(DBUtil.class)
                    .error("Cannot commit connection!");
            rollBackConnection(connection);
            throw e;
        } finally {
            closeConnection(connection);
        }
    }

    public static void rollBackAndCloseConnection(Connection connection) throws SQLException {
        try {
            connection.rollback();
            LOG.getLog(DBUtil.class)
                    .info("RollBack!!!");
        } catch (SQLException e) {
            LOG.getLog(DBUtil.class)
                    .info("Cannot rollback connection!");
            LOG.getLog(DBUtil.class)
                    .error("Cannot rollback connection!");
            throw e;
        } finally {
            closeConnection(connection);
        }
    }

    private static void rollBackConnection(Connection connection) throws SQLException {
        if (connection != null)
            connection.rollback();
        LOG.getLog(DBUtil.class)
                .info("RollBack!!!");
    }

    private static void closeConnection(Connection connection) throws SQLException {
        if (connection != null
                && !connection.isClosed()) {
            connection.close();
        }
    }

    public static long nextValue(String tableName) throws SQLException {
        SequenceSelect sequenceSelect = new SequenceSelect(tableName);
        execute(sequenceSelect);

        long nextId = sequenceSelect.getNextVal();
        return nextId;
    }

    public static long nextValue(String pkg,
                                 String tableName)
            throws SQLException {
        SequenceSelect sequenceSelect = new SequenceSelect(pkg,
                tableName);
        execute(sequenceSelect);

        long nextId = sequenceSelect.getNextVal();
        return nextId;
    }

    public static Date getSysDateTime() throws SQLException {
        DataBaseDateTimeSelect select = new DataBaseDateTimeSelect();
        execute(select);
        return select.getSysDateTime();
    }

    public static String getVersion() throws SQLException {
        DataBaseVersionSelect select = new DataBaseVersionSelect();
        execute(select);
        return select.getDbVersion();
    }

    /**
     * Int to bool.
     *
     * @param in
     *            the in
     * @return true, if successful
     */
    public static boolean IntToBool(Integer in) {
        return (in != 0) ? true : false;
    }

    /**
     * Bool to int.
     *
     * @param boo
     *            the boo
     * @return the int
     */
    public static int BoolToInt(boolean boo) {
        return (boo) ? 1 : 0;
    }

    /**
     * Round float.
     *
     * @param f
     *            the f
     * @return the string
     */
    public static String RoundFloat(Float f) {
        // Р—Р° Р·Р°РєСЂСЉРіР»СЏРЅРµ РЅР° float РґРѕ РґРІР° СЃРёРјРІРѕР»Р° СЃР»РµРґ РґРµСЃРµС‚РёС‡РЅР°С‚Р° Р·Р°РїРµС‚Р°СЏ
        DecimalFormat df = new DecimalFormat("0.00");
        try {
            return df.format(f)
                    .replace(",",
                            ".");
        } catch (Exception e) {
            // LOG.warn(e.getMessage());
            return "";
        }
    }

    /**
     * Round double.
     *
     * @param f
     *            the f
     * @return the string
     */
    public static String RoundDouble(Double f) {
        // Р—Р° Р·Р°РєСЂСЉРіР»СЏРЅРµ РЅР° float РґРѕ РґРІР° СЃРёРјРІРѕР»Р° СЃР»РµРґ РґРµСЃРµС‚РёС‡РЅР°С‚Р° Р·Р°РїРµС‚Р°СЏ
        DecimalFormat df = new DecimalFormat("#.##");
        try {
            return df.format(f)
                    .replace(",",
                            ".");
        } catch (Exception e) {
            // LOG.warn(e.getMessage());
            return "";
        }
    }

    /**
     * Round big decimal.
     *
     * @param bd
     *            the bd
     * @return the string
     */
    public static String RoundBigDecimal(BigDecimal bd) {
        // Р—Р° Р·Р°РєСЂСЉРіР»СЏРЅРµ РЅР° float РґРѕ РґРІР° СЃРёРјРІРѕР»Р° СЃР»РµРґ РґРµСЃРµС‚РёС‡РЅР°С‚Р° Р·Р°РїРµС‚Р°СЏ
        if (bd == null)
            return "";
        DecimalFormat df = new DecimalFormat("#.##");
        double doublePayment = bd.doubleValue();
        String s = df.format(doublePayment)
                .replace(",",
                        ".");
        return s;
    }

    public static void closeDataSource() {
        if (DBConfig.connectionFactory != null)
            DBConfig.connectionFactory.closeDataSource();
    }

}