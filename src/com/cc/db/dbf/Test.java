package com.cc.db.dbf;

import com.cc.db.dbf.reader.DbfReader;
import com.cc.db.dbf.reader.structure.DbfHeader;
import com.cc.db.dbf.reader.structure.DbfRow;

import java.io.File;

public class Test {
    public static void main(String[] args) {
        try (DbfReader reader = new DbfReader(new File("SV.DBF"))) {
            DbfHeader header = reader.getHeader();
            for (int rowNumber = 0; rowNumber < reader.getRecordCount(); rowNumber++) {
                DbfRow dbfRow = reader.nextRow();
                for (int fieldNumber = 0; fieldNumber < header.getFieldsCount(); fieldNumber++)
                    System.out.print(dbfRow.getObject(header.getField(fieldNumber)
                            .getName()));
                System.out.println();
            }
        }
    }
}