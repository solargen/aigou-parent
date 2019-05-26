package cn.itsource.aigou.controller;

import cn.itsource.aigou.service.IProductService;
import cn.itsource.aigou.service.IProductTypeService;
import cn.itsource.aigou.domain.ProductType;
import cn.itsource.aigou.query.ProductTypeQuery;
import cn.itsource.aigou.util.AjaxResult;
import cn.itsource.aigou.util.PageList;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ProductTypeController {
    @Autowired
    public IProductTypeService productTypeService;
    @Autowired
    private IProductService productService;

    /**
    * 保存和修改公用的
    * @param productType  传递的实体
    * @return Ajaxresult转换结果
    */
    @RequestMapping(value="/productType",method= RequestMethod.POST)
    public AjaxResult save(@RequestBody ProductType productType){
        try {
            if(productType.getId()!=null){
                productTypeService.updateById(productType);
            }else{
                productTypeService.save(productType);
            }
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setMessage("保存对象失败！"+e.getMessage());
        }
    }

    /**
    * 删除对象信息
    * @param id
    * @return
    */
    @RequestMapping(value="/productType/{id}",method=RequestMethod.DELETE)
    public AjaxResult delete(@PathVariable("id") Long id){
        try {
            productTypeService.removeById(id);
            return AjaxResult.me();
        } catch (Exception e) {
        e.printStackTrace();
            return AjaxResult.me().setMessage("删除对象失败！"+e.getMessage());
        }
    }

    //获取
    @RequestMapping(value = "/productType/{id}",method = RequestMethod.GET)
    public ProductType get(@PathVariable("id") Long id)
    {
        return productTypeService.getById(id);
    }


    /**
    * 查看所有信息
    * @return
    */
    @RequestMapping(value = "/productType/list",method = RequestMethod.GET)
    public List<ProductType> list(){
        return productTypeService.list();
    }


    /**
    * 分页查询数据
    *
    * @param query 查询对象
    * @return PageList 分页对象
    */
    @RequestMapping(value = "/productType/page",method = RequestMethod.POST)
    public PageList<ProductType> json(@RequestBody ProductTypeQuery query)
    {
        IPage<ProductType> productTypeIPage = productTypeService.page(new Page<>(query.getPage(), query.getSize()));
        return new PageList<>(productTypeIPage.getTotal(),productTypeIPage.getRecords());
    }

    /**
     * 加载类型树
     * @return
     */
    @RequestMapping(value = "/productType/tree",method = RequestMethod.GET)
    public List<ProductType> loadTreeData(){
        return productTypeService.loadTreeData();
    }


    /**
     * 静态化首页
     * @return
     */
    @GetMapping("/page")
    public AjaxResult generateStaticPage(){
        try {
            productTypeService.generateStaticPage();
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMessage("生成失败!");
        }
    }

    /**
     * 加载类型面包屑
     * @param productTypeId
     * @return
     */
    @GetMapping("/productType/crumb")
    public List<Map<String,Object>> loadCrumbs(Long productTypeId){
        return productService.loadCrumbs(productTypeId);
    }









    @GetMapping("/productType/path")
    public String path(@RequestParam("id")Long id){
        return productTypeService.getPathById(id);
    }


}
