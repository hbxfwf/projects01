package com.zelin.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/29 11:03
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext*.xml")
public class TestRedisTemplate {
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 测试redis中字符串类型的使用
     */
    @Test
    public void testString(){
        //1.将字符串类型的数据保存到redis中
        redisTemplate.boundValueOps("name").set("aaaa");
        redisTemplate.boundValueOps("age").set("20");
        //2.从字符串中取出值
        String name = (String) redisTemplate.boundValueOps("name").get();
        System.out.println("name = "  + name);
        //3.从字符串删除值(将redis中存放的key为name的字符串删除)
        redisTemplate.delete("name");
    }

    /**
     *测试Set类型的数据
     */
    @Test
    public void testSet(){
        //1.向set中添加数据
//        redisTemplate.boundSetOps("namesets" ).add("张三");
//        redisTemplate.boundSetOps("namesets" ).add("李四","王五");
        //2.从set中取出数据
//        Set names = redisTemplate.boundSetOps("namesets").members();
//        System.out.println("namesets = " + names);
        //3.从set中删除一个值
        redisTemplate.boundSetOps("namesets").remove("王五");
        Set names = redisTemplate.boundSetOps("namesets").members();
        System.out.println("namesets = " + names);
        //4.删除多个值,当然，也可以使用上面的方法来删除多个值
        redisTemplate.delete("namesets");
    }

    /**
     * 测试List集合
     */
    @Test
    public void testList(){
        //右入栈
        //rightStack();
        //左入栈
        //leftStack();
        //删除指定个数的元素
        deleteList();
    }
    @Test
    public void testHash(){
        //1.向hash结构中添加数据
        redisTemplate.boundHashOps("hashs1").put("name","张三");
        redisTemplate.boundHashOps("hashs1").put("age","20");
        redisTemplate.boundHashOps("hashs1").put("addr","上海");
        //2.从hash结构中取出数据
        //2.1)取出某个小key的值
        String name = (String) redisTemplate.boundHashOps("hashs1").get("name");
        //2.2)取出指定大key下的所有小key的集合
        Set keys = redisTemplate.boundHashOps("hashs1").keys();
        //2.3)取出指定大key下的所有的值的集合
        List values = redisTemplate.boundHashOps("hashs1").values();
        System.out.println("name = " + name);
        System.out.println("keys = " + keys);
        System.out.println("values = " + values);

    }

    //删除List中指定元素
    private void deleteList() {
        redisTemplate.boundListOps("lists1").remove(1,"小明");
        //查看删除后的结果
        List list1 = redisTemplate.boundListOps("lists1").range(0, 10);
        System.out.println("lists = "  + list1);
    }

    //左入栈
    private void leftStack() {
        //1. 一次入栈一个
        redisTemplate.boundListOps("lists1").leftPush("小明");
        //2.一次入栈多个
        redisTemplate.boundListOps("lists1").leftPushAll("小张","小李");
        //3.查看右入栈的结果(使用范围查看)
        List list1 = redisTemplate.boundListOps("lists1").range(0, 10);
        System.out.println("lists = "  + list1);
    }

    //右入栈
    private void rightStack() {
        //1.右入栈一个
        redisTemplate.boundListOps("lists").rightPush("曹操");
        //2.右入栈多个
        redisTemplate.boundListOps("lists").rightPushAll("张飞","关羽","刘备");
        //3.查看右入栈的结果(使用范围查看)
        List list1 = redisTemplate.boundListOps("lists").range(0, 10);
        System.out.println("lists = "  + list1);
    }
}
