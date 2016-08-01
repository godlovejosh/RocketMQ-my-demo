package com.alibaba.rocketmq;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wuxing on 16/7/25.
 */
public class Producer extends AbstractJavaSamplerClient {

    /** nameServerIP地址和端口号 */
    private static String nameServer = "127.0.0.1:9876";

    /** 生产者分组 */
    private static String producerGroup = "producer0729";

    /** 生产者TOPIC */
    private String topic = "topic0729";

    /** 生产者TAG */
    private String tag = "defaultTag";

    /** 生产者KEY */
    private String key = "defaultKey";

    /** 生产者处理类 */
    private static DefaultMQProducer producer = new DefaultMQProducer(producerGroup);

    /** 消息最大size */
    private static int  maxMessageSize = 128;

    /** 重试次数 */
    private static int retryTimesWhenSendFailed = 2;

    /** 发送消息超时时间 */
    private static int sendMsgTimeout = 3000;

    /** 是否保存到数据库 */
    private int saveActive = 0; // 0表示不保存,1表示保存

    /** 消息默认body */
    private static final String MSG_BODY = "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdef" +
            "ghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghi" +
            "jabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghija" +
            "bcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcd" +
            "efghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefg" +
            "hijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij" +
            "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcd" +
            "efghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij" +
            "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghi" +
            "jabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcd";

    static {
        producer.setNamesrvAddr(nameServer);
//        producer.setMaxMessageSize(maxMessageSize);
        producer.setRetryTimesWhenSendFailed(retryTimesWhenSendFailed);
        producer.setSendMsgTimeout(sendMsgTimeout);
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("saveActive", String.valueOf(saveActive));
        return params;
    }

    //每个线程测试前执行一次，做一些初始化工作；
    public void setupTest(JavaSamplerContext arg0) {
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {

        SampleResult sr = new SampleResult();
        sr.setSampleLabel( "java request");
        String flag = javaSamplerContext.getParameter("saveActive");
        try {
            sr.sampleStart();// jmeter 开始统计响应时间标记
            String resultData = String.valueOf(testStress01(flag));
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

    private SendResult testStress01(String flag) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        Message msg = buildMessage();
        SendResult sendResult = producer.send(msg);

        if(Integer.parseInt(flag) == 1) {
            MsgInfo msgInfo = new MsgInfo();
            msgInfo.setMsgId(sendResult.getMsgId());
            msgInfo.setCdate(new Date());
            msgInfo.setTopic(topic);
            msgInfo.setTag(tag);
            msgInfo.setKey(key);
            if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
                msgInfo.setSendStatus((byte) 0);
            } else {
                msgInfo.setSendStatus((byte) -1);
            }
            saveMessage(msgInfo);
        }

        return sendResult;
    }

    private Message buildMessage() {
        String msgBody = "";
        int len = MSG_BODY.length();
        if (len >= this.maxMessageSize) {
            msgBody = MSG_BODY.substring(0, this.maxMessageSize);
        } else {
            int x = this.maxMessageSize / len;
            int m = this.maxMessageSize % len;
            for (int i = 0; i < x; i++) {
                msgBody += MSG_BODY;
            }
            msgBody += MSG_BODY.substring(0, m);
        }
        Message message = new Message(topic,
                tag,
                key,
                (msgBody.getBytes())
        );
        return message;
    }

    private void saveMessage(MsgInfo msgInfo) {
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
            String sql = "insert into msg_info(msg_id,topic,`key`,cdate,sendStatus) values('"+ msgInfo.getMsgId() +"','" +
                    msgInfo.getTopic() +"','"+ msgInfo.getKey() +"','"+ sf.format(msgInfo.getCdate()) +"','"+ msgInfo.getSendStatus() +"')";

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
