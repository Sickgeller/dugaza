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
public class AppConfig implements WebMvcConfigurer{

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

}
