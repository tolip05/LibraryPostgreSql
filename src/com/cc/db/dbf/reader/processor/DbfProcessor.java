package com.cc.db.dbf.reader.processor;

import com.cc.db.dbf.reader.DbfReader;
import com.cc.db.dbf.reader.exception.DbfException;
import com.cc.db.dbf.reader.structure.DbfDataType;
import com.cc.db.dbf.reader.structure.DbfField;
import com.cc.db.dbf.reader.structure.DbfHeader;
import com.cc.db.dbf.reader.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DbfProcessor {
    private static final int INDEX_WIDTH = 4;
    private static final int NAME_WIDTH = 16;
    private static final int TYPE_WIDTH = 8;
    private static final int LENGTH_WIDTH = 8;
    private static final int DECIMAL_WIDTH = 8;

    private DbfProcessor() {
    }

    /**
     *
     * @param <T>
     *            Output type
     * @param inputStream
     *            Input stream
     * @param rowMapper
     *            Row mapper
     * @return Mapped rows
     * @throws DbfException
     */
    public static <T> List<T> loadData(InputStream inputStream,
                                       DbfRowMapper<T> rowMapper)
            throws DbfException {
        try (DbfReader reader = new DbfReader(inputStream)) {
            List<T> result = new ArrayList<>(reader.getRecordCount());
            Object[] row;
            while ((row = reader.nextRecord()) != null) {
                result.add(rowMapper.mapRow(row));
            }

            return result;
        }
    }

    public static <T> List<T> loadData(File dbf,
                                       DbfRowMapper<T> rowMapper)
            throws DbfException {
        try (DbfReader reader = new DbfReader(dbf)) {
            List<T> result = new ArrayList<>(reader.getRecordCount());
            Object[] row;
            while ((row = reader.nextRecord()) != null) {
                result.add(rowMapper.mapRow(row));
            }

            return result;
        }
    }

    public static void processDbf(File dbf,
                                  DbfRowProcessor rowProcessor)
            throws DbfException {
        try (DbfReader reader = new DbfReader(dbf)) {
            Object[] row;
            while ((row = reader.nextRecord()) != null) {
                rowProcessor.processRow(row);
            }
        }
    }

    public static void writeToTxtFile(File dbf,
                                      File txt,
                                      Charset dbfEncoding) {
        try (DbfReader reader = new DbfReader(dbf); PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(txt)))) {
            DbfHeader header = reader.getHeader();

            String[] titles = new String[header.getFieldsCount()];
            for (int i = 0; i < header.getFieldsCount(); i++) {
                DbfField field = header.getField(i);
                titles[i] = StringUtils.rightPad(field.getName(),
                        field.getFieldLength(),
                        ' ');
            }

            for (String title : titles)
                writer.print(title);
            writer.println();

            Object[] row;
            while ((row = reader.nextRecord()) != null) {
                for (int i = 0; i < header.getFieldsCount(); i++) {
                    DbfField field = header.getField(i);
                    String value = field.getDataType() == DbfDataType.CHAR ? new String((byte[]) row[i],
                            dbfEncoding)
                            : String.valueOf(row[i]);
                    writer.print(StringUtils.rightPad(value,
                            field.getFieldLength(),
                            ' '));
                }
                writer.println();
            }
        } catch (IOException e) {
            throw new DbfException("Cannot write .dbf file to .txt",
                    e);
        }
    }

    /**
     * Create string with dbf information: - creation date - total records count
     * - columns info
     *
     * @param dbf
     *            .dbf file
     * @return string with dbf information
     */
    public static String readDbfInfo(File dbf) {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(dbf)))) {
            DbfHeader header = DbfHeader.read(in);
            StringBuilder sb = new StringBuilder(512);
            sb.append("Created at: ")
                    .append(header.getYear())
                    .append('-')
                    .append(header.getMonth())
                    .append('-')
                    .append(header.getDay())
                    .append('\n')
                    .append("Total records: ")
                    .append(header.getNumberOfRecords())
                    .append('\n')
                    .append("Header length: ")
                    .append(header.getHeaderLength())
                    .append('\n')
                    .append("Columns: ")
                    .append('\n');

            sb.append("  ")
                    .append(StringUtils.rightPad("#",
                            INDEX_WIDTH,
                            ' '))
                    .append(StringUtils.rightPad("Name",
                            NAME_WIDTH,
                            ' '))
                    .append(StringUtils.rightPad("Type",
                            TYPE_WIDTH,
                            ' '))
                    .append(StringUtils.rightPad("Length",
                            LENGTH_WIDTH,
                            ' '))
                    .append(StringUtils.rightPad("Decimal",
                            DECIMAL_WIDTH,
                            ' '))
                    .append('\n');

            int totalWidth = INDEX_WIDTH
                    + NAME_WIDTH
                    + TYPE_WIDTH
                    + LENGTH_WIDTH
                    + DECIMAL_WIDTH
                    + 2;
            for (int i = 0; i < totalWidth; i++)
                sb.append('-');

            for (int i = 0; i < header.getFieldsCount(); i++) {
                DbfField field = header.getField(i);
                sb.append('\n')
                        .append("  ")
                        .append(StringUtils.rightPad(String.valueOf(i),
                                INDEX_WIDTH,
                                ' '))
                        .append(StringUtils.rightPad(field.getName(),
                                NAME_WIDTH,
                                ' '))
                        .append(StringUtils.rightPad(String.valueOf((char) field.getDataType().byteValue),
                                TYPE_WIDTH,
                                ' '))
                        .append(StringUtils.rightPad(String.valueOf(field.getFieldLength()),
                                LENGTH_WIDTH,
                                ' '))
                        .append(StringUtils.rightPad(String.valueOf(field.getDecimalCount()),
                                DECIMAL_WIDTH,
                                ' '));
            }

            return sb.toString();
        } catch (IOException e) {
            throw new DbfException("Cannot read header of .dbf file "
                    + dbf,
                    e);
        }
    }
}