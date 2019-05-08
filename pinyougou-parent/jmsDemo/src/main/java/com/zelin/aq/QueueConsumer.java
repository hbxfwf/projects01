package com.zelin.aq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;
import java.io.IOException;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:点对点消息的消费方
 * @Date: Create in 2019/5/8 09:18
 */
public class QueueConsumer {
    public static void main(String[] args) throws JMSException, IOException {
        //1.创建连接工厂对象
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.4:61616");
        //2.得到连接对象
        Connection connection = connectionFactory.createConnection();
        //2.1）开启连接
        connection.start();
        //3.通过连接对象得到session对象
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //4.创建目标对象
        Destination destination = session.createQueue("test-queue");
        //5.创建消费者对象
        MessageConsumer consumer = session.createConsumer(destination);
        //6.接受消息
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    //6.1)得到发过来的消息
                    TextMessage textMessage = (TextMessage) message;
                    //6.2)取得消息内容并打印
                    System.out.println(textMessage.getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        //让程序不断处于运行状态，才能够监听到对方发来的消息
        System.in.read();
        //关闭相关的资源
        consumer.close();
        session.close();
        connection.close();

    }
}
