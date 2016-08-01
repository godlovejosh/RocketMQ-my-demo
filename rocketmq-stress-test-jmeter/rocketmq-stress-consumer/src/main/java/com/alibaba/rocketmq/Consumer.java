package com.alibaba.rocketmq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;

public class Consumer {

    /** nameServerIP地址和端口号 */
    private static String nameServer = "127.0.0.1:9876";

    /** 消费者分组 */
    private static String producerGroup = "producer0729";

    /** 消费TOPIC */
    private static String topic = "topic0729";

    /** 消费TOPIC下的tag */
    private static String tag = "defaultTag";

    /** 是否保存到数据库 */
    private static int saveActive = 0; // 0表示不保存,1表示保存

    public static void main(String[] args) throws InterruptedException, MQClientException {

        final int flag = args.length >= 1 ? Integer.parseInt(args[0]) : saveActive;

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(producerGroup);
        consumer.setNamesrvAddr(nameServer);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(topic, tag);
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {

                System.out.println(Thread.currentThread().getName() + " Receive New Messages: " + msgs);

                for(MessageExt message : msgs) {
                    byte[] body = message.getBody();
                    if (flag == 1) {
                        saveMessage(message.getMsgId());
                    }
                    try {
                        String bodyString = new String(body, "UTF-8");
                        System.out.println(bodyString);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();

        System.out.println("Consumer Started.");
    }

    private static void saveMessage(String msgId) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/rocketmq",
                    "admin", "athene.admin");
            if (!conn.isClosed()) {
                System.out.println("数据库连接成功！"); //验证是否连接成功
            }

            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Statement statement = conn.createStatement();
            String sql = "update msg_info set receiveStatus = '"+ 0 +"' where msg_id = '"+ msgId +"'";

            System.out.println(sql);
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
