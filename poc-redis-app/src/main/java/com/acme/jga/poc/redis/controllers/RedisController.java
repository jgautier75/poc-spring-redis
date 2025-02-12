package com.acme.jga.poc.redis.controllers;

import com.acme.jga.poc.redis.dto.RedisValueCreateDto;
import com.acme.jga.poc.redis.dto.RedisValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
public class RedisController {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @PostMapping(value = "/api/v1/redis")
    public void create(@RequestBody RedisValueCreateDto redisValueCreateDto) {
        redisTemplate.opsForValue().set(redisValueCreateDto.getKey(), redisValueCreateDto.getValue(), Duration.of(redisValueCreateDto.getTimeoutMs(), ChronoUnit.MILLIS));
    }

    @GetMapping(value = "/api/v1/redis")
    public RedisValueDto get(@RequestParam(name = "key") String key) {
        String val = redisTemplate.opsForValue().get(key);
        return new RedisValueDto(val);
    }

    @DeleteMapping(value = "/api/v1/redis")
    public void delete(@RequestParam(name = "key") String key){
        redisTemplate.delete(List.of(key));
    }

}
