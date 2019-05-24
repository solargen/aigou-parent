package cn.itsource.aigou.service.impl;

import cn.itsource.aigou.client.RedisClient;
import cn.itsource.aigou.client.TemplateClient;
import cn.itsource.aigou.domain.ProductType;
import cn.itsource.aigou.mapper.ProductTypeMapper;
import cn.itsource.aigou.service.IProductTypeService;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品目录 服务实现类
 * </p>
 *
 * 项目中遇到的问题：
 * 后台管理中树形结构展示所有的商品类型，展示无限级别的类型
 * 问题：我们使用的是递归，写一个方法，根据父id查询所有的自己类型
 *      再对所有的子类型遍历，针对于每个子类型再次调用这个方法查询子类型的子类型
 *      知道没有子类型位置跳出递归，这种方式每次都查询数据库，效率很低，资源消耗大，
 *      增加了数据库的压力，用户的体验很不好
 *
 *  解决：
 *  使用循环代替递归，一次性查询出所有的类型，开始遍历，如果是一级类型，直接封装到返回的list中
 *  如果是子类型，添加到对应父类型的children中。由于只查询一次数据库，降低了数据库的压力，并且提高了
 *  效率，增强了用户的体验。
 *
 * @author solargen
 * @since 2019-05-16
 */
@Service
public class ProductTypeServiceImpl extends ServiceImpl<ProductTypeMapper, ProductType> implements IProductTypeService {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private TemplateClient templateClient;


    /**
     * 生成静态化首页
     */
    @Override
    public void generateStaticPage() {

        //模板   数据   目标文件的路径
        //路径问题最好不要硬编码，可以写到属性文件或者配置文件中，再读入

        //根据product.type.vm模板生成 product.type.vm.html
        String templatePath = "D:\\IDEA\\workspace\\aigou\\aigou-parent\\aigou-product-parent\\product-service\\src\\main\\resources\\template\\product.type.vm";
        String targetPath = "D:\\IDEA\\workspace\\aigou\\aigou-parent\\aigou-product-parent\\product-service\\src\\main\\resources\\template\\product.type.vm.html";
        List<ProductType> productTypes = loadDataTree();
        Map<String,Object> params = new HashMap<>();
        params.put("model",productTypes);
        params.put("templatePath",templatePath);
        params.put("targetPath",targetPath);
        templateClient.createStaticPage(params);

        //再根据home.vm生成home.html
        templatePath = "D:\\IDEA\\workspace\\aigou\\aigou-parent\\aigou-product-parent\\product-service\\src\\main\\resources\\template\\home.vm";
        targetPath = "D:\\IDEA\\workspace\\aigou\\aigou-web-parent\\ecommerce\\home.html";
        params = new HashMap<>();

        Map<String,Object> model = new HashMap<>();
        model.put("staticRoot","D:\\IDEA\\workspace\\aigou\\aigou-parent\\aigou-product-parent\\product-service\\src\\main\\resources\\");
        params.put("model",model);
        params.put("templatePath",templatePath);
        params.put("targetPath",targetPath);
        templateClient.createStaticPage(params);

    }

    @Override
    public String getPathById(Long id) {
        ProductType productType = baseMapper.selectById(id);
        return productType.getPath();
    }


    /**
     * 加载类型树
     * （1）递归   看有没有children，有则继续查
     * （2）循环，一次性查询所有的类型，循环添加到children中
     * @return
     */
    @Override
    public List<ProductType> loadTreeData() {
        //先从redis中获取
        String productTypesStr = redisClient.get("productTypes");
        if(StringUtils.isEmpty(productTypesStr)){

            System.out.println("111111111111111111111111111111111111111111111");

            //从数据库中查询
            //使用循环
            List<ProductType> productTypes = loadDataTree();
            //存到redis中
            String jsonString = JSONArray.toJSONString(productTypes);
            redisClient.set("productTypes",jsonString);
            //返回
            return productTypes;
        }
        //转换成List
        List<ProductType> productTypes = JSONArray.parseArray(productTypesStr, ProductType.class);
        return productTypes;
    }


    /**
     * 递归：
     * （1）自己调用自己
     * （2）要有出口
     *
     * 查自己的子
     *
     * @return
     */
    private List<ProductType> loadDataTree(Long pid){
        //根据父id查询子类型
        List<ProductType> children = baseMapper.selectList(new QueryWrapper<ProductType>().eq("pid", pid));
        //递归的出口
        if(children==null||children.size()==0){
            return null;
        }
        for (ProductType productType : children) {
            //对子进行循环，调用自己继续查询子的子类型
            List<ProductType> c = loadDataTree(productType.getId());
            //将所有的孙子类型放入到子的children属性中
            productType.setChildren(c);
        }
        return children;
    }


    /**
     * 循环
     * @return
     */
    private List<ProductType> loadDataTree(){

        //查询所有
        List<ProductType> productTypes = baseMapper.selectList(null);
        //放到Map中--大大的降低循环次数
        Map<Long,ProductType> map = new HashMap<>();
        for (ProductType productType : productTypes) {
            map.put(productType.getId(),productType);
        }

        //放所有的一级类型
        List<ProductType> list = new ArrayList<>();
        //子找父
        for (ProductType productType : productTypes) {
            //如果pid为0则自己就是父
            if(productType.getPid()==0){
                list.add(productType);
            }else{
                List<ProductType> children = map.get(productType.getPid()).getChildren();
                children.add(productType);
            }
        }
        return list;

    }

    //=====================重写增删改，同步redis缓存，同步生成静态页面==================================
    @Override
    public boolean save(ProductType entity) {
        //先执行保存
        boolean result = super.save(entity);
        sychornizedOperate();
        return result;
    }

    @Override
    public boolean removeById(Serializable id) {
        boolean result = super.removeById(id);
        sychornizedOperate();
        return result;
    }

    @Override
    public boolean updateById(ProductType entity) {
        boolean result = super.updateById(entity);
        sychornizedOperate();
        return result;
    }


    private void updateRedis(){
        List<ProductType> productTypes = loadDataTree();
        //转成json字符串缓存到redis中
        String jsonString = JSONArray.toJSONString(productTypes);
        redisClient.set("productTypes",jsonString);
    }

    //=====================结束==================================

    /**
     * 只要是增删改操作，都要同步去做两件事情
     * （1）同步redis缓存
     * （2）同步生成静态的主页面
     */
    private void sychornizedOperate(){
        updateRedis();
        generateStaticPage();
    }

}
