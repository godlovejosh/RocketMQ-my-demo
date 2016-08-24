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