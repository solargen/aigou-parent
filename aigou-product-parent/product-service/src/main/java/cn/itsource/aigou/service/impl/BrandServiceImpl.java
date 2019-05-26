package cn.itsource.aigou.service.impl;

import cn.itsource.aigou.domain.Brand;
import cn.itsource.aigou.mapper.BrandMapper;
import cn.itsource.aigou.query.BrandQuery;
import cn.itsource.aigou.service.IBrandService;
import cn.itsource.aigou.util.PageList;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * <p>
 * 品牌信息 服务实现类
 * </p>
 *
 * @author solargen
 * @since 2019-05-16
 */
@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements IBrandService {

    @Override
    public PageList<Brand> getByQuery(BrandQuery query) {
        Page<Brand> page = new Page<>(query.getPage(),query.getSize());
        //核心代码 mybatis-plus的分页
        IPage<Brand> iPage = baseMapper.selectByQuery(page,query);
        //从IPage中提取分页结果封装到自己的PageList中
        return new PageList<Brand>(iPage.getTotal(),iPage.getRecords());
        /*
        Page<Brand> page = new Page<Brand>(query.getPage(),query.getSize());
        QueryWrapper<Brand> wrapper = new QueryWrapper<>();
        wrapper.like("b.name",query.getKeyword()).or().like("englishName",query.getKeyword());
        IPage<Brand> iPage = baseMapper.selectByWrapper(page, wrapper);
        return new PageList<Brand>(iPage.getTotal(),iPage.getRecords());
        */

    }

    /**
     * 根据类型编号查询所有品牌
     * @param productTypeId
     * @return
     */
    @Override
    public Map<String,Object> loadByPrductTypeId(Long productTypeId) {
        Map<String,Object> map = new HashMap<>();
        List<Brand> brands = baseMapper.selectList(new QueryWrapper<Brand>().eq("product_type_id", productTypeId));
        map.put("brands",brands);
        //首字母,去重，排序
        TreeSet<String> letters = new TreeSet<>();
        for (Brand brand : brands) {
            letters.add(brand.getFirstLetter());
        }
        map.put("letters",letters);
        return map;
    }

}
