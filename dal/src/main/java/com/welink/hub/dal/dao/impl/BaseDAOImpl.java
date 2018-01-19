/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.dal.dao.impl;

import com.welink.hub.dal.dao.BaseDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import java.lang.reflect.ParameterizedType;

public class BaseDAOImpl<Pk, DO> implements BaseDAO<Pk, DO> {

    private static final Logger LOG = LoggerFactory.getLogger(BaseDAOImpl.class);

    @Autowired
    protected SqlMapClientTemplate sqlMapClientTemplate;
    protected String namespace;
    protected String selectSqlName;
    protected String insertSqlName;
    protected String updateSqlName;
    protected String deleteSqlName;
    protected String countSqlName;

    public BaseDAOImpl() {
        Class type = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        String namespace = type.getName() + ".";
        this.namespace = namespace;
        selectSqlName = namespace + "select";
        insertSqlName = namespace + "insert";
        updateSqlName = namespace + "update";
        deleteSqlName = namespace + "delete";
        countSqlName = namespace + "count";
    }

    @SuppressWarnings("unchecked")
    @Override
    public DO select(Pk pk) {
        return (DO) sqlMapClientTemplate.queryForObject(selectSqlName, pk);
    }

    @Override
    public Object insert(DO d) {
        LOG.info("insert {}", d);
        return sqlMapClientTemplate.insert(insertSqlName, d);
    }

    @Override
    public int update(DO d) {
        LOG.info("update {}", d);
        return sqlMapClientTemplate.update(updateSqlName, d);
    }

    @Override
    public int delete(Pk pk) {
        LOG.info("delete {}", pk);
        return sqlMapClientTemplate.delete(deleteSqlName, pk);
    }

    public int count(DO d) {
        LOG.info("count {}", d);
        return (Integer) sqlMapClientTemplate.queryForObject(countSqlName, d);
    }
}
