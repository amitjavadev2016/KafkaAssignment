package in.assignment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.assignment.dto.KafkaMessage;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.io.kafka.KafkaRecord;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Partition;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.logging.Logger;

@Service
public class ApacheBeamKafkaService {

    static Logger logger = Logger.getLogger(ApacheBeamKafkaService.class.getName());

    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;// = "localhost:9092";

    private static final String SOURCE_TOPIC = "SOURCE_TOPIC";
    private static final String EVEN_TOPIC = "EVEN_TOPIC";
    private static final String ODD_TOPIC = "ODD_TOPIC";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void runPipeline() {

        Pipeline pipeline = Pipeline.create();

        // Step 1: Read messages from Kafka
        PCollection<KV<String, String>> inputCollection = pipeline
                .apply("ReadFromKafka", KafkaIO.<String, String>read()
                        .withBootstrapServers(BOOTSTRAP_SERVERS)
                        .withTopic(SOURCE_TOPIC)
                        .withKeyDeserializer(org.apache.kafka.common.serialization.StringDeserializer.class)
                        .withValueDeserializer(org.apache.kafka.common.serialization.StringDeserializer.class))
                .apply("ExtractKeyValue", ParDo.of(new ExtractKeyValueFn()));

        // Step 2: Partition the collection into two: even age and odd age
        PCollectionList<KV<String, String>> partitions = inputCollection.apply("PartitionByAge", Partition.of(2, new AgePartitionFn(objectMapper)));

        // Step 3: Write partition 0 (even age) to EVEN_TOPIC
        partitions.get(0).apply("WriteToEvenTopic", KafkaIO.<String, String>write()
                .withBootstrapServers(BOOTSTRAP_SERVERS)
                .withTopic(EVEN_TOPIC)
                .withKeySerializer(org.apache.kafka.common.serialization.StringSerializer.class)
                .withValueSerializer(org.apache.kafka.common.serialization.StringSerializer.class));

        // Step 4: Write partition 1 (odd age) to ODD_TOPIC
        partitions.get(1).apply("WriteToOddTopic", KafkaIO.<String, String>write()
                .withBootstrapServers(BOOTSTRAP_SERVERS)
                .withTopic(ODD_TOPIC)
                .withKeySerializer(org.apache.kafka.common.serialization.StringSerializer.class)
                .withValueSerializer(org.apache.kafka.common.serialization.StringSerializer.class));

        // Run the pipeline
        pipeline.run().waitUntilFinish();
        logger.info("Pipeline completed.");

    }

    static class ExtractKeyValueFn extends DoFn<KafkaRecord<String, String>, KV<String, String>> {
        @ProcessElement
        public void processElement(@Element KafkaRecord<String, String> record, OutputReceiver<KV<String, String>> out) {
            out.output(record.getKV());
        }
    }

    static class AgePartitionFn implements Partition.PartitionFn<KV<String, String>> {

        private transient ObjectMapper objectMapper;

        public AgePartitionFn(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        private ObjectMapper getObjectMapper() {
            if (objectMapper == null) {
                objectMapper = new ObjectMapper(); // Initialize lazily
            }
            return objectMapper;
        }

        @Override
        public int partitionFor(KV<String, String> element, int numPartitions) {
            try {
                String value = element.getValue();
                KafkaMessage message = getObjectMapper().readValue(value, KafkaMessage.class);

                logger.info("message received from topic is : " + getObjectMapper().writeValueAsString(message));

                // Calculate age
                LocalDate dob = LocalDate.parse(message.getDateOfBirth());
                int age = Period.between(dob, LocalDate.now()).getYears();

                // Return partition index: 0 for even, 1 for odd
                return (age % 2 == 0) ? 0 : 1;
            } catch (Exception e) {
                throw new RuntimeException("Error during partitioning", e);
            }
        }
    }


}


