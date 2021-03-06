## Demo

##### Setup Kafka runtime

In vallina kafka directory,

```shell
$ cd vallina-kafka-dir
$ bin/zookeeper-server-start.sh config/zookeeper.properties
$ bin/kafka-server-start.sh config/server.properties
```

##### Setup Kafka schema registry

In Confluent Kafka,

```shell
cd confluent-kafka
$ bin/schema-registry-start etc/schema-registry/schema-registry.properties
```

The used schema-registry.properties is under `config/`

##### Setup Kafka Connector(JDBC Sink Connector)

In Confluent Kafka,

```shell
$ bin/connect-standalone etc/schema-registry/connect-avro-standalone.properties \ 
    etc/kafka-connect-jdbc/sink-quickstart-postgresql.properties
```

##### Setup TimescaleDB(predefine DB schema and hypertable extension)

Install TimescaleDB,

```
$ docker run -d --name timescaledb -p 5432:5432 -e POSTGRES_PASSWORD=password timescale/timescaledb
$ docker exec -it timescaledb psql -U postgres
```

Predefine schema and enable hypertable extension,

```sql
CREATE database metrics;
\c metrics;
CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;
CREATE TABLE 'metric' (
  time      TIMESTAMPTZ       NOT NULL,
  device    TEXT              NOT NULL,
  cpu       DOUBLE PRECISION  NOT NULL,
  memory    BIGINT  NULL      NOT NULL
);
SELECT create_hypertable('metric', 'time');
```

##### Setup avro producer

Metric.avsc

```json
{"namespace": "com.github.aihex.metric",
   "type": "record",
     "name": "Metric",
     "doc" : "Represents an Metric for a device",
     "fields": [
        {"name": "time", "type": {"type": "long", "logicalType": "timestamp-millis"}},
        {"name": "device", "type": "string"},
        {"name": "cpu", "type": "double"},
        {"name": "memory",  "type": "long"}
     ]
}
```

For demo producer code, refer to `src/main/java/com/github/aihex/avro_demo/AvroProducer.java`

##### Query table
