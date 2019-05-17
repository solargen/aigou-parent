package cn.itsource.aigou.service;

import cn.itsource.aigou.ProductApplication;
import cn.itsource.aigou.client.RedisClient;
import cn.itsource.aigou.client.TemplateClient;
import cn.itsource.aigou.domain.ProductType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = ProductApplication.class)
@RunWith(SpringRunner.class)
public class ProductTypeServiceTest {

    @Autowired
    private IProductTypeService productTypeService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private TemplateClient templateClient;

    @Test
    public void loadTreeData() {
        List<ProductType> productTypes = productTypeService.loadTreeData();
        for (ProductType productType : productTypes) {
            System.out.println(productType);
        }
    }

    @Test
    public void testRedisSet()throws Exception{
        redisClient.set("username","admin");
    }


    @Test
    public void testRedisGet()throws Exception{
        System.out.println(redisClient.get("username"));
    }




}