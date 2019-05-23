package cn.itsource.common;

import cn.itsource.aigou.CommonApplication;
import cn.itsource.aigou.domain.ProductDoc;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author solargen
 * @version V1.0
 * @className ESTest
 * @description TODO
 * @date 2019/5/23
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommonApplication.class)
public class ESTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void test(){
        elasticsearchTemplate.deleteIndex("aigou");
        elasticsearchTemplate.createIndex("aigou");
        elasticsearchTemplate.putMapping(ProductDoc.class);
    }

}
