package cn.itsource.aigou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author solargen
 * @version V1.0
 * @className ProductApplication
 * @description TODO
 * @date 2019/5/16
 */
@SpringBootApplication
@EnableEurekaClient
@EnableSwagger2 //标识使用swagger生成接口文档
@MapperScan("cn.itsource.aigou.mapper")
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class,args);
    }

}
