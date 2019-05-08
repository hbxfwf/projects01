package com.zelin.aq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description: 点对点消息生产者方
 * @Date: Create in 2019/5/8 09:06
 */
public class QueueProducer {
    public static void main(String[] args) throws JMSException {
        //1.定义连接工厂对象
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.4:61616");
        //2.得到连接对象
        Connection connection = connectionFactory.createConnection();
        //2.1）开启连接
        connection.start();
        //3.创建session对象,参数1：代表是否使用事务，参数2：应答模式，可取值如下：
        /**
         * AUTO_ACKNOWLEDGE = 1         自动确认
         * CLIENT_ACKNOWLEDGE = 2       客户端手动确认   
         * DUPS_OK_ACKNOWLEDGE = 3      自动批量确认
         * SESSION_TRANSACTED = 0       事务提交并确认
         * */
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //4.创建目标对象
        Destination destination = session.createQueue("test-queue");
        //5.创建生产者对象
        MessageProducer producer = session.createProducer(destination);
        //6.定义消息对象的文本
        Message message = session.createTextMessage("这是点对点消息...");
        //7.发送消息
        producer.send(message);
        //8.关闭资源
        producer.close();
        session.close();
        connection.close();
    }
}
