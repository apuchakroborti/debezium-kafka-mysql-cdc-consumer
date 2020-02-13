package com.apu.tech.SBMySqlCDCDebziumKafkaConsumer.consumer;

import com.jayway.jsonpath.JsonPath;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MyKafkaConsumer {

    private static final Logger logger = Logger.getLogger(MyKafkaConsumer.class);

    private KafkaConsumer<String, String> kafkaConsumer;

    public MyKafkaConsumer(String theTechCheckTopicName, Properties consumerProperties) {

        kafkaConsumer = new KafkaConsumer<>(consumerProperties);
        kafkaConsumer.subscribe(Arrays.asList(theTechCheckTopicName));
    }

    public void runWorker(){
        while(true) {


            ConsumerRecords<String, String> records = kafkaConsumer.poll(100);

            for (ConsumerRecord<String, String> record : records) {
                System.out.println("consumerStart\n");


                String message = record.value();

                if(message!=null && !message.isEmpty()) {


                    //Logging the received message to the console.

                    System.out.println(message + "\n");
                    logger.info("Received message: " + message);


                    Map before = JsonPath.read(message, "$.payload.before");

                    if (before != null) {

                        int before_id = JsonPath.read(message, "$.payload.before.id");
                        String before_first_name = JsonPath.read(message, "$.payload.before.first_name");
                        String before_last_name = JsonPath.read(message, "$.payload.before.last_name");
                        int before_age = JsonPath.read(message, "$.payload.before.age");
                        String before_address = JsonPath.read(message, "$.payload.before.address");


                        System.out.println("Before changes...");
                        System.out.println(" ID = " + before_id);
                        System.out.println("FirstName = " + before_first_name);
                        System.out.println("LastName= " + before_last_name);
                        System.out.println("Age = " + before_age);
                        System.out.println("Address = " + before_address);

                    } else {
                        System.out.println("Before changes...");
                        System.out.println("null");
                    }


                    Map after = JsonPath.read(message, "$.payload.after");

                    if (after != null) {
                        int after_id = JsonPath.read(message, "$.payload.after.id");
                        String after_first_name = JsonPath.read(message, "$.payload.after.first_name");
                        String after_last_name = JsonPath.read(message, "$.payload.after.last_name");
                        int after_age = JsonPath.read(message, "$.payload.after.age");
                        String after_address = JsonPath.read(message, "$.payload.after.address");

                        System.out.println("After changes...");
                        System.out.println("ID = " + after_id);
                        System.out.println("FirstName = " + after_first_name);
                        System.out.println("LastName= " + after_last_name);
                        System.out.println("Age = " + after_age);
                        System.out.println("Address = " + after_address);

                    } else {
                        System.out.println("After changes...");
                        System.out.println("null");
                    }

                    {
                        Map<TopicPartition, OffsetAndMetadata> commitMessage = new HashMap<>();

                        commitMessage.put(new TopicPartition(record.topic(), record.partition()),
                                new OffsetAndMetadata(record.offset() + 1));

                        kafkaConsumer.commitSync(commitMessage);

                        logger.info("Offset committed to Kafka.");
                    }
                }
            }

        }

    }


}
