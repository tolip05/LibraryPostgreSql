package com.cc.pojo;

import com.cc.db.statement.SelectSqlStatement;
import com.cc.db.util.DBUtil;
import com.cc.logging.LOG;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.BiConsumer;

public class LargeObjectSelect extends SelectSqlStatement {
    private final String className;
    private final String sqlString;
    private List<com.cc.pojo.Param> queryParams;
    private LargeObjectManager lobj;

    private BiConsumer<byte[], Integer> consummer;

    public LargeObjectSelect(String className,
                             String sqlString,
                             BiConsumer<byte[], Integer> consummer,
                             List<Param> params) {
        this.className = className;
        this.sqlString = sqlString;
        this.consummer = consummer;
        queryParams = params;
    }

    public LargeObjectSelect(String className,
                             String sqlString,
                             BiConsumer<byte[], Integer> consummer) {
        this(className,
                sqlString,
                consummer,
                null);
    }

    @Override
    protected String getSqlString() {
        return sqlString;
    }

    @Override
    protected void setParameters(PreparedStatement prStmt) throws SQLException {
        List<String> parametersAsString = Param.setParameters(prStmt,
                queryParams);
        setParametersAsString(parametersAsString);
        lobj = prStmt.getConnection()
                .unwrap(org.postgresql.PGConnection.class)
                .getLargeObjectAPI();
    }

    @Override
    protected void retrieveResult(ResultSet rs) throws SQLException {
        while (rs.next()) {
            // Open the large object for reading
            long oid = rs.getLong(1);
            LargeObject obj = lobj.open(oid,
                    LargeObjectManager.READ);
            byte buf[] = new byte[2
                    * 1024
                    * 1024];
            int read, offset = 0;
            // Read the data
            while ((read = obj.read(buf,
                    0,
                    buf.length)) > 0) {
                consummer.accept(buf, read);
                offset += read;
                LOG.getLog(DBUtil.class)
                        .info(String.format("%d MB read!",
                                offset
                                        / 1024
                                        / 1024));
            }

            // Close the object
            obj.close();
        }
    }

    public void execute() throws SQLException {
        DBUtil.execute(this);
    }

    @Override
    public String getClassName() {
        return className;
    }
}