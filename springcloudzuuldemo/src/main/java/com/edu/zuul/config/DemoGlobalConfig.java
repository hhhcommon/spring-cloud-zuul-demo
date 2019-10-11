package com.edu.zuul.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class DemoGlobalConfig {
    @Value("${debug.pre.enable}")
    private boolean preDebug;
}
