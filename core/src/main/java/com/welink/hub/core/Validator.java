/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.core;

import com.welink.hub.core.context.ValidateContext;

/**
 * @author zhengzhi
 */
public interface Validator {
    /**
     * @param context
     * @return
     */
    boolean validate(ValidateContext context);
}
