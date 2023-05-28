package gachon.bridge.exerciseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ExerciseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExerciseServiceApplication.class, args);
	}

}
