package cn.itsource.aigou.service;

import cn.itsource.aigou.ProductApplication;
import cn.itsource.aigou.domain.ProductType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = ProductApplication.class)
@RunWith(SpringRunner.class)
public class ProductTypeServiceTest {

    @Autowired
    private IProductTypeService productTypeService;

    @Test
    public void loadTreeData() {
        List<ProductType> productTypes = productTypeService.loadTreeData();
        for (ProductType productType : productTypes) {
            System.out.println(productType);
        }
    }
}