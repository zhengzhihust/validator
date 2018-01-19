/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.core;

import com.welink.hub.core.dto.ValidateContext;

public interface Validator {
    boolean validate(ValidateContext context);
}