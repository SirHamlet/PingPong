package com.serge.pingpong.misc;

import org.springframework.context.ApplicationContext;

/**
 * @author Serge Ivanov
 * Holds Spring context for better access from Netty EventLoop
 */
public class ContextHolder {

    private static final ContextHolder INSTANCE  = new ContextHolder();

    private ApplicationContext context;

    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    public static ContextHolder getInstance() {
        return INSTANCE;
    }
}
