package com.asl.pms.util;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Configuration
@PropertySource(value = {"classpath:application.properties"}, ignoreResourceNotFound = false)
public class DBConfigService implements EnvironmentAware {

    private static Environment env;

    public static String getUrl() {
        return env.getProperty("spring.datasource.url");
    }

    public static String getDriverClass() {
        return env.getProperty("spring.datasource.driver.class");
    }

    public static String getUserName() {
        return env.getProperty("spring.datasource.username");
    }

    public static String getPassword() {
        return env.getProperty("spring.datasource.password");
    }

    public static String getMysqlBinPath() {
        return env.getProperty("mysql.server.bin-path");
    }

    public static String getBackupPath() {
        return env.getProperty("backup-file-path");
    }

    public static String getProperty(String key) {
        return env.getProperty(key);
    }

    @Override
    public void setEnvironment(Environment env) {
        DBConfigService.env = env;
    }

    /*@Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }*/
}
