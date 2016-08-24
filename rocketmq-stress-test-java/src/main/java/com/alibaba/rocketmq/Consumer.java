package com.alibaba.rocketmq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import java.io.*;
import java.util.List;

public class Consumer {

    public static void main(String[] args) throws InterruptedException, MQClientException {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("PushConsumer");
        String namesrvAddr = "114.55.108.114:9876";
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe("TopicTest", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                    ConsumeConcurrentlyContext context) {

                System.out.println(Thread.currentThread().getName() + " Receive New Messages: " + msgs);

                String bodyString = "";
                for(MessageExt message : msgs) {
                    byte[] body = message.getBody();
                    try {
                        bodyString = new String(body, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                String filePath = "/Users/josh/RocketMQ/";
                String fileName = "rocketMQ";
                fileName = filePath + fileName + msgs.hashCode();

                File file = new File(fileName);
                if(!file.exists()) {
                    try {
                        file.createNewFile();
                        Writer out = new FileWriter(file, true);
                        out.write(bodyString);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();

        System.out.println("Consumer Started.");
    }
}
