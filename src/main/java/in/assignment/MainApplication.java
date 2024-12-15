package in.assignment;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Logger;

@SpringBootApplication
public class MainApplication {

    static Logger logger = Logger.getLogger(MainApplication.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
        logger.info("**************Kafka v1 Processor Application Started...");
    }
}