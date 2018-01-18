/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.validator;

import com.welink.hub.validator.dto.ValidateContext;

public interface Validator {
    boolean validate(ValidateContext context);
}
