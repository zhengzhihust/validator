package com.welink.hub.validator;

import com.welink.hub.validator.dto.ValidateContext;

public interface ValidatorExecutor {
    void execute(ValidateContext context, String chain);
}
