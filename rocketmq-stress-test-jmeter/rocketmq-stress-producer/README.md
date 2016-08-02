## 前台启动,后台启动,查找进程,杀死进程
* /data/source/apache-jmeter-3.0/bin/jmeter -n -t /data/source/RocketMQ-test-report/scripts/RocketMQ_test_plan.jmx -l /data/source/RocketMQ-test-report/scripts/rocket-listener.jtl
* nohup /data/source/apache-jmeter-3.0/bin/jmeter -n -t /data/source/RocketMQ-test-report/scripts/RocketMQ_test_plan.jmx -l /data/source/RocketMQ-test-report/scripts/rocket-listener.jtl &
* nohup /data/source/apache-jmeter-3.0/bin/jmeter -n -t /data/source/RocketMQ-test-report/scripts/RocketMQ_test_plan.jmx -l /data/source/RocketMQ-test-report/scripts/rocket-listener.csv &
* ps aux|grep /data/source
* kill -9 进程号

## 插件生成 png图片或者csv报告
* 使用jmeter插件 可以将结果文件 生成 png 或者 csv (JMeterPlugins-Standard-1.2.0)
* <https://jmeter-plugins.org/wiki/JMeterPluginsCMD/>
* <http://www.yeetrack.com/?p=1028>

1. CMDRunner.jar
2. JMeterPlugins-Standard.jar
3. JMeterPlugins-Extras.jar

* java -jar CMDRunner.jar --tool Reporter --generate-png test.png --input-jtl rocket-listener.jtl --plugin-type ResponseTimesOverTime --width 800 --height 600
* java -jar CMDRunner.jar --tool Reporter --generate-csv test.csv --input-jtl rocket-listener.jtl --plugin-type ResponseTimesOverTime
* java -jar CMDRunner.jar --tool Reporter --generate-png ThroughputVsThreads.png --input-jtl rocket-listener.jtl --plugin-type ThroughputVsThreads --width 1000 --height 1000
*

## HTML报告 Apache JMeter Dashboard
* /Users/josh/zhongtong/apache-jmeter-3.0/bin/jmeter -g rocket-listener.csv -o ./output
<http://www.tuicool.com/articles/BNvuEzr>

#### jmeter.properties:
    jmeter.save.saveservice.bytes = true
    jmeter.save.saveservice.label = true
    jmeter.save.saveservice.latency = true
    jmeter.save.saveservice.response_code = true
    jmeter.save.saveservice.response_message = true
    jmeter.save.saveservice.successful = true
    jmeter.save.saveservice.thread_counts = true
    jmeter.save.saveservice.thread_name = true
    jmeter.save.saveservice.time = true
    # the timestamp format must include the time and should include the date.
    # For example the default, which is milliseconds since the epoch:
    jmeter.save.saveservice.timestamp_format = ms
    # Or the following would also be suitable
    jmeter.save.saveservice.timestamp_format = yyyy/MM/dd HH:mm:ss



* 4核 + 16G + SSD硬盘 (IO读写速度很重要,不使用外网IP测试所以与带宽没有关系)
* 测试结果参考: 单机测试：TPS 3W+ 

* TPS上不去的问题之一:多网卡问题:虽然生产者指定了nameserver的内网IP,但是broker配置文件中如果没有指定brokerIP为内网IP,broker很可能随机连接一块网卡,导致broker走的是外网
* TPS上不去的问题之二:同步刷盘与异步刷盘
    
    
    brokerRole=SYNC_MASTER
    flushDiskType=ASYNC_FLUSH

## 参考脚本
    #!/bin/bash
    #
    
    PROG_NAME=$0
    ACTION=$1
    CPATH=/data/source/RocketMQ-test-report/lib
    LOG_HOME=$HOME/logs/rocketmqlogs
    
    usage() {
        echo "Usage: $PROG_NAME {producer|consumer}"
        exit 1;
    }
    
    producer_start() {
        nohup /data/source/apache-jmeter-3.0/bin/jmeter -n -t /data/source/RocketMQ-test-report/scripts/RocketMQ_test_plan.jmx -l /data/source/RocketMQ-test-report/scripts/rocket-listener.csv > $LOG_HOME/application/producer.out 2>&1 &
    }
    
    consumer_start() {
        nohup java -classpath $CPATH/rocketmq-client-3.4.6.jar:$CPATH/rocketmq-remoting-3.4.6.jar:$CPATH/netty-all-4.0.29.Final.jar:$CPATH/rocketmq-common-3.4.6.jar:$CPATH/fastjson-1.2.3.jar:$CPATH/rocketmq-stress-consumer-1.0-SNAPSHOT.jar:$CPATH/slf4j-api-1.7.5.jar:$CPATH/slf4j-log4j12-1.7.5.jar:$CPATH/log4j-1.2.17.jar:$CPATH/mysql-connector-java-6.0.3.jar:. com.alibaba.rocketmq.Consumer > $LOG_HOME/application/consumer.out 2>&1 &
    }
    
    case "$ACTION" in
        producer)
            producer_start
        ;;
        consumer)
            consumer_start
        ;;
        *)
            usage
        ;;
    esac
    
    exit 0
    

## 监控
* https://github.com/rocketmq/rocketmq-console
* top  shift+P按CPU排序,shift+M按内存排序
* jps  查看java进程
* free -m 空闲内存
* df -lh  磁盘空间
* iptraf  tcp监控  sudo yum install -y iptraf


## 环境优化
* sudo vim /etc/sysctl.conf  优化系统 线程数、tcp数、文件数...
* jmeter 和 RocketMQ的 jvm 内存分配优化
