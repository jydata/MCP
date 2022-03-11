#!/bin/sh
## java env
JAVA_HOME=$JAVA_HOME
JAVA_OPTS="-Xmx8192m -Xms8192m"

## env
MCP_HOME=$(cd `dirname $0`; pwd)/..

API_NAME=mcp-agent
API_VERSION=1.0.0

JAR_NAME=${API_NAME}-${API_VERSION}.jar
PID=${API_NAME}.pid

#使用说明，用来提示输入参数
usage() {
    echo "Usage: sh mcp-server.sh [start|stop|restart|status]"
    exit 1
}

#检查程序是否在运行
is_exist(){
  pid=`ps -ef|grep $JAR_NAME|grep -v grep|awk '{print $2}' `
  #如果不存在返回1，存在返回0
  if [ -z "${pid}" ]; then
   return 1
  else
    return 0
  fi
}

#启动方法
start(){
  is_exist
  if [ $? -eq "0" ]; then
    echo ">>> ${JAR_NAME} is already running PID=${pid} <<<"
  else
    nohup ${JAVA_HOME}/bin/java ${JAVA_OPTS} -jar ${MCP_HOME}/$JAR_NAME  >/dev/null 2>&1 &
    echo $! > ${MCP_HOME}/$PID
    echo ">>> start $JAR_NAME successed PID=$! <<<"
   fi
  }

#停止方法
stop(){
  #is_exist
  pidf=$(cat ${MCP_HOME}/$PID)
  #echo "$pidf"
  echo ">>> api PID = $pidf begin kill $pidf <<<"
  kill $pidf
  rm -rf ${MCP_HOME}/$PID
  sleep 2
  is_exist
  if [ $? -eq "0" ]; then
    echo ">>> api 2 PID = $pid begin kill -9 $pid  <<<"
    kill -9  $pid
    sleep 2
    echo ">>> $JAR_NAME process stopped <<<"
  else
    echo ">>> ${JAR_NAME} is not running <<<"
  fi
}

#输出运行状态
status(){
  is_exist
  if [ $? -eq "0" ]; then
    echo ">>> ${JAR_NAME} is running PID is ${MCP_HOME}/${pid} <<<"
  else
    echo ">>> ${JAR_NAME} is not running <<<"
  fi
}

#重启
restart(){
  stop
  start
}

#根据输入参数，选择执行对应方法，不输入则执行使用说明
case "$1" in
  "start")
    start
    ;;
  "stop")
    stop
    ;;
  "status")
    status
    ;;
  "restart")
    restart
    ;;
  *)
    usage
    ;;
esac
exit 0