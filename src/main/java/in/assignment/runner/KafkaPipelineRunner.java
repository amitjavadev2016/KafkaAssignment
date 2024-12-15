package in.assignment.runner;

import in.assignment.service.ApacheBeamKafkaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class KafkaPipelineRunner implements CommandLineRunner {

    static Logger logger = Logger.getLogger(KafkaPipelineRunner.class.getName());

    private final ApacheBeamKafkaService kafkaService;


    public KafkaPipelineRunner(ApacheBeamKafkaService kafkaService) {
        this.kafkaService = kafkaService;
        logger.info("KafkaPipelineRunner created : ");
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("KafkaPipelineRunner executed" );
        kafkaService.runPipeline();
    }
}
