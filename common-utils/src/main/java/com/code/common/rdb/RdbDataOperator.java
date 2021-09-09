package com.code.common.rdb;

import com.code.common.utils.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

/**
 * batch insert util
 *
 * @author Pan Jiebin
 * @date 2020-09-14 23:31
 */
public class RdbDataOperator<T> {

    private final Logger logger = LoggerFactory.getLogger(RdbDataOperator.class);
    private Connection conn;
    private PreparedStatement pst;
    private final Function<T, Map<Integer, Object>> converter;
    private final RdbConfig config;
    private boolean isInit = true;

    public RdbDataOperator(Function<T, Map<Integer, Object>> converter, RdbConfig config) {
        this.converter = converter;
        this.config = config;
    }

    public void insert(Iterator<T> iterator) {
        if (isInit) {
            init();
        }
        long start = Profiler.begin();
        try {
            int count = 0;
            while (iterator.hasNext()) {
                count++;
                Map<Integer, Object> params = converter.apply(iterator.next());
                if (params != null && !params.isEmpty()) {
                    for (int i = 1; i < params.size() + 1; i++) {
                        pst.setObject(i, params.get(i));
                    }
                    pst.addBatch();
                }
                if (count % config.getTipNum() == 0) {
                    pst.executeBatch();
                    conn.commit();
                    if (logger.isDebugEnabled()) {
                        logger.debug("inserted [{}] pieces of data,takes: [{}] s,total: [{}]", config.getTipNum(), Profiler.end(start)/1000, count);
                        start = Profiler.begin();
                    }
                }
            }
            pst.executeBatch();
            conn.commit();
            if (logger.isInfoEnabled()) {
                logger.info("inserted completed, total: [{}]", count);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close();
            Profiler.clear();
        }
    }

    public void init() {
        try {
            Class.forName(config.getDriverName());
            conn = DriverManager.getConnection(config.getUrl(), config.getUserName(), config.getPassword());
            if (logger.isDebugEnabled()) {
                logger.debug("connected successful!");
            }
            conn.setAutoCommit(false);
            pst = conn.prepareStatement(config.getSql());
            if (logger.isInfoEnabled()) {
                logger.info("sql: [{}]", config.getSql());
            }
            this.isInit = false;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void close() {
        try {
            if (pst != null) {
                pst.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
