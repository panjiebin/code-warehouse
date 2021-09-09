package com.code.common.rdb.parser;

import com.code.common.rdb.ColumnMeta;
import com.code.common.rdb.IResultParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pan Jiebin
 * @date 2020-09-10 11:31
 */
public class MapStringParser implements IResultParser<Map<String, String>> {

    @Override
    public Map<String, String> parse(ResultSet res, Map<Integer, ColumnMeta> columnMetas) {
        Map<String, String> map = new HashMap<>(8);
        try {
            for (ColumnMeta columnMeta : columnMetas.values()) {
                map.put(columnMeta.getColumnName(), res.getString(columnMeta.getColumnName()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    private static Reader readBlob(Blob blob) {
        StringBuilder longText = new StringBuilder();
        InputStream is = null;
        try {
            is = blob.getBinaryStream();
            byte[] buf = new byte[1024];
            int length;
            while ((length = is.read(buf)) != -1) {
                if (length < 1024) {
                    byte[] temp = new byte[length];
                    System.arraycopy(buf, 0, temp, 0, temp.length);
                    longText.append(new String(temp, "gbk"));
                } else {
                    longText.append(new String(buf, "gbk"));
                }

            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new StringReader(longText.toString());
    }

    private static String readClob(Clob clob) {
        StringBuilder longText = new StringBuilder();
        Reader reader = null;
        try {
            reader = clob.getCharacterStream();
            char[] buf = new char[1024];
            int length;
            while ((length = reader.read(buf)) != -1) {
                if (length < 1024) {
                    char[] temp = new char[length];
                    System.arraycopy(buf, 0, temp, 0, temp.length);
                    longText.append(new String(temp));
                } else {
                    longText.append(new String(buf));
                }

            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return longText.toString();
    }
}
