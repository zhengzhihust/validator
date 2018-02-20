/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.dal.dataobject;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhengzhi
 */
public class Record implements Serializable {

    private static final long serialVersionUID = -5012102323662973986L;

    private Long id;
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Record{");
        sb.append("id=").append(id);
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }
}
