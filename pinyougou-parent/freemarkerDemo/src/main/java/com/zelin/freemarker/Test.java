package com.zelin.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/5/7 09:06
 */
public class Test {
    public static void main(String[] args) throws IOException, TemplateException {
        //1.构造一个configuration对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //2.指定configuration对象的参数
        //2.1)设置默认的字符集编码
        configuration.setDefaultEncoding("UTF-8");
        //2.2)指定模板文件的位置
        configuration.setDirectoryForTemplateLoading(new File("D:\\java1301_2019_114\\pinyougou-parent\\freemarkerDemo\\src\\main\\resources"));
        //3.根据配置文件得到模板对象
        Template template = configuration.getTemplate("hello.ftl");
        //4.定义存放数据的Map
        Map dataModel = new HashMap();
        //4.1)添加数据到map中
        dataModel.put("username","张三");
        dataModel.put("success",true);
        Map apple = new HashMap();
        apple.put("name","苹果");
        apple.put("price",10);
        Map orange = new HashMap();
        orange.put("name","桔子");
        orange.put("price",5);
        Map banana = new HashMap();
        banana.put("name","香蕉");
        banana.put("price",4);
        //定义List将上面的三种水果 分别放入
        List<Map> list = new ArrayList<>();
        list.add(apple);
        list.add(orange);
        list.add(banana);
        //将上面的list放到dataModel中
        dataModel.put("fruitList",list);
        //存放日期到数据对象中
        dataModel.put("today",new Date());
        //存放数字
        dataModel.put("point",2456345);
        //5.定义用于输出的文件的输出流
        FileWriter out = new FileWriter("d:/item/test.html");
        //6.执行模板，生成静态页面(其实就是将数据dataModel与模板文件hello.ftl进行整合的过程)
        template.process(dataModel,out);
        //7.关闭流
        out.close();
        System.out.println("输出静态页面成功!");
    }
}
