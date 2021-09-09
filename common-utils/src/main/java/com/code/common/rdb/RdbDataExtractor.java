package com.code.common.rdb;

import com.code.common.rdb.parser.MapStringParser;
import com.code.common.utils.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * big data extract util
 *
 * @author Pan Jiebin
 * @date 2020-09-10 10:52
 */
public class RdbDataExtractor<E> implements Iterator<E> {

    private int tipNum = 10000;

    private final Logger logger = LoggerFactory.getLogger(RdbDataExtractor.class);

    private boolean isNext = true;
    private Connection conn;
    private PreparedStatement pst;
    private ResultSet res;
    private final RdbConfig config;
    private final IResultParser<E> parser;
    private int total = 0;
    private Map<Integer, ColumnMeta> columnMetas;
    private long start;

    public RdbDataExtractor(RdbConfig config, IResultParser<E> parser) {
        this.config = config;
        this.parser = parser;
        tipNum = config.getTipNum() == 0 ? tipNum : config.getTipNum();
    }

    public void extract() {
        try {
            Class.forName(config.getDriverName());
            conn = DriverManager.getConnection(config.getUrl(), config.getUserName(), config.getPassword());
            if (logger.isDebugEnabled()) {
                logger.debug("connected successful!");
            }
            pst = conn.prepareStatement(config.getSql(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            if (logger.isInfoEnabled()) {
                logger.info("extract sql: [{}]", config.getSql());
            }
            if (config.getParams() != null && !config.getParams().isEmpty()) {
                for (Map.Entry<Integer, Object> entry : config.getParams().entrySet()) {
                    pst.setObject(entry.getKey(), entry.getValue());
                }
            }
            res = pst.executeQuery();
            res.setFetchSize(config.getFetchSize());
            ResultSetMetaData resMetaData = res.getMetaData();
            columnMetas = new HashMap<>(resMetaData.getColumnCount());
            for (int i = 1; i <= resMetaData.getColumnCount(); i++) {
                columnMetas.put(i, new ColumnMeta(resMetaData.getColumnType(i),
                        resMetaData.getColumnName(i), i, resMetaData.getColumnTypeName(i)));
            }
            start = Profiler.begin();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        if (null == res) {
            return false;
        }
        try {
            if (isNext && res.next()) {
                isNext = false;
            }
            if (isNext) {
                this.close();
                if (logger.isInfoEnabled()) {
                    logger.info("extract complete, total: [{}] ", this.total);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return !isNext;
    }

    @Override
    public E next() {
        this.total++;
        if (logger.isInfoEnabled() && this.total % tipNum == 0) {
            logger.info("extracted [{}}] pieces of data, takes: [{}] s, total :[{}}]", tipNum, Profiler.end(start)/1000, this.total);
            start = Profiler.begin();
        }
        isNext = true;
        return this.parser.parse(res, columnMetas);
    }

    private void close() throws SQLException {
        if (res != null) {
            res.close();
        }
        if (pst != null) {
            pst.close();
        }
        if (conn != null) {
            conn.close();
        }
    }

    public static IResultParser<Map<String, String>> MAP_STRING_PARSER = new MapStringParser();


}
