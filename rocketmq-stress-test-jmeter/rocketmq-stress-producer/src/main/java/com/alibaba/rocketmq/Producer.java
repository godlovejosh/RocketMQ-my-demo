package com.alibaba.rocketmq;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
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

    private static DefaultMQProducer producer = new DefaultMQProducer("Producer");

    static {
        String namesrvAddr = "114.55.108.114:9876";
        producer.setNamesrvAddr(namesrvAddr);
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    private static String msgBody = "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdef" +
            "ghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghi" +
            "jabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghija" +
            "bcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcd" +
            "efghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefg" +
            "hijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij" +
            "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcd" +
            "efghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij" +
            "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghi" +
            "jabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcd";

    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("msgKey", "");
        return params;
    }

    //每个线程测试前执行一次，做一些初始化工作；
    public void setupTest(JavaSamplerContext arg0) {
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {

        SampleResult sr = new SampleResult();
        sr.setSampleLabel( "java request");
        String msgKey = javaSamplerContext.getParameter("msgKey");

        try {
            sr.sampleStart();// jmeter 开始统计响应时间标记
            String resultData = String.valueOf(testStress01(msgKey));
            System.out.println(resultData);
            if (resultData != null && resultData.length() > 0) {
                sr.setResponseData("结果是："+resultData, null);
                sr.setDataType(SampleResult.TEXT);
            }
            sr.setSuccessful(true);
        } catch (Throwable e) {
            sr.setSuccessful(false);
            e.printStackTrace();
        } finally {
            sr.sampleEnd();// jmeter 结束统计响应时间标记
        }

        return sr;
    }

    private SendResult testStress01(String msgKey) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        Message msg = buildMessage(msgKey);
        SendResult sendResult = producer.send(msg);
        return sendResult;
    }

    private Message buildMessage(String msgKey) {
        String topic = "TopicTest";
        String tag = "tagA";
        Message message = new Message(topic,
                tag,
                msgKey,
                (msgBody.getBytes())
        );
        return message;
    }
}
