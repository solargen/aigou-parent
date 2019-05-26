package cn.itsource.aigou.controller;

import cn.itsource.aigou.domain.ProductDoc;
import cn.itsource.aigou.repository.ProductDocRepository;
import cn.itsource.aigou.util.AjaxResult;
import cn.itsource.aigou.util.PageList;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author solargen
 * @version V1.0
 * @className ProductEsController
 * @description TODO
 * @date 2019/5/23
 */
@RestController
public class ProductEsController {

    @Autowired
    private ProductDocRepository repository;

    /**
     * 保存
     * @param productDoc
     * @return
     */
    @PostMapping("/es/save")
    public AjaxResult save(@RequestBody ProductDoc productDoc){
        try {
            repository.save(productDoc);
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMessage("保存失败!"+e.getMessage());
        }
    }

    /**
     * 批量存
     * @param productDocs
     * @return
     */
    @PostMapping("/es/saveBatch")
    public AjaxResult saveBatch(@RequestBody List<ProductDoc> productDocs){
        try {
            repository.saveAll(productDocs);
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMessage("保存失败!"+e.getMessage());
        }
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/es/delete")
    public AjaxResult delete(@RequestParam("id") Long id){
        try {
            repository.deleteById(id);
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMessage("删除失败!"+e.getMessage());
        }
    }

    /**
     * 批量删除
     * @param productDocs
     * @return
     */
    @PostMapping("/es/deleteBatch")
    public AjaxResult deleteBatch(@RequestBody List<ProductDoc> productDocs){
        try {
            repository.deleteAll(productDocs);
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMessage("删除失败!"+e.getMessage());
        }
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @PostMapping("/es/deleteBatchByIds")
    public AjaxResult deleteBatchByIds(@RequestBody List<Long> ids){
        try {
            for (Long id : ids) {
                //如果es中有数据，再删除，否则会抛出异常
                if(repository.existsById(id))
                    repository.deleteById(id);
            }
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMessage("删除失败!"+e.getMessage());
        }
    }

    @PostMapping("/es/list")
    public PageList<ProductDoc> listProduct(@RequestBody Map<String,String> params){
        //获取到请求参数
        //查询的参数：all   productTypeId  brandId  minPrice  maxPrice
        //排序的字段: saleCount  onSaleTime  commontCount  viewCount       列名 sortField = xl,xp,pl,jg,rq   排序规则 sortRule = asc  desc
        //特殊字段    price排序   降序则使用maxPrice    升序minPrice
        //分页字段 page size

        String all = params.get("keyword");
        String productTypeId = params.get("productTypeId");
        String brandId = params.get("brandId");
        Integer page = Integer.parseInt(params.get("page"));
        Integer size = Integer.parseInt(params.get("size"));
        //价格
        String minPrice = params.get("minPrice");
        String maxPrice = params.get("maxPrice");

        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        //查询条件  --  all   match匹配
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(all)){
            boolQueryBuilder.must(new MatchQueryBuilder("all",all));
        }
        //过滤   -- termQuery  productTypeId   brandId
        List<QueryBuilder> filterList = boolQueryBuilder.filter();
       //productTypeId  类型过滤
        if(StringUtils.isNotEmpty(productTypeId)){
            filterList.add(new TermQueryBuilder("productTypeId",productTypeId));
        }
        //brandId 品牌过滤
        if(StringUtils.isNotEmpty(brandId)){
            filterList.add(new TermQueryBuilder("brandId",brandId));
        }
        //最小价格和最大价格过滤
        if(StringUtils.isNotEmpty(minPrice)){
            filterList.add(new RangeQueryBuilder("maxPrice").gte(Integer.parseInt(minPrice)));
        }
        if(StringUtils.isNotEmpty(maxPrice)){
            filterList.add(new RangeQueryBuilder("minPrice").lte(Integer.parseInt(maxPrice)));
        }
        builder.withQuery(boolQueryBuilder);

        //排序   --  saleCount  onSaleTime  commontCount  viewCount   price
        String sortField = params.get("sortField");// xl,xp,pl,jg,rq
        String order = "saleCount";//默认按照销量
        SortOrder sortOrder = SortOrder.DESC;//排序

        //排序规则：
        String sortRule = params.get("sortRule");
        if("asc".equals(sortRule)){
            sortOrder = SortOrder.ASC;
        }

        if("xp".equals(sortField)){
            order = "onSaleTime";
        }
        if("pl".equals(sortField)){
            order = "commontCount";
        }
        if("rq".equals(sortField)){
            order = "viewCount";
        }
        //价格排序
        if("jg".equals(sortField)){
            if(sortOrder == SortOrder.DESC){
                order = "maxPrice";
            }else{
                order = "minPrice";
            }
        }
        builder.withSort(new FieldSortBuilder(order).order(sortOrder));

        //分页   --  page size  es中页数从0开始
        builder.withPageable(PageRequest.of(page-1,size));
        Page<ProductDoc> productDocPage = repository.search(builder.build());
        return new PageList<>(productDocPage.getTotalElements(),productDocPage.getContent());
    }
}
