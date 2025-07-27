package kr.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class SpringPageApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringPageApplication.class, args);
	}
}