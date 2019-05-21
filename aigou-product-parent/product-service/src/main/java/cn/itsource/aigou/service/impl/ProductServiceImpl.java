package cn.itsource.aigou.service.impl;

import cn.itsource.aigou.domain.Product;
import cn.itsource.aigou.domain.ProductExt;
import cn.itsource.aigou.domain.Specification;
import cn.itsource.aigou.mapper.ProductExtMapper;
import cn.itsource.aigou.mapper.ProductMapper;
import cn.itsource.aigou.mapper.SpecificationMapper;
import cn.itsource.aigou.query.ProductQuery;
import cn.itsource.aigou.service.IProductService;
import cn.itsource.aigou.util.PageList;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 商品 服务实现类
 * </p>
 *
 * @author solargen
 * @since 2019-05-20
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Autowired
    private ProductExtMapper productExtMapper;
    @Autowired
    private SpecificationMapper specificationMapper;

    @Override
    public PageList<Product> getByQuery(ProductQuery query) {
        Page<Product> page = new Page<>(query.getPage(),query.getSize());
        IPage<Product> iPage = baseMapper.selectByQuery(page,query);
        //封装PageList
        return new PageList<Product>(iPage.getTotal(),iPage.getRecords());
    }

    /**
     * 获取商品的显示属性
     * @param productId
     * @return
     */
    @Override
    public List<Specification> getViewProperties(Long productId) {
        //去哪里获取？
        //先从商品表中获取 ---  修改
        Product product = baseMapper.selectOne(new QueryWrapper<Product>().eq("id", productId));
        String viewProperties = product.getViewProperties();
        //如果商品表中没有，则表示该商品还没有维护过显示属性  --- 新增
        if(StringUtils.isEmpty(viewProperties)){
            Long productTypeId = product.getProductType();
            //应该从属性表中查询出该商品类型的显示属性给前端
            List<Specification> specifications = specificationMapper.selectList(new QueryWrapper<Specification>().eq("productTypeId", productTypeId).eq("isSku", 0));
            return specifications;
        }
        List<Specification> specifications = JSONArray.parseArray(viewProperties, Specification.class);
        return specifications;
    }

    @Override
    public void saveViewProperties(List<Specification> specifications,Long productId) {
        //先根据商品id查询出商品
        Product product = baseMapper.selectById(productId);
        //修改domain的viewProperties属性
        //1 自定义要保存的属性
        //2 直接放入Specification
        // SimplePropertyPreFilter第二个参数：传入需要序列化属性的名称
        //SimplePropertyPreFilter filter = new SimplePropertyPreFilter(Specification.class, "id","specName","value");
        String jsonString = JSONArray.toJSONString(specifications);
        product.setViewProperties(jsonString);
        //执行update方法
        baseMapper.updateById(product);
    }


    @Override
    @Transactional
    public boolean save(Product entity) {
        try {
            super.save(entity);//保存product   是否获取id -- mybatisplus默认是自动获取id的
            //保存商品详情
            ProductExt productExt = new ProductExt();
            productExt.setDescription(entity.getDescription());
            productExt.setRichContent(entity.getContent());
            productExt.setProductId(entity.getId());
            productExtMapper.insert(productExt);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
