package cn.itsource.aigou.service.impl;

import cn.itsource.aigou.client.ElasticSearchClient;
import cn.itsource.aigou.domain.*;
import cn.itsource.aigou.mapper.*;
import cn.itsource.aigou.query.ProductQuery;
import cn.itsource.aigou.service.IProductService;
import cn.itsource.aigou.util.PageList;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

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
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private ElasticSearchClient elasticSearchClient;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private ProductTypeMapper productTypeMapper;

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

    /**
     * 获取SKU属性和属性值
     * @param productId
     * @return
     */
    @Override
    public List<Specification> getSkuProperties(Long productId) {
        //先从product中查询
        Product product = baseMapper.selectById(productId);
        String skuProperties = product.getSkuProperties();
        if(StringUtils.isEmpty(skuProperties)){
            //再查询属性表
            Long productTypeId = product.getProductType();
            List<Specification> specifications = specificationMapper.selectList(new QueryWrapper<Specification>().eq("productTypeId", productTypeId).eq("isSku", 1));
            return specifications;
        }
        return JSONArray.parseArray(skuProperties,Specification.class);
    }

    /**
     * 保存sku属性
     * @param specifications
     * @param productId
     */
    @Override
    public void saveSkuProperties(List<Specification> specifications, Long productId,List<Map<String,String>> skus) {
        //先根据商品id查询出商品
        Product product = baseMapper.selectById(productId);
        String jsonString = JSONArray.toJSONString(specifications);
        product.setSkuProperties(jsonString);
        baseMapper.updateById(product);
        //添加或者修改sku表
        //先删除该商品之前的sku
        skuMapper.delete(new QueryWrapper<Sku>().eq("productId",productId));
        //再重新添加
        List<Sku> skuList = new ArrayList<>();
        for (Map<String, String> skuMap : skus) {
            Sku sku = new Sku();
            sku.setProductId(productId);
            sku.setAvailableStock(Integer.parseInt(skuMap.get("availableStock")));
            sku.setCreateTime(new Date().getTime());
            sku.setPrice(Integer.parseInt(skuMap.get("price")));
            sku.setSkuIndex(skuMap.get("sku_index"));

            //获取除了sku_index price,availableStock之外的所有属性
            //name    sku_properties
            String name = "";
            Map<String,String> sku_properties = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : skuMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                //排除sku_index,price,availableStock
                if(key.equals("price")||key.equals("sku_index")||key.equals("availableStock")){
                    continue;
                }
                name += value;
                sku_properties.put(key,value);
            }
            sku.setSkuName(name);
            sku.setSkuProperties(JSONObject.toJSONString(sku_properties));
            //保存
            skuMapper.insert(sku);
        }

    }

    /**
     * 商品上架
     * @param ids
     */
    @Override
    public void onSale(List<Long> ids) {
        //修改上架时间和状态，保存到数据库
        baseMapper.onSale(ids,new Date().getTime());
        //查询数据库
        List<Product> products = baseMapper.selectBatchIds(ids);
        //保存到es中
        //将List<Product> 转成List<ProductDoc>
        List<ProductDoc> productDocList = products2productDocs(products);
        elasticSearchClient.saveBatch(productDocList);
    }

    /**
     * 类型转换 List<Product> 转成List<ProductDoc>
     * @param products
     * @return
     */
    private List<ProductDoc> products2productDocs(List<Product> products) {
        List<ProductDoc> productDocList = new ArrayList<>();
        for (Product product : products) {
            ProductDoc productDoc = product2ProductDoc(product);
            productDocList.add(productDoc);
        }
        return productDocList;
    }

    /**
     * 类型转换 Product转ProductDoc
     * @param product
     * @return
     */
    private ProductDoc product2ProductDoc(Product product) {
        ProductDoc productDoc = new ProductDoc();
        productDoc.setId(product.getId());
        productDoc.setProductTypeId(product.getProductType());
        productDoc.setBrandId(product.getBrandId());
        productDoc.setSaleCount(product.getSaleCount());
        productDoc.setOnSaleTime(product.getOnSaleTime());
        productDoc.setCommontCount(product.getCommentCount());
        productDoc.setViewCount(product.getViewCount());
        productDoc.setName(product.getName());
        productDoc.setSubName(product.getSubName());
        productDoc.setViewProperties(product.getViewProperties());
        productDoc.setSkuProperties(product.getSkuProperties());
        productDoc.setMedias(product.getMedias());

        //all字段
        Brand brand = brandMapper.selectById(product.getBrandId());
        String brandName = brand.getName();
        ProductType productType = productTypeMapper.selectById(product.getProductType());
        String productTypeName = productType.getName();
        String all = product.getName()+" "+product.getSubName()+" "+brandName+" "+productTypeName;
        productDoc.setAll(all);// 标题  副标题  品牌名称  类型名称 中间以空格拼接

        //最大价格和最小价格
        List<Sku> skus = skuMapper.selectList(new QueryWrapper<Sku>().eq("productId", product.getId()));
        Integer maxPrice = skus.get(0).getPrice();
        Integer minPrice = skus.get(0).getPrice();
        for (Sku sku : skus) {
            if(sku.getPrice()>maxPrice) maxPrice = sku.getPrice();
            if(sku.getPrice()<minPrice) minPrice = sku.getPrice();
        }
        productDoc.setMinPrice(minPrice);//每个sku都有对应的价格，从所有的价格中取到最小价格
        productDoc.setMaxPrice(maxPrice);//从所有的sku中取到最大价格

        return productDoc;
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
