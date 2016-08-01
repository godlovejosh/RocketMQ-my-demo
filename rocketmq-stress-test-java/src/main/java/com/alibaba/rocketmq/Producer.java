package com.alibaba.rocketmq;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

/**
 * Created by wuxing on 16/7/25.
 */
public class Producer extends AbstractJavaSamplerClient {

    private final static DefaultMQProducer producer = new DefaultMQProducer("Producer");
    private static String msgBody;
    private static int count = 0;

    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("msgKey", "");
        return params;
    }

    //每个线程测试前执行一次，做一些初始化工作；
    public void setupTest(JavaSamplerContext arg0) {
        buildMsgBody(1024);
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {

        SampleResult sr = new SampleResult();
        sr.setSampleLabel( "java request");
        String msgKey = javaSamplerContext.getParameter("msgKey");

        if (count == 0) {
            synchronized (javaSamplerContext) {
                String namesrvAddr = "127.0.0.1:9876";
                producer.setNamesrvAddr(namesrvAddr);
                try {
                    producer.start();
                } catch (MQClientException e) {
                    e.printStackTrace();
                }
                count++;
            }
        }

        try {
            sr.sampleStart();// jmeter 开始统计响应时间标记
            String resultData = String.valueOf(testStress01(msgKey));
            System.out.println(resultData);
            if (resultData != null && resultData.length() > 0) {
                sr.setResponseData("结果是："+resultData, null);
                sr.setDataType(SampleResult.TEXT);
            }
            sr.setSuccessful(true);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            sr.setSuccessful(false);
            e.printStackTrace();
        } finally {
            sr.sampleEnd();// jmeter 结束统计响应时间标记
        }

        return sr;
    }

    private static SendResult testStress01(String msgKey) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        Message msg = buildMessage(msgKey);

        SendResult sendResult = producer.send(msg);
        LocalTransactionExecuter tranExecuter = new LocalTransactionExecuter() {

            @Override
            public LocalTransactionState executeLocalTransactionBranch(Message msg, Object arg) {
                return null;
            }
        };

        return sendResult;
    }

    private static Message buildMessage(String msgKey) {
        String topic = "TopicTest";
        String tag = "tagA";
        Message message = new Message(topic,
                tag,
                msgKey,
                (msgBody.getBytes())
        );
        return message;
    }

    private static void buildMsgBody(Integer messageSize) {
        String base = "abcdefghijklmnopqrstuvwxyz";

        if (base.length() > messageSize) {
            msgBody = base.substring(0, messageSize);
        } else {
            int x = messageSize / base.length();
            int m = messageSize % base.length();
            for (int i = 0; i < x; i++) {
                msgBody += base;
            }
            msgBody += base.substring(0, m);
        }
    }
}
