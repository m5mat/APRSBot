package cafe.deadbeef.aprs_bridge;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * APRS to RabbitMQ Bridge
 *
 */
@SpringBootApplication
public class App extends SpringBootServletInitializer
{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(App.class);
    }
	
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
    @PostConstruct
    private void start() {
    	logger.info("In start()");
    }
}
