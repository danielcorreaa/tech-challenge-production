package com.techchallenge.config;

import org.hibernate.dialect.MySQLDialect;


public class MysqlCustomDialect extends MySQLDialect {

    @Override
    public String getTableTypeString() {
        return " ENGINE=innodb DEFAULT CHARSET=utf8 COLLATE=utf8_bin";
    }
}
