package nearshore.infolayers.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

import nearshore.infolayers.backend.entities.User;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EntityScan(basePackageClasses=User.class)
public class BackendInfoLayersApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendInfoLayersApplication.class, args);
	}

}
