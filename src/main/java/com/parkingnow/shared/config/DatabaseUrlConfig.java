package com.parkingnow.shared.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Normalizes DATABASE_URL for Render + Supabase:
 * - Converts postgres:// / postgresql:// → jdbc:postgresql://
 * - Adds sslmode=require (Supabase requires SSL)
 * - Extracts user/password from URL so HikariCP gets them correctly
 */
public class DatabaseUrlConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        ConfigurableEnvironment env = context.getEnvironment();
        String rawUrl = env.getProperty("DATABASE_URL");
        if (rawUrl == null || rawUrl.isBlank()) return;

        Map<String, Object> props = new HashMap<>();

        try {
            // Normalize scheme for URI parsing
            String uriStr = rawUrl
                .replace("postgres://", "postgresql://")
                .replace("jdbc:postgresql://", "postgresql://");

            URI uri = new URI(uriStr);

            // Extract user info (user:password)
            String userInfo = uri.getUserInfo();
            if (userInfo != null) {
                String[] parts = userInfo.split(":", 2);
                String user = parts[0];
                String pass = parts.length > 1 ? parts[1] : "";

                // Only set if not explicitly provided via env vars
                if (env.getProperty("DATABASE_USER") == null) {
                    props.put("spring.datasource.username", user);
                }
                if (env.getProperty("DATABASE_PASSWORD") == null) {
                    props.put("spring.datasource.password", pass);
                }
            }

            // Build clean JDBC URL without embedded credentials
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String path = uri.getPath();
            String query = uri.getQuery() != null ? uri.getQuery() : "";

            if (!query.contains("sslmode")) {
                query = query.isEmpty() ? "sslmode=require" : query + "&sslmode=require";
            }

            String jdbcUrl = String.format("jdbc:postgresql://%s:%d%s?%s", host, port, path, query);
            props.put("DATABASE_URL", jdbcUrl);

        } catch (Exception e) {
            // Fallback: simple string replacement
            String jdbcUrl = rawUrl
                .replace("postgres://", "jdbc:postgresql://")
                .replace("postgresql://", "jdbc:postgresql://");
            if (!jdbcUrl.contains("sslmode")) {
                jdbcUrl = jdbcUrl.contains("?") ? jdbcUrl + "&sslmode=require" : jdbcUrl + "?sslmode=require";
            }
            props.put("DATABASE_URL", jdbcUrl);
        }

        env.getPropertySources().addFirst(new MapPropertySource("render-db-url-fix", props));
    }
}
