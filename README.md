### 这里仅仅是一些demo，没有主pom，demo之间都是相互独立的，并非子项目的关系，只是存在目录结构

## 参考脚本
* rocketmq-namesrv 


    #!/bin/sh
    #
    
    PROG_NAME=$0
    SERVER_NAME=$1
    ACTION=$2
    
    MQ_COMMAND=/usr/local/rocketmq/bin
    LOG_HOME=$HOME/logs/rocketmqlogs
    
    usage() {
        echo "Usage: $PROG_NAME {nameserver1|nameserver2} {start|stop|restart}"
        exit 1;
    }
    
    start() {
        nohup sh $MQ_COMMAND/mqnamesrv -n "$SERVER_NAME.pamirs.com:9876"  > $LOG_HOME/out/$SERVER_NAME.out 2>&1 &
    }
    
    stop() {
        sh $MQ_COMMAND/mqshutdown namesrv > $LOG_HOME/out/mqnamesrv.out 2>&1 &
    }
    
    restart() {
        stop
        start
    }
    
    case "$ACTION" in
      start)
        start
      ;;
      stop)
        stop
      ;;
      restart)
        restart
      ;;
      *)
        usage
      ;;
    esac
    
    exit 0


* rocketmq-namesrv


    #!/bin/sh
    #
    
    PROG_NAME=$0
    BROKER_NAME=$1
    ACTION=$2
    
    MQ_COMMAND=/usr/local/rocketmq/bin
    LOG_HOME=$HOME/logs/rocketmqlogs
    RUN_HOME=$HOME/run
    
    usage() {
        echo "Usage: $PROG_NAME {master1|master2|slave1|slave2} {start|stop|restart}"
        exit 1;
    }
    
    start() {
        nohup sh $MQ_COMMAND/mqbroker -c $RUN_HOME/RocketMQ/conf/2m-2s-sync/broker-$BROKER_NAME.properties > $LOG_HOME/out/$BROKER_NAME.out 2>&1 &
    }
    
    stop() {
        sh $MQ_COMMAND/mqshutdown broker > $LOG_HOME/out/mqbroker.out 2>&1 &
    }
    restart() {
        stop
        start $1
    }
    
    case "$ACTION" in
      start)
        start
      ;;
      stop)
        stop
      ;;
      restart)
        restart
      ;;
      *)
        usage
      ;;
    esac
    
    exit 0
