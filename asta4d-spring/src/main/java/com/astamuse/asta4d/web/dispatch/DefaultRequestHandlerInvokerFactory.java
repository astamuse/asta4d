package com.astamuse.asta4d.web.dispatch;

public class DefaultRequestHandlerInvokerFactory implements RequestHandlerInvokerFactory {

    private RequestHandlerInvoker invoker = new DefaultRequestHandlerInvoker();

    /* (non-Javadoc)
     * @see com.astamuse.asta4d.web.dispatch.RequestHandlerInvokerFactory#getInvoker()
     */
    @Override
    public RequestHandlerInvoker getInvoker() {
        return invoker;
    }

}
