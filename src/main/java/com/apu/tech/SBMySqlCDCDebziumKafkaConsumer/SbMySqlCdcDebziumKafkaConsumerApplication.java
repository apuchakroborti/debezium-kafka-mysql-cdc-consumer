package com.apu.tech.SBMySqlCDCDebziumKafkaConsumer;

import com.apu.tech.SBMySqlCDCDebziumKafkaConsumer.consumer.MyKafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.log4j.Logger;

import java.util.Properties;

@SpringBootApplication
public class SbMySqlCdcDebziumKafkaConsumerApplication implements CommandLineRunner{


	@Value("${kafka.topic}")
	private String topicName;

	@Value("${kafka.bootstrap.servers}")
	private String kafkaBootstrapServers;

	@Value("${zookeeper.groupId}")
	private String zookeeperGroupId;

	@Value("${zookeeper.host}")
	String zookeeperHost;

	private static final Logger logger = Logger.getLogger(SbMySqlCdcDebziumKafkaConsumerApplication.class);


	public static void main(String[] args)

	{
		SpringApplication.run(SbMySqlCdcDebziumKafkaConsumerApplication.class, args);
	}

	@Override
	public void run(String... args){

		Properties consumerProperties = new Properties();
		consumerProperties.put("bootstrap.servers", kafkaBootstrapServers);
		consumerProperties.put("group.id", zookeeperGroupId);
		consumerProperties.put("zookeeper.session.timeout.ms", "6000");
		consumerProperties.put("zookeeper.sync.time.ms","2000");
		consumerProperties.put("auto.commit.enable", "false");
		consumerProperties.put("auto.commit.interval.ms", "1000");
		consumerProperties.put("consumer.timeout.ms", "-1");
		consumerProperties.put("max.poll.records", "1");
		consumerProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		consumerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		Thread kafkaConsumerThread = new Thread(() -> {
			logger.info("Starting Kafka consumer thread.");

			MyKafkaConsumer simpleKafkaConsumer = new MyKafkaConsumer(topicName, consumerProperties );

			simpleKafkaConsumer.runWorker();
		});

		kafkaConsumerThread.start();


	}

}
