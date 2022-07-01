#### clickhouse卸载安装

```
yum list installed | grep clickhouse
yum remove -y clickhouse-*

rm -rf /var/lib/clickhouse
rm -rf /etc/clickhouse-*
rm -rf /var/log/clickhouse-server

rpm -ivh *.rpm 

vim /etc/clickhouse-server/config.xml  修改配置文件放开listen host

systemctl start clickhouse-server 启动clickhouse服务

systemctl disable clickhouse-server 关闭开机自启

clickhouse-client -m 客户端访问


clikhouse 密码修改
https://blog.csdn.net/chengyuqiang/article/details/108534587

切换目录
 cd /etc/clickhouse-server/
 
修改密码 
vim user.xml password 123456
SHA256密码加密：

echo -n 123456 | openssl dgst -sha256

获取  (stdin)= 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92 

修改默认密码 user.d/ default-password

客户端进入 ：clickhouse-client --host 127.0.0.1 --port 9933 --password 123456

```

