package cn.itsource.aigou.repository;

import cn.itsource.aigou.domain.ProductDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author solargen
 * @version V1.0
 * @className ProductRepository
 * @description TODO
 * @date 2019/5/23
 */
public interface ProductDocRepository extends ElasticsearchRepository<ProductDoc,Long>{
}
