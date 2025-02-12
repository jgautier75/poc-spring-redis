package com.acme.jga.poc.redis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RedisValueCreateDto {
    private String key;
    private String value;
    private long timeoutMs;
}
