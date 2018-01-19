/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.dal.cao;

import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.TairManager;
import com.welink.hub.dal.dto.AbstractDataEntry;
import com.welink.hub.dal.exception.CacheVersionException;
import com.welink.hub.dal.exception.IncrOutOfBoundsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class TairCache implements Cache {

    private static final Logger LOG = LoggerFactory.getLogger(TairCache.class);
    private TairManager tairManager;
    private int namespace;

    @Override
    public AbstractDataEntry<Object> get(Serializable key) {
        Result<com.taobao.tair.DataEntry> result = tairManager.get(namespace, key);
        return transDataResult(result);
    }

    @Override
    public void cas(Serializable key, Serializable value, int version, int expireTime)
            throws CacheVersionException {
        ResultCode rc = tairManager.put(namespace, key, value, version, expireTime);
        if (rc == ResultCode.VERERROR) {
            throw new CacheVersionException("the ResultCode is " + rc.getCode()
                    + "; error message is " + rc.getMessage() + "; key: " + key);
        }
        if (rc != ResultCode.SUCCESS) {
            throw new RuntimeException("the ResultCode is " + rc.getCode() + "; error message is "
                    + rc.getMessage() + "; key: " + key);
        }
    }

    @Override
    public void delete(Serializable key) {
        LOG.info("remove key: {}", key);
        ResultCode resultCode = tairManager.delete(namespace, key);
        if (resultCode == ResultCode.DATANOTEXSITS) {
            LOG.error("delete fail! key: " + key + " result code: " + resultCode);
            return;
        }
        if (resultCode != ResultCode.SUCCESS) {
            throw new RuntimeException("delete fail! key: " + key + " result code: " + resultCode);
        }
    }

    public void setTairManager(TairManager tairManager) {
        this.tairManager = tairManager;
    }

    public void setNamespace(int namespace) {
        this.namespace = namespace;
    }


    @Override
    public Integer incr(Serializable key, int value, int defaultValue, int expireTime,
                        int lowBound, int upperBound) throws IncrOutOfBoundsException {
        Result<Integer> result = tairManager.incr(namespace, key, value, defaultValue, expireTime);
        ResultCode rc = result.getRc();
        if (rc == ResultCode.SUCCESS) {
            return result.getValue();
        } else if (rc == ResultCode.OUTOFRANGE) {
            throw new IncrOutOfBoundsException();
        } else {
            throw new RuntimeException("incr error rc: " + rc);
        }
    }


    private AbstractDataEntry<Object> transDataResult(Result<com.taobao.tair.DataEntry> result) {
        final com.taobao.tair.DataEntry dataEntry = result.getValue();
        ResultCode rc = result.getRc();
        if (rc == ResultCode.SUCCESS) {
            return transDataEntry(dataEntry);
        } else if (rc == ResultCode.DATAEXPIRED || rc == ResultCode.DATANOTEXSITS) {
            return null;
        } else {
            throw new RuntimeException("" + rc);
        }
    }

    private AbstractDataEntry<Object> transDataEntry(final com.taobao.tair.DataEntry dataEntry) {
        return new AbstractDataEntry<Object>() {

            @Override
            public int getVersion() {
                return dataEntry.getVersion();
            }

            @Override
            public Object getValue() {
                return dataEntry.getValue();
            }

            @Override
            public Serializable getKey() {
                return (Serializable) dataEntry.getKey();
            }

            @Override
            public int getExpireTime() {
                return dataEntry.getExpriedDate();
            }

            @Override
            public long getCreateTime() {
                return dataEntry.getCreateDate() * 1000 + System.currentTimeMillis();
            }
        };
    }
}