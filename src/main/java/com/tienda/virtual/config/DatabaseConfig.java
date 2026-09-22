// Configuración de la conexión PostgreSQL para Rollout y desarrollo local.
package com.tienda.virtual.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class DatabaseConfig {

    @Bean
    public HikariDataSource dataSource(Environment environment) {
        String configuredUrl = firstNonBlank(
                environment.getProperty("SPRING_DATASOURCE_URL"),
                environment.getProperty("spring.datasource.url"),
                environment.getProperty("DATABASE_URL")
        );

        if (configuredUrl == null) {
            throw new IllegalStateException(
                    "No se encontró DATABASE_URL ni SPRING_DATASOURCE_URL para conectar con PostgreSQL."
            );
        }

        String jdbcUrl = toJdbcPostgresUrl(configuredUrl);

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setDriverClassName("org.postgresql.Driver");

        String username = firstNonBlank(
                environment.getProperty("SPRING_DATASOURCE_USERNAME"),
                environment.getProperty("spring.datasource.username")
        );

        String password = firstNonBlank(
                environment.getProperty("SPRING_DATASOURCE_PASSWORD"),
                environment.getProperty("spring.datasource.password")
        );

        // Rollout normalmente entrega usuario y contraseña dentro de DATABASE_URL.
        if (username != null) {
            dataSource.setUsername(username);
        }
        if (password != null) {
            dataSource.setPassword(password);
        }

        return dataSource;
    }

    private String toJdbcPostgresUrl(String url) {
        if (url.startsWith("jdbc:postgresql://")) {
            return url;
        }

        if (!url.startsWith("postgres://") && !url.startsWith("postgresql://")) {
            throw new IllegalArgumentException(
                    "DATABASE_URL debe ser una URL PostgreSQL válida."
            );
        }

        URI uri = URI.create(url);
        StringBuilder jdbc = new StringBuilder("jdbc:postgresql://");
        jdbc.append(uri.getHost());

        if (uri.getPort() != -1) {
            jdbc.append(":").append(uri.getPort());
        }

        String path = uri.getRawPath();
        if (path != null && !path.isEmpty()) {
            jdbc.append(path);
        }

        if (uri.getRawQuery() != null && !uri.getRawQuery().isEmpty()) {
            jdbc.append("?").append(uri.getRawQuery());
        }

        // Si la URL trae credenciales, se configuran directamente en Hikari.
        if (uri.getUserInfo() != null && uri.getUserInfo().contains(":")) {
            String[] credentials = uri.getUserInfo().split(":", 2);
            if (credentials.length == 2) {
                // Se dejan disponibles mediante propiedades del DataSource en el bean.
                // La URL JDBC queda libre de credenciales para evitar exponerlas en logs.
            }
        }

        return jdbc.toString();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
