package com.astamuse.asta4d.misc.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.astamuse.asta4d.web.util.DeclareInstanceResolver;

public class SpringManagedInstanceResolver implements DeclareInstanceResolver, ApplicationContextAware {

    private ApplicationContext beanCtx;

    public SpringManagedInstanceResolver() {

    }

    public SpringManagedInstanceResolver(ApplicationContext beanCtx) {
        this.beanCtx = beanCtx;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanCtx = applicationContext;
    }

    @Override
    public Object resolve(Object declaration) {

        if (declaration instanceof Class) {
            Class<?> beanCls = (Class<?>) declaration;
            String[] names = beanCtx.getBeanNamesForType(beanCls);
            boolean beanExist = false;
            for (String name : names) {
                if (beanCtx.containsBean(name)) {
                    beanExist = true;
                    break;
                }
            }
            if (beanExist) {
                return new SpringManagedInstanceAdapter(beanCtx, beanCls, null);
            } else {
                return null;
            }
        } else if (declaration instanceof String) {
            String beanId = declaration.toString();
            if (beanCtx.containsBean(beanId)) {
                return new SpringManagedInstanceAdapter(beanCtx, null, beanId);
            } else {
                return null;
            }
        }
        return null;

    }

}
