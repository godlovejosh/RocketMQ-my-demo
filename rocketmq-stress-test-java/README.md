# 手工测试
### 通过自己编写多线程任务调度去压测的注意事项:
* 一定要有主入口main方法
* 将项目打成jar包与所有的依赖包放在一个目录下运行
* 推荐运行方式: java -classpath <执行jar包:依赖包> 主方法所在类的全类名
* 例如:java -classpath ./rocketmq-client-3.5.5.jar:./rocketmq-stress-consumer-1.0-SNAPSHOT.jar:./slf4j-api-1.7.5.jar:./slf4j-log4j12-1.7.5.jar:./log4j-1.2.17.jar:. com.alibaba.rocketmq.Consumer
* 手工测试通常需要建表保存压测数据,然后统计出成功和失败的比例
