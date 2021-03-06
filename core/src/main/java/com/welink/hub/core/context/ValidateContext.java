/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.core.context;

/**
 * @Author cheng
 */
public class ValidateContext implements Cloneable {
    private Long buyerUserId;
    private Long orderId;
    private String ip;

    private ValidateContext(Long buyerUserId, Long orderId) {
        super();
        this.buyerUserId = buyerUserId;
        this.orderId = orderId;
    }

    public Long getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(Long buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public ValidateContext clone() {
        ValidateContext clonedContext = new Builder().addBuyerUserId(buyerUserId).
                addOrderId(orderId).build();
        clonedContext.setIp(ip);
        return clonedContext;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ValidateContext{");
        sb.append("buyerUserId=").append(buyerUserId);
        sb.append(", orderId=").append(orderId);
        sb.append(", ip='").append(ip).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static class Builder {
        private Long buyerUserId;
        private Long orderId;

        public Builder addBuyerUserId(Long buyerUserId) {
            this.buyerUserId = buyerUserId;
            return this;
        }

        public Builder addOrderId(Long orderId) {
            this.orderId = orderId;
            return this;
        }

        public ValidateContext build() {
            return new ValidateContext(buyerUserId, orderId);
        }

    }
}
