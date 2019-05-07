<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Freemarker模板的基本用法</title>
</head>
<body>
    <#include 'head.ftl'>
    <#--1.定义一个基本的变量-->
    <#assign linkman="李先生">
    <#--2.定义一个复杂的变量(对象类型)-->
    <#assign info={'telephone':'13690011111','addr':'上海浦东'}>
    你好,${username}!
    <hr>
    联系人：${linkman}<br>
    电话：${info.telephone}<br>
    住址：${info.addr}<br>
    <#--3.条件判断用法-->
    <#if success==true>
        成功
        <#else >
        失败
    </#if>
    <br>
    <#--4.遍历-->
    <#list fruitList as fruit>
      <li>${fruit.name} | ${fruit.price}</li>
    </#list>
    <#--5.调用对象的方法-->
    总共水果数量：${fruitList?size} <br>
    <#--6.转换json串为对象-->
    <#--6.1)定义一个json串对象-->
    <#assign stud="{'sname':'张三','addr':'上海'}">
    <#--6.2）转换json串对象为json对象-->
    <#assign stud1=stud?eval>
    <#--6.3)打印转换后的结果-->
    姓名：${stud1.sname} <br>
    住址：${stud1.addr} <br>
    <#--7.显示时间和日期-->
    今天日期：${today?date} <br>
    当前时间：${today?time} <br>
    日期时间：${today?datetime} <br>
    定制格式：${today?string('yyyy年MM月dd日 E')} <br>
    <#--8.调整显示数字格式,默认从右向左三位一分隔-->
    数字格式调整：${point?c} <br>
    <#--9.空字符串处理-->
    <#if aa??>
        上午好
        <#else >
        下午好
    </#if>
    <br>
    <#--10.为不存在的变量赋默认值,如果此变量没定义，就使用!后的值，否则，就使用原来的值-->
    ${bb!'hello'}

</body>
</html>