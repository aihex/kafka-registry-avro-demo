package com.github.aihex.avro_demo;

import com.github.aihex.metric.Metric;
import com.github.aihex.phonebook.Employee;
import com.github.aihex.phonebook.Status;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.joda.time.DateTime;
import oshi.SystemInfo;

import java.util.Properties;
import java.util.Random;
import java.util.stream.IntStream;

public class AvroProducer {

    private static final Random random = new Random();
    private static final RandomString randomString = new RandomString();
    private static final SystemInfo si = new SystemInfo();

    private static final String[] status = new String[]{"RETIRED", "SALARY", "HOURLY", "PART_TIME"};

    private static Producer<Long, Metric> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "AvroProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());

        // Configure the KafkaAvroSerializer.
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                KafkaAvroSerializer.class.getName());

        // Schema Registry location.
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
                "http://localhost:8082");

        return new KafkaProducer<>(props);
    }

    private final static String TOPIC = "metric-ts";

    public static void main(String... args) {

        Producer<Long, Metric> producer = createProducer();

        IntStream.range(1, 10000).forEach(index -> {
            try {
                producer.send(new ProducerRecord<>(TOPIC, (long) index, newMetric()));
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        producer.flush();
        producer.close();

    }

    private static Employee newEmployee() {
        return Employee.newBuilder().setAge(random.nextInt(100) + 1)
                .setFirstName(randomString.nextString())
                .setLastName(randomString.nextString())
                .setStatus(Status.valueOf(status[random.nextInt(status.length)]))
                .build();
    }

    private static Metric newMetric() {
        return Metric.newBuilder().setCpu(si.getHardware().getProcessor().getSystemLoadAverage())
                .setMemory(si.getHardware().getMemory().getTotal() - si.getHardware().getMemory().getAvailable())
                .setDevice(si.getOperatingSystem().getNetworkParams().getHostName())
                .setTime(DateTime.now())
                .build();
    }
}
