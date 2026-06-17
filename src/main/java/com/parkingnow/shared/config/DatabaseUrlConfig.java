package com.parkingnow.shared.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts Render's postgres:// URL format to JDBC-compatible jdbc:postgresql://.
 */
public class DatabaseUrlConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        ConfigurableEnvironment env = context.getEnvironment();
        String dbUrl = env.getProperty("DATABASE_URL");
        if (dbUrl != null && dbUrl.startsWith("postgres://")) {
            String jdbcUrl = dbUrl.replace("postgres://", "jdbc:postgresql://");
            Map<String, Object> props = new HashMap<>();
            props.put("DATABASE_URL", jdbcUrl);
            env.getPropertySources().addFirst(new MapPropertySource("render-db-url-fix", props));
        }
    }
}
