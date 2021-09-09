package com.code.common.rdb;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Pan Jiebin
 * @date 2020-09-10 11:00
 */
public class RdbConfig {
    private final String userName;
    private final String url;
    private final String password;
    private final String driverName;
    private final String sql;
    private final Map<Integer, Object> params;
    private final int fetchSize;
    private final int tipNum;
    private final int batchSize;

    public RdbConfig(Builder builder) {
        this.userName = builder.userName;
        this.url = builder.url;
        this.password = builder.password;
        this.driverName = builder.driverName;
        this.sql = builder.sql;
        this.params = builder.params;
        this.fetchSize = builder.fetchSize;
        this.tipNum = builder.tipNum;
        this.batchSize = builder.batchSize;
    }

    public String getUserName() {
        return userName;
    }

    public String getUrl() {
        return url;
    }

    public String getPassword() {
        return password;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getSql() {
        return sql;
    }

    public Map<Integer, Object> getParams() {
        return params;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getTipNum() {
        return tipNum;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String userName;
        private String url;
        private String password;
        private String driverName;
        private String sql;
        private Map<Integer, Object> params;
        private int fetchSize = 5000;
        private int tipNum = 20000;
        private int batchSize = 5000;

        private Builder() {
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder driverName(String driverName) {
            this.driverName = driverName;
            return this;
        }

        public Builder sql(String sql) {
            this.sql = sql;
            return this;
        }

        public Builder addParam(Integer pos, Object value) {
            if (params == null) {
                params = new HashMap<>(4);
            }
            params.put(pos, value);
            return this;
        }

        public Builder fetchSize(int fetchSize) {
            this.fetchSize = fetchSize;
            return this;
        }

        public Builder tipNum(int tipNum) {
            this.tipNum = tipNum;
            return this;
        }

        public Builder batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public RdbConfig build() {
            return new RdbConfig(this);
        }
    }
}
