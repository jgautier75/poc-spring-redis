package com.acme.jga.poc.redis.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClusterNode {
    private String name;
    private String host;
    private int port;
}
