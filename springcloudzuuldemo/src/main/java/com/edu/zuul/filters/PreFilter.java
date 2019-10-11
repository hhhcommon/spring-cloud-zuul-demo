package com.edu.zuul.filters;

import com.edu.zuul.config.DemoApolloConfig;
import com.edu.zuul.config.DemoGlobalConfig;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class PreFilter extends ZuulFilter {
    @Autowired
    private DemoGlobalConfig demoGlobalConfig;
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        System.out.println("Request Method : " + request.getMethod() + " Request URL : " + request.getRequestURL().toString());
        System.out.println("Debug Result : " + demoGlobalConfig.isPreDebug());
        return null;
    }
}
