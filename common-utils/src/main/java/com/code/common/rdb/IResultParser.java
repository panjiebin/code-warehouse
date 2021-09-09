package com.code.common.rdb;

import java.sql.ResultSet;
import java.util.Map;

/**
 * @author Pan Jiebin
 * @date 2020-09-10 10:54
 */
public interface IResultParser<T> {

    T parse(ResultSet res, Map<Integer, ColumnMeta> columnMetas);
}
