package cn.itsource.aigou.client;

import cn.itsource.aigou.domain.ProductDoc;
import cn.itsource.aigou.util.AjaxResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("AIGOU-COMMON")
public interface ElasticSearchClient {

    /**
     * 保存
     * @param productDoc
     * @return
     */
    @PostMapping("/es/save")
    AjaxResult save(@RequestBody ProductDoc productDoc);

    /**
     * 批量存
     * @param productDocs
     * @return
     */
    @PostMapping("/es/saveBatch")
    AjaxResult saveBatch(@RequestBody List<ProductDoc> productDocs);

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/es/delete")
    AjaxResult delete(@RequestParam("id") Long id);

    /**
     * 批量删除
     * @param productDocs
     * @return
     */
    @PostMapping("/es/deleteBatch")
    AjaxResult deleteBatch(@RequestBody List<ProductDoc> productDocs);

    /**
     * 批量id删除
     * @param ids
     * @return
     */
    @PostMapping("/es/deleteBatchByIds")
    AjaxResult deleteBatchByIds(@RequestBody List<Long> ids);

}
