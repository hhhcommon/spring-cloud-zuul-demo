package com.edu.zuul.config;

import com.edu.zuul.filters.ErrorFilter;
import com.edu.zuul.filters.PostFilter;
import com.edu.zuul.filters.PreFilter;
import com.edu.zuul.filters.RouteFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringCloudZuulConfig {
    @Bean
    public PreFilter preFilter() {
        return new PreFilter();
    }

    @Bean
    public PostFilter postFilter() {
        return new PostFilter();
    }

    @Bean
    public ErrorFilter errorFilter() {
        return new ErrorFilter();
    }

    @Bean
    public RouteFilter routeFilter() {
        return new RouteFilter();
    }
}
