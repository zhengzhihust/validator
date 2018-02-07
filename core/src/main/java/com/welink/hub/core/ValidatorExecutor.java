package com.welink.hub.core;

import com.welink.hub.core.context.ValidateContext;

public interface ValidatorExecutor {
    /**
     * @param context
     * @param chain
     */
    void execute(ValidateContext context, String chain);
}
