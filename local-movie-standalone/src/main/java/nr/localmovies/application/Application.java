package nr.localmovies.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "nr")
@EntityScan(basePackages = "nr")
@EnableCaching
@ComponentScan(basePackages = "nr")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}