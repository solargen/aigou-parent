package cn.itsource.aigou.service;

import cn.itsource.aigou.domain.Product;
import cn.itsource.aigou.domain.Specification;
import cn.itsource.aigou.query.ProductQuery;
import cn.itsource.aigou.util.PageList;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品 服务类
 * </p>
 *
 * @author solargen
 * @since 2019-05-20
 */
public interface IProductService extends IService<Product> {

    PageList<Product> getByQuery(ProductQuery query);

    List<Specification> getViewProperties(Long productId);

    void saveViewProperties(List<Specification> specifications,Long productId);

    List<Specification> getSkuProperties(Long productId);

    void saveSkuProperties(List<Specification> specifications, Long productId, List<Map<String,String>> skus);

    void onSale(List<Long> ids);
}
