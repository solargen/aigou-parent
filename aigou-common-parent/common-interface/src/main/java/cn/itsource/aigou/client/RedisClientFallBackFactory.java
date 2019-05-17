//package cn.itsource.aigou.client;
//
//import feign.hystrix.FallbackFactory;
//import org.springframework.stereotype.Component;
//
///**
// * @author solargen
// * @version V1.0
// * @className RedisClientFallBackFactory
// * @description TODO
// * @date 2019/5/17
// */
//@Component
//public class RedisClientFallBackFactory implements FallbackFactory<RedisClient> {
//
//    public RedisClient create(Throwable throwable) {
//        return new RedisClient() {
//            public void set(String key, String value) {
//                //...........
//            }
//
//            public String get(String key) {
//                return "[]";
//            }
//        };
//    }
//}
