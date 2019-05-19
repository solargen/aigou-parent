package cn.itsource.aigou.controller;

import cn.itsource.aigou.util.AjaxResult;
import cn.itsource.aigou.util.FastDfsApiOpr;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author solargen
 * @version V1.0
 * @className FileController
 * @description TODO
 * @date 2019/5/19
 */
@RestController
public class FileController {

    /**
     * 文件的上传
     * @param file
     * @return
     */
    @PostMapping("/file/upload")
    public AjaxResult uploadFile(@RequestParam("file") MultipartFile file){
        try {
            //获取到文件后缀
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            //调用方法
            byte[] bytes;
            String fileId = FastDfsApiOpr.upload(file.getBytes(), extension);
            //上传成功
            return AjaxResult.me().setData(fileId);
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMessage("上传失败!");
        }
    }

    /**
     * 删除分布式文件系统中的文件
     * @param fileId
     * @return
     */
    @GetMapping("/file/delete")
    public AjaxResult deleteFile(@RequestParam("fileId") String fileId){
        try {
            //fileId = /group1/M00/00/01/wKgBBVzhdv-ALosSAACNWUaTOwk027.jpg
            //拆分组名和其他
            String tempFile = fileId.substring(1);
            String group = tempFile.substring(0, tempFile.indexOf("/"));
            String name = tempFile.substring(tempFile.indexOf("/")+1);
            FastDfsApiOpr.delete(group,name);
            return AjaxResult.me().setData(fileId);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMessage("删除失败!");
        }

    }




}
