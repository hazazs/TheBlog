package hu.hazazs.blog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlogApplication {
    private static final Logger logger = LogManager.getLogger(BlogApplication.class);
    public static Logger getLogger() {
        return logger;
    }
    public static void main(String[] args) {
	SpringApplication.run(BlogApplication.class, args);
    }
}