# 使用JMeter测试RocketMQ
* 继承AbstractJavaSamplerClient类
* 生产者定义成static,实例属于类,不属于对象,避免多线程重复创建对象
* start定义在static静态块中,避免多线程重复开启
* 无需写main方法
* getDefaultParameters 接收参数
* SampleResult将返回值作为结果出来
