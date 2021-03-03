package com.cc.db.dbf.reader.structure;

import com.cc.db.dbf.reader.exception.DbfException;
import com.cc.db.dbf.reader.util.DbfUtils;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbfHeader {
    private byte year; /* 1 */
    private byte month; /* 2 */
    private byte day; /* 3 */
    private int numberOfRecords; /* 4-7 */
    private short headerLength; /* 8-9 */
    private short recordLength; /* 10-11 */
    private List<DbfField> fields; /* each 32 bytes */

    private Map<String, Integer> fieldIndexesByNames;

    public static DbfHeader read(DataInput dataInput) throws DbfException {
        try {
            DbfHeader header = new DbfHeader();

            dataInput.skipBytes(1); /* 0 */
            header.year = dataInput.readByte(); /* 1 */
            header.month = dataInput.readByte(); /* 2 */
            header.day = dataInput.readByte(); /* 3 */
            header.numberOfRecords = DbfUtils.readLittleEndianInt(dataInput); /*
             * 4
             * -
             * 7
             */

            header.headerLength = DbfUtils.readLittleEndianShort(dataInput); /*
             * 8
             * -
             * 9
             */
            header.recordLength = DbfUtils.readLittleEndianShort(dataInput); /*
             * 10
             * -
             * 11
             */

            dataInput.skipBytes(20);
            header.fields = new ArrayList<>();
            DbfField field;
            int fieldIndex = 0;
            while ((field = DbfField.read(dataInput,
                    fieldIndex++)) != null) { /*
             * 32 each
             */
                header.fields.add(field);
            }

            return header;
        } catch (IOException e) {
            throw new DbfException("Cannot read Dbf header",
                    e);
        }
    }

    public short getHeaderLength() {
        return headerLength;
    }

    public int getFieldsCount() {
        return fields.size();
    }

    public byte getYear() {
        return year;
    }

    public byte getMonth() {
        return month;
    }

    public byte getDay() {
        return day;
    }

    public DbfField getField(int i) {
        return fields.get(i);
    }

    public DbfField getField(String fieldName) {
        return getField(getFieldIndex(fieldName));
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public short getRecordLength() {
        return recordLength;
    }

    public int getFieldIndex(String fieldName) {
        if (fieldIndexesByNames == null) {
            initFieldIndexesByNames();
        }
        Integer index = fieldIndexesByNames.get(fieldName);
        return index == null ? -1 : index;
    }

    private void initFieldIndexesByNames() {
        fieldIndexesByNames = new HashMap<>(fields.size());
        for (int i = 0; i < fields.size(); i++) {
            DbfField field = fields.get(i);
            fieldIndexesByNames.put(field.getName(),
                    i);
        }
    }
}
