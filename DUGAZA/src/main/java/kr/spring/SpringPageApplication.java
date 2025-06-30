package kr.spring;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableAspectJAutoProxy
@MapperScan(basePackages = {"kr.spring.api.mapper", "kr.spring.member.dao", "kr.spring.faq.dao"})
public class SpringPageApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringPageApplication.class, args);
	}

}
