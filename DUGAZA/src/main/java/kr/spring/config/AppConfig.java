package kr.spring.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import kr.spring.api.config.ApiConfig;
import kr.spring.websocket.SocketHandler;

/**
 * 애플리케이션 전체 설정을 관리하는 설정 클래스
 */
@Configuration
@EnableWebSecurity
@MapperScan(basePackages = {
    "kr.spring.api.mapper",
    "kr.spring.**.dao",
        "kr.spring.**.dao"
})
@EnableConfigurationProperties({ApiConfig.class})
public class AppConfig implements WebMvcConfigurer, WebSocketConfigurer {

    @Value("${spring.mail.username}")
    private String google_mail_url;
    @Value("${spring.mail.password}")
    private String google_mail_password;
    @Autowired
    private ApplicationContext applicationContext;

    // Oracle JDBC 드라이버 로드
    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 웹소켓 셋팅
    @Bean
    public JavaMailSenderImpl javaMailSenderImpl() {
        Properties prop = new Properties();
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.debug", "true");
        
        JavaMailSenderImpl javaMail = new JavaMailSenderImpl();
        javaMail.setHost("smtp.gmail.com");
        javaMail.setPort(587);
        javaMail.setDefaultEncoding("utf-8");
        javaMail.setUsername(google_mail_url);
        javaMail.setPassword(google_mail_password);
        javaMail.setJavaMailProperties(prop);
        
        return javaMail;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketHandler(), "message-ws").setAllowedOrigins("*");
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        
        // 매퍼 XML 파일 위치 설정
        Resource[] mapperLocations = new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/**/*.xml");
        sessionFactory.setMapperLocations(mapperLocations);
        
        // 타입 별칭 패키지 설정
        sessionFactory.setTypeAliasesPackage("kr.spring.api.dto,"
                + "kr.spring.**.vo,"
                + "kr.spring.**.dto");
        
        return sessionFactory.getObject();
    }
    //시발
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/upload/**")
                .addResourceLocations("classpath:/static/assets/upload/");
    }

}
