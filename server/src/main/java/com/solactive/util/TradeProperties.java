package com.solactive.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "trade")
public class TradeProperties {

    private int monitorTimeInMillis = 60000;
}
