package com.example.restapifiletransfer.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import javax.sql.DataSource;

@Configuration
public class AppConfig extends WebSecurityConfigurerAdapter {

    @Value("${rest.api.file.transfer.aws.s3.region}")
    private String region;

    @Value("${rest.api.file.transfer.aws.rds.url}")
    private String url;

    @Value("${rest.api.file.transfer.aws.rds.driver-class-name}")
    private String driverClassName;

    @Value("${rest.api.file.transfer.aws.rds.username}")
    private String username;

    @Value("${rest.api.file.transfer.aws.rds.password}")
    private String password;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .oauth2ResourceServer()
                    .jwt();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client
                .builder()
                .region(Region.of(region))
                .build();
    }

    @Bean
    public HikariDataSource dataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(url);
        hikariDataSource.setDriverClassName(driverClassName);
        hikariDataSource.setUsername(username);
        hikariDataSource.setPassword(password);

        return hikariDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public RandomStringGenerator randomStringGenerator() {
        return new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .build();
    }


}
