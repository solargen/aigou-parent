package cn.itsource.aigou.util;

/**
 * @author solargen
 * @version V1.0
 * @className NameUtils
 * @description TODO
 * @date 2019/5/17
 */
public class NameUtils {


    public static void main(String[] args) {
        String random = "";
        String[] strName = new String[]{"陈波", "陈中方", "樊俊鉴", "李林", "李雪松", "彭聪", "苏鸿翔", "田鑫", "王光彪", "易浩", "张斌", "张涛", "章秩", "黄海南", "蒋从洲", "王小刚", "邹鹏", "姚浩"};
        int index = (int)(Math.random() * (double)strName.length);
        System.out.println(strName[index]);
    }


}
