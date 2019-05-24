package cn.itsource.aigou.mapper;

import cn.itsource.aigou.domain.Product;
import cn.itsource.aigou.query.ProductQuery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 商品 Mapper 接口
 * </p>
 *
 * @author solargen
 * @since 2019-05-20
 */
public interface ProductMapper extends BaseMapper<Product> {

    IPage<Product> selectByQuery(Page<Product> page, @Param("query") ProductQuery query);
    //上架 修改上架时间和状态
    void onSale(@Param("ids") List<Long> ids,@Param("onSaleTime") Long onSaleTime);

    void offSale(@Param("ids")List<Long> ids, @Param("offSaleTime")long offSaleTime);
}
