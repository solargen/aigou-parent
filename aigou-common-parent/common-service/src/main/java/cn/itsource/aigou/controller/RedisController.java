package cn.itsource.aigou.controller;

import cn.itsource.aigou.util.RedisUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author solargen
 * @version V1.0
 * @className RedisController
 * @description TODO
 * @date 2019/5/17
 */
@RestController
public class RedisController {

    //ribbon调用使用restTemplate   feign调用需要feign的接口

    /**
     * 添加数据到redis中
     * @param key
     * @param value
     */
    @PostMapping("/redis")
    public void set(String key,String value){
        RedisUtils.INSTANCE.set(key,value);
    }

    /**
     * 根据key获取value
     * @param key
     * @return
     */
    @GetMapping("/redis")
    public String get(String key){
        return RedisUtils.INSTANCE.get(key);
    }

}
