package com.cc.pojo;

import com.cc.annotation.Parameters.ParamTypes;
import com.cc.db.util.DBUtil;
import com.cc.db.util.DateUtil;
import com.cc.logging.LOG;
import com.cc.model.IModelWithId;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Param {
    public final ParamTypes paramType;
    public final Object paramValue;

    public Param(ParamTypes paramType,
                 Object paramValue) {
        this.paramType = paramType;
        this.paramValue = paramValue;
    }

    @SuppressWarnings("unchecked")
    public static List<String> setParameters(PreparedStatement prStmt,
                                             List<Param> queryParams)
            throws SQLException {
        List<String> parametersAsString = new ArrayList<String>();
        if (queryParams != null)
            for (int i = 1; i <= queryParams.size(); i++) {
                Param param = queryParams.get(i
                        - 1);
                switch (param.paramType) {
                    case BigDecimal:
                        if (param.paramValue == null)
                            prStmt.setNull(i,
                                    Types.NUMERIC);
                        else
                            prStmt.setBigDecimal(i,
                                    (BigDecimal) param.paramValue);
                        if (param.paramValue != null)
                            parametersAsString.add(String.valueOf((BigDecimal) param.paramValue));
                        else
                            parametersAsString.add("null");
                        break;
                    case Integer:
                        if (param.paramValue == null)
                            prStmt.setNull(i,
                                    Types.NUMERIC);
                        else
                            prStmt.setInt(i,
                                    (Integer) param.paramValue);
                        if (param.paramValue != null)
                            parametersAsString.add(String.valueOf((Integer) param.paramValue));
                        else
                            parametersAsString.add("null");
                        break;
                    case Long:
                        if (param.paramValue == null)
                            prStmt.setNull(i,
                                    Types.NUMERIC);
                        else
                            prStmt.setLong(i,
                                    (Long) param.paramValue);
                        if (param.paramValue != null)
                            parametersAsString.add(String.valueOf((Long) param.paramValue));
                        else
                            parametersAsString.add("null");
                        break;
                    case String:
                        if (param.paramValue == null)
                            prStmt.setNull(i,
                                    Types.VARCHAR);
                        else
                            prStmt.setString(i,
                                    (String) param.paramValue);
                        if (param.paramValue != null)
                            parametersAsString.add("'"
                                    + (String) param.paramValue
                                    + "'");
                        else
                            parametersAsString.add("null");
                        break;
                    case Date:
                        if (param.paramValue == null)
                            prStmt.setNull(i,
                                    Types.DATE);
                        else
                            prStmt.setDate(i,
                                    DateUtil.toSQLDate((Date) param.paramValue));
                        if (param.paramValue != null)
                            parametersAsString.add(DateUtil.sdfDate.format((Date) param.paramValue));
                        else
                            parametersAsString.add("null");
                        break;
                    case Time:
                        if (param.paramValue == null)
                            prStmt.setNull(i,
                                    Types.TIME);
                        else
                            prStmt.setTime(i,
                                    DateUtil.toTime((Date) param.paramValue));
                        if (param.paramValue != null)
                            parametersAsString.add(DateUtil.sdfTime.format((Date) param.paramValue));
                        else
                            parametersAsString.add("null");
                        break;
                    case Timestamp:
                        if (param.paramValue == null)
                            prStmt.setNull(i,
                                    Types.TIMESTAMP);
                        else
                            prStmt.setTimestamp(i,
                                    DateUtil.toTimestamp((Date) param.paramValue));
                        if (param.paramValue != null)
                            parametersAsString.add(DateUtil.sdfTimestamp.format((Date) param.paramValue));
                        else
                            parametersAsString.add("null");
                        break;
                    case Boolean:
                        if (param.paramValue == null)
                            prStmt.setNull(i,
                                    Types.BOOLEAN);
                        else
                            prStmt.setBoolean(i,
                                    (Boolean) param.paramValue);
                        if (param.paramValue != null)
                            parametersAsString.add(String.valueOf((Boolean) param.paramValue));
                        else
                            parametersAsString.add("null");
                        break;
                    case IntegerArray:
                        if (param.paramValue == null)
                            prStmt.setNull(i,
                                    Types.ARRAY);
                        else
                            prStmt.setArray(i,
                                    prStmt.getConnection()
                                            .createArrayOf("numeric",
                                                    ((List<Integer>) param.paramValue).toArray()));
                        if (param.paramValue != null)
                            if (param.paramValue != null)
                                parametersAsString.add(Arrays.toString(((List<Integer>) param.paramValue).toArray()));
                            else
                                parametersAsString.add("null");
                        else
                            parametersAsString.add("null");
                        break;
                    case LongArray:
                        if (param.paramValue == null)
                            prStmt.setNull(i,
                                    Types.ARRAY);
                        else
                            prStmt.setArray(i,
                                    prStmt.getConnection()
                                            .createArrayOf("numeric",
                                                    ((List<Long>) param.paramValue).toArray()));
                        if (param.paramValue != null)
                            parametersAsString.add(Arrays.toString(((List<Long>) param.paramValue).toArray()));
                        else
                            parametersAsString.add("null");
                        break;
                    case StringArray:
                        if (param.paramValue == null)
                            prStmt.setNull(i,
                                    Types.ARRAY);
                        else
                            prStmt.setArray(i,
                                    prStmt.getConnection()
                                            .createArrayOf("varchar",
                                                    ((List<String>) param.paramValue).toArray()));
                        if (param.paramValue != null)
                            parametersAsString.add(Arrays.toString(((List<String>) param.paramValue).toArray()));
                        else
                            parametersAsString.add("null");
                        break;
                    case ByteArray:
                        if (param.paramValue == null)
                            prStmt.setNull(i,
                                    Types.ARRAY);
                        else
                            prStmt.setBytes(i,
                                    (byte[]) param.paramValue);
                        break;
                    case LargeObject:
                        if (param.paramValue == null)
                            prStmt.setNull(i,
                                    Types.ARRAY);
                        else {

                            LargeObjectManager lobj = prStmt.getConnection()
                                    .unwrap(org.postgresql.PGConnection.class)
                                    .getLargeObjectAPI();
                            // Create a new large object
                            long oid = lobj.createLO(LargeObjectManager.READ
                                    | LargeObjectManager.WRITE);
                            // Open the large object for writing
                            try (LargeObject obj = lobj.open(oid,
                                    LargeObjectManager.WRITE)) {
                                InputStream is = (InputStream) param.paramValue;
                                byte buf[] = new byte[2
                                        * 1024
                                        * 1024];
                                int read, offset = 0;
                                LOG.getLog(DBUtil.class)
                                        .info("Writing large file");
                                while ((read = is.read(buf)) > 0) {
                                    obj.write(buf,
                                            0,
                                            read);
                                    offset += read;
                                    LOG.getLog(DBUtil.class)
                                            .info(String.format("%d MB written!",
                                                    offset
                                                            / 1024
                                                            / 1024));
                                }
                                // Close the large object
                                prStmt.setLong(i,
                                        oid);
                            } catch (IOException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        }
                        break;
                    case ModelWithId:
                        if (param.paramValue == null
                                || ((IModelWithId) param.paramValue).getId() == null)
                            prStmt.setNull(i,
                                    Types.NUMERIC);
                        else
                            prStmt.setLong(i,
                                    ((IModelWithId) param.paramValue).getId());
                        if (param.paramValue != null)
                            parametersAsString.add(String.valueOf(((IModelWithId) param.paramValue).getId()));
                        else
                            parametersAsString.add("null");
                        break;
                    default:
                        break;
                }
            }
        return parametersAsString;
    }
}