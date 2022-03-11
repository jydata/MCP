# MCP
MySQL Convert to Phoenix(HBase)    
Real-time data synchronization middleware based on Netty's distributed architecture.   
Professional because of focus.

---

### 1.Install phoenix-client jar 
#### 1.1 Windows for cmd:
```
mvn install:install-file -Dfile=E:/phoenix-4.10.0-cdh5.12.0-client.jar -DgroupId=org.apache.phoenix -DartifactId=phoenix-client -Dversion=4.10.0-cdh5.12.0 -Dpackaging=jar
```

#### 1.2 Mac for iTerm:
```
开发环境:
mvn install:install-file \
-Dfile=/opt/BDP/SourceCode/Phoenix/phoenix-for-cloudera-4.10-HBase-1.2-cdh5.12/phoenix-assembly/target/phoenix-4.10.0-cdh5.12.0/phoenix-4.10.0-cdh5.12.0-client.jar \
-DgroupId=org.apache.phoenix \
-DartifactId=phoenix-client \
-Dversion=4.10.0-cdh5.12.0 \
-Dpackaging=jar   
```
```
生产环境:
mvn install:install-file \
-Dfile=/opt/hbase/pro-hbase/phoenix-4.14.1-cdh5.16.1-client.jar \
-DgroupId=org.apache.phoenix \
-DartifactId=phoenix-client \
-Dversion=4.14.1-cdh5.16.1 \
-Dpackaging=jar
```
```
检查校验是否安装成功，进入Mac的maven仓库文件夹:   
jepson:/opt/software/apache-maven-3.3.9/repository/org/apache/phoenix/phoenix-client/4.10.0-cdh5.12.0:>ll
total 302232
-rw-r--r--  1 jepson  staff        209 Jan  5 23:35 _remote.repositories
-rw-r--r--  1 jepson  staff  87466310 Mar 23  2018 phoenix-client-4.10.0-cdh5.12.0.jar
-rw-r--r--  1 jepson  staff        487 Jan  5 23:35 phoenix-client-4.10.0-cdh5.12.0.pom
```

### 2.mcp-ui部署
#### 2.1 IDEA的Terminal窗口，打包dev环境后的压缩部署文件mcp-ui.tar.gz 
##### 先安装依赖：npm install ,再打包:
* 本地运行：npm run local （IDEA开发)
* 打包dev环境：npm run dev
* 打包pro环境：npm run prod
```
jepson:/work/jydata/mcp:>cd  mcp-ui
jepson:/work/jydata/mcp/mcp-ui:>ll
total 648
-rw-r--r--   1 jepson  staff     153 Dec 17 10:22 Dockerfile
-rw-r--r--   1 jepson  staff    1089 Dec 17 10:22 LICENSE
drwxr-xr-x  13 jepson  staff     416 Jan  3 15:47 build
drwxr-xr-x   6 jepson  staff     192 Jan  7 13:08 config
-rw-r--r--   1 jepson  staff    1776 Dec 17 10:22 default.conf
-rw-r--r--   1 jepson  staff    1600 Dec 24 13:18 index.html
-rw-r--r--   1 jepson  staff  309683 Dec 24 13:18 package-lock.json
-rw-r--r--   1 jepson  staff    2228 Jan  3 15:47 package.json
drwxr-xr-x  13 jepson  staff     416 Jan  5 11:47 src
drwxr-xr-x   6 jepson  staff     192 Dec 17 10:22 static
jepson:/work/jydata/mcp/mcp-ui:>which npm
/usr/local/bin/npm
 
打包dev
jepson:/work/jydata/mcp/mcp-ui:>npm run dev
jepson:/work/jydata/mcp/mcp-ui:>ll dist/
total 8
-rw-r--r--  1 jepson  staff  1780 Jan  8 16:17 index.html
drwxr-xr-x  7 jepson  staff   224 Jan  8 16:17 static
  
jepson:/work/jydata/mcp/mcp-ui:>tar -zcf mcp-ui.tar.gz -C dist/ .

```

#### 2.2 mcp-ui上传解压
```
$ cd /opt/software/
$ mkdir mcp
$ cd mcp
 
上传 
$ rz 
$ mkdir mcp-ui
$ tar -xzvf mcp-ui.tar.gz -C mcp-ui
$ ll mcp-ui
```


#### 2.3 nginx部署
```
2.3.1 下载解压
$ wget https://nginx.org/download/nginx-1.14.2.tar.gz
$ tar -xzvf nginx-1.14.2.tar.gz 
$ cd nginx-1.14.2
 
2.3.2 自定义配置
$ ./configure \
--prefix=/usr/local/nginx \
--conf-path=/usr/local/nginx/conf/nginx.conf \
--pid-path=/usr/local/nginx/conf/nginx.pid \
--lock-path=/var/lock/nginx.lock \
--error-log-path=/var/log/nginx/error.log \
--http-log-path=/var/log/nginx/access.log \
--with-http_gzip_static_module \
--http-client-body-temp-path=/var/temp/nginx/client \
--http-proxy-temp-path=/var/temp/nginx/proxy \
--http-fastcgi-temp-path=/var/temp/nginx/fastcgi \
--http-uwsgi-temp-path=/var/temp/nginx/uwsgi \
--http-scgi-temp-path=/var/temp/nginx/scgi
  
$ mkdir -p /var/temp/nginx/client
  
2.3.3 编译安装
$ make && make install
 
2.3.4 查找安装路径：
$ whereis nginx
```

#### 2.4 配置mcp-ui.conf
```
$ cd /usr/local/nginx/conf
$ cp nginx.conf.default mcp-ui.conf
 
$ vi mcp-ui.conf
    
    #mcp-server ip
    upstream mcp-api {
        server 192.168.17.36:8886;
        keepalive 50;
    }
 
    server {
        listen       80;
        
        #mcp-ui ip 
        server_name  192.168.17.36;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location / {
            root   /opt/software/mcp/mcp-ui/;
            index  index.html index.htm;
        }

        location /mcp/ {
            #rewrite ^/api/(.*) /$1 break;
            proxy_pass http://mcp-api;
            proxy_redirect              off;
            proxy_set_header            Host $host;
            proxy_set_header            X-real-ip $remote_addr;
            proxy_set_header            X-Forwarded-For $proxy_add_x_forwarded_for;
        }
        
        ......
        ......
    }

 ```
 
 #### 2.5 mcp-ui启动&停止
 ```
 $ /usr/local/nginx/sbin/nginx -c /usr/local/nginx/conf/mcp.conf
 
 $ ps -ef|grep nginx
 root      12393      1  0 Jan08 ?        00:00:00 nginx: master process sbin/nginx -c conf/mcp.conf
 nobody    12394  12393  0 Jan08 ?        00:00:00 nginx: worker process
 root     151955 136854  0 13:45 pts/4    00:00:00 grep --color=auto nginx
$ netstat -nlp|grep nginx
 tcp        0      0 0.0.0.0:80              0.0.0.0:*               LISTEN      12393/nginx: master 
 ```
 
```
停止
/usr/local/nginx/sbin/nginx -s stop
/usr/local/nginx/sbin/nginx -s quit    此方式停止步骤是待nginx进程处理任务完毕进行停止。
/usr/local/nginx/sbin/nginx -s reload  此方式相当于先查出nginx进程id再使用kill命令强制杀掉进程。
 
重启
a.先停止再启动（推荐）：
对 nginx 进行重启相当于先停止再启动，即先执行停止命令再执行启动命令。如下：
/usr/local/nginx/sbin/nginx -s quit
/usr/local/nginx/sbin/nginx -c conf/mcp.conf
  
b.重新加载配置文件：
当 ngin x的配置文件 nginx.conf 修改后，要想让配置生效需要重启 nginx，使用-s reload不用先停止 ngin x再启动 nginx 即可将配置信息在 nginx 中生效，如下：
/usr/local/nginx/sbin/nginx -s reload
```

### 4.mcp-server&mcp-agent 部署
#### 4.1 package打包
```
$ mvn clean package
[INFO] --- maven-assembly-plugin:2.6:single (make-assembly) @ mcp-server ---
[INFO] Reading assembly descriptor: src/main/build/assembly.xml
[INFO] Building tar: /work/jydata/mcp/mcp-server/target/mcp-server-1.0.0-bin.tar.gz
 
[INFO] --- maven-assembly-plugin:2.6:single (make-assembly) @ mcp-agent ---
[INFO] Reading assembly descriptor: src/main/build/assembly.xml
[INFO] Building tar: /work/jydata/mcp/mcp-agent/target/mcp-agent-1.0.0-bin.tar.gz
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] mcp ................................................ SUCCESS [  0.109 s]
[INFO] mcp-common ......................................... SUCCESS [  3.395 s]
[INFO] mcp-server ......................................... SUCCESS [ 12.054 s]
[INFO] mcp-agent .......................................... SUCCESS [  4.959 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
```

#### 4.2 使用rz上传服务器解压
```
4.2.1 mcp-server
$ tar -xzvf  mcp-server-1.0.0-bin.tar.gz
$ cd mcp-server-1.0.0
$ bin/mcp-server.sh start
 
4.2.2 mcp-agent
$ tar -xzvf  mcp-agent-1.0.0-bin.tar.gz
$ cd mcp-agent-1.0.0
$ bin/mcp-agent.sh start
```

#### 4.3 查看日志
```
$ cd /var/log/mcp/
$ ll
total 0
drwxr-xr-x 2 root root 45 Jan  9 19:18 agent
drwxr-xr-x 2 root root 10 Jan  9 18:35 job
drwxr-xr-x 2 root root 46 Jan  9 18:34 server
  
#mcp-server log
$ cd server/
$ ll
total 1600
-rw-r--r-- 1 root root 623606 Jan  9 19:23 mcp-server.2019-01-09.log
 
#mcp-agent log 
$ cd agent/
$ ll
total 128
-rw-r--r-- 1 root root 68137 Jan  9 19:23 mcp-agent.2019-01-09.log

```

### 5.打开web
[http://192.168.17.36](http://192.168.17.36)   admin/111111



