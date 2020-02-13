MySQL Change Data Capture Using kafka, debezium MYSQL connector: \
Download the kafka_2.12-2.4.0 from url(https://www.apache.org/dyn/closer.cgi?path=/kafka/2.4.0/kafka_2.12-2.4.0.tgz) and extract it to any directory \
then extract the file\
For 'name*.tar.gz' file\
$ tar -xvzf /path/to/the/kafka_2.12-2.4.0.tgz
$ cd kafka_2.12-2.4.0
\
Setting up MySQL server: \
https://debezium.io/documentation/reference/assemblies/cdc-mysql-connector/as_setup-the-mysql-server.html 

Create a new MySql user: \
$ mysql -u root -p \
Mysql > CREATE USER 'apu'@'localhost' IDENTIFIED BY 'tigerit'; \
Mysql > GRANT ALL PRIVILEGES ON * . * TO 'apu'@'localhost'; \
Mysql $ FLUSH PRIVILEGES; \
\
Enabling the MySQL binlog for Debezium: \
$ sudo vi /etc/mysql/my.cnf \
Add the following lines: \
[mysqld] \
server-id         = 223344 \
log_bin           = mysql-bin \
binlog_format     = row \
binlog_row_image  = full \
expire_logs_days  = 10 \
performance_schema = ON \
show_compatibility_56 = On \
gtid_mode = ON \
enforce_gtid_consistency = ON \
binlog_rows_query_log_events = ON \
\
$ sudo systemctl restart mysql.service \
$ sudo systemctl status mysql.service \
\
Checking: \
1.Check if the log-bin option is already on or not. \
Mysql > SELECT variable_value as "BINARY LOGGING STATUS (log-bin) ::" 
FROM information_schema.global_variables WHERE variable_name='log_bin'; \
2.Confirm your changes by checking the binlog status once more. \
Mysql > SELECT variable_value as "BINARY LOGGING STATUS (log-bin) ::"
FROM information_schema.global_variables WHERE variable_name='log_bin'; 

Confirm the GTID changes: \
Mysql> show global variables like '%GTID%';\
\
Debezium MySql Connector Installation: \
Download the MySql-Debezium Connector and execute the followings command: \
$ cp /yourpath/mysql-plugin/debezium-connector-mysql-1.0.0.Final-plugin.tar.gz /yourpath/plug-in \
$ cd /yourpath/plug-in \
$ tar -xvzf debezium-connector-mysql-1.0.0.Final-plugin.tar.gz \
$ rm debezium-connector-mysql-1.0.0.Final-plugin.tar.gz \
\
$ cd /path/to/the/kafka \
$ sudo mkdir workers \
$ cd workers \
$ vi mysql-debezium-connector.properties \
Paste the below code to the file: \
name=dbserver1 \
connector.class=io.debezium.connector.mysql.MySqlConnector \
tasks.max=1 \
database.hostname=127.0.0.1 \
database.port=3306 \
database.user=apu \
database.password=tigerit \
database.server.id=223344 \
database.server.name=mysqldb \
database.whitelist=studentdb \
database.history.kafka.bootstrap.servers=localhost:9092 \
database.history.kafka.topic=kafka-connect-test \
include.schema.changes=true \
key.converter=org.apache.kafka.connect.json.JsonConverter \
value.converter=org.apache.kafka.connect.json.JsonConverter \

Start the zookeeper: \
$ cd /path/to/the/kafka_2.12-2.4.0 \
$ ./bin/zookeeper-server-start.sh ./config/zookeeper.properties 

Start the kafka server: \
$ ./bin/kafka-server-start.sh ./config/server.properties 

Start the connector: \
$ vi ./config/connect-standalone.properties
Add the plugin.path=/yourpath/mysql-plugin/
$ ./bin/connect-standalone.sh ./config/connect-standalone.properties ./workers/mysql-debezium-connector.properties

Now the check the connector running or not.. \
$ curl -s "http://localhost:8083/connectors" | jq '.[]' | xargs -I{dbserver1} curl -s "http://localhost:8083/connectors/"{dbserver1}"/status" | jq -c -M '[.name,.connector.state,.tasks[].state] |  
join(":|:")'| column -s : -t| sed 's/"//g'| sort

Output:\
dbserver1 | RUNNING | RUNNING\
\
\
\
\
Start the SpringBoot Consumer:\
$ cd /path/to/project\
$ mvn spring-boot:run\
\
Do some changes in the MySql Database:\
$ mysql -u apu -p\
password\
$ show databases;\
$ CREATE DATABASE  studentdb;\
$ USE studentdb;\
$ CREATE TABLE Customers (\
   id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
   first_name varchar(255) NOT NULL,
   last_name varchar(255) NOT NULL,
   age int NOT NULL,   
address varchar(255)NOT NULL
);\
$ INSERT INTO Customers (first_name, last_name, age, address)
VALUES ('Apu', 'Chakroborti', 26, 'abc');\
\
Then check the spring boot consumer\
Before and After will be shown. 

Clean up resources:\
$ ctrl+c for zookeeper, kafka and connector \


