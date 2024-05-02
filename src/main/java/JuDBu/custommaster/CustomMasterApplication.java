package JuDBu.custommaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CustomMasterApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomMasterApplication.class, args);
	}

}
