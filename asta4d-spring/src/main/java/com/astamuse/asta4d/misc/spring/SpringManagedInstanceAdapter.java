package com.astamuse.asta4d.misc.spring;

import org.springframework.context.ApplicationContext;

import com.astamuse.asta4d.web.util.DeclareInstanceAdapter;

public class SpringManagedInstanceAdapter implements DeclareInstanceAdapter {

    private ApplicationContext beanCtx;

    private Class<?> beanCls = null;

    private String beanId = null;

    public SpringManagedInstanceAdapter(ApplicationContext beanCtx, Class<?> beanCls, String beanId) {
        this.beanCtx = beanCtx;
        this.beanCls = beanCls;
        this.beanId = beanId;
    }

    @Override
    public Object asTargetInstance() {
        if (beanCls != null) {
            return beanCtx.getBean(beanCls);
        } else if (beanId != null) {
            return beanCtx.getBean(beanId);
        } else {
            return null;
        }
    }

}
