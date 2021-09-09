package com.code.common.rdb;

/**
 * @author Pan Jiebin
 * @date 2020-09-10 10:54
 */
public class ColumnMeta {
    private int columnType;
    private int columnIndex;
    private String columnName;
    private String columnTypeName;

    public ColumnMeta(int columnType, String columnName, int columnIndex, String columnTypeName) {
        super();
        this.columnType = columnType;
        this.columnName = columnName;
        this.columnIndex = columnIndex;
        this.columnTypeName = columnTypeName;
    }

    public int getColumnType() {
        return columnType;
    }

    public void setColumnType(int columnType) {
        this.columnType = columnType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getColumnTypeName() {
        return columnTypeName;
    }

    public void setColumnTypeName(String columnTypeName) {
        this.columnTypeName = columnTypeName;
    }
}
