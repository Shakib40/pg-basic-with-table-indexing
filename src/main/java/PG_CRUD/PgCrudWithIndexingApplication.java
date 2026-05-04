package PG_CRUD;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PgCrudWithIndexingApplication {

	public static void main(String[] args) {
		SpringApplication.run(PgCrudWithIndexingApplication.class, args);
	}

}
