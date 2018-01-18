/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.validator.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.welink.hub.validator.ThreadPoolService;
import com.welink.hub.validator.Validator;
import com.welink.hub.validator.ValidatorExecutor;
import com.welink.hub.validator.annotation.ABTest;
import com.welink.hub.validator.annotation.ValidatorAnno;
import com.welink.hub.validator.context.ContextManager;
import com.welink.hub.validator.context.RunnableWithContext;
import com.welink.hub.validator.dto.ValidateContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.PostConstruct;
import java.util.*;

public class ValidatorExecutorImpl implements ValidatorExecutor, ApplicationContextAware {

    @Autowired
    @Qualifier("threadPoolService")
    private ThreadPoolService threadPoolService;
    @Value("${is.main}")
    private boolean isMain;
    private ApplicationContext applicationContext;
    private Map<String, List<Validator>> chainToValidators = Maps.newHashMap();
    private Map<Class<?>, Validator> mainValidatorClassToSubValidator = Maps.newHashMap();

    private static final Comparator<Validator> COMPARATOR =
            new Comparator<Validator>() {
                @Override
                public int compare(Validator o1, Validator o2) {
                    final String orderKey = "order";
                    int thisOrder = (int) AnnotatedElementUtils.getMergedAnnotationAttributes(o1.getClass(), ValidatorAnno.class).get(orderKey);
                    int thatOrder = (int) AnnotatedElementUtils.getMergedAnnotationAttributes(o2.getClass(), ValidatorAnno.class).get(orderKey);
                    return thisOrder - thatOrder;
                }
            };

    @PostConstruct
    public void init() {
        Map<String, Object> beansWithValidatorAnno = applicationContext.getBeansWithAnnotation(ValidatorAnno.class);
        for(Map.Entry<String, Object> entry : beansWithValidatorAnno.entrySet()) {
            Validator validator = (Validator) entry.getValue();
            String chain = AnnotatedElementUtils.getMergedAnnotationAttributes(validator.getClass(), ValidatorAnno.class).getString("chain");
            List<Validator> validators = chainToValidators.get(chain);
            if (validators == null) {
                chainToValidators.put(chain, Lists.newArrayList());
            }
            chainToValidators.get(chain).add(validator);
        }
        for (Map.Entry<String, List<Validator>> entry : chainToValidators.entrySet()) {
            Collections.sort(entry.getValue(), COMPARATOR);
        }
        Map<String, Object> beansWithAbTest = applicationContext.getBeansWithAnnotation(ABTest.class);
        for (Map.Entry<String, Object> entry : beansWithAbTest.entrySet()) {
            Validator validator = (Validator) entry.getValue();
            ABTest abTest = AnnotationUtils.getAnnotation(validator.getClass(), ABTest.class);
            if (abTest != null) {
                mainValidatorClassToSubValidator.put(abTest.testFor(), validator);
                continue;
            }
        }
    }

    @Override
    public void execute(ValidateContext context, String chain) {
        for (Validator validator : chainToValidators.get(chain)) {
            Validator abTestValidator = mainValidatorClassToSubValidator.get(validator.getClass());
            Validator mainThreadValidator = validator;
            boolean mainValiadtorPass;
            if (abTestValidator != null) {
                ABTest abTest = AnnotationUtils.getAnnotation(abTestValidator.getClass(), ABTest.class);
                final Validator subThreadValidator = isMain ? validator : abTestValidator;
                mainThreadValidator = isMain ? abTestValidator : validator;
                mainValiadtorPass = mainThreadValidator.validate(context);
                ContextManager.Context threadContext = ContextManager.getContext();
                threadPoolService.execute(new RunnableWithContext() {
                    @Override
                    public void doRun() {
                        subThreadValidator.validate(context.clone());
                    }

                    @Override
                    public String getLoggerName() {
                        if (StringUtils.isEmpty(abTest.loggerName())) {
                            return null;
                        } else {
                            return abTest.loggerName();
                        }
                    }

                    @Override
                    public List<String> getLogFieldNames() {
                       return Arrays.asList(abTest.logFieldNames());
                    }

                    @Override
                    public void buildContext() {
                        ContextManager.build(threadContext);
                    }
                });
            } else {
                mainValiadtorPass = mainThreadValidator.validate(context);
            }
            if (!mainValiadtorPass) {
                return;
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
