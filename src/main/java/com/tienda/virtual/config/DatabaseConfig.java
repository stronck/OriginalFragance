/*
 * DOCUMENTACIÓN DEL ARCHIVO: DatabaseConfig.java
 *
 * Este bloque explica el propósito general del archivo sin modificar su lógica.
 * Las clases, métodos, atributos, anotaciones y llamadas que siguen pertenecen
 * a la implementación funcional de la aplicación y se conservan exactamente.
 * La información detallada se centra en qué responsabilidad cumple cada parte,
 * cómo participa en el flujo de la tienda y qué relación tiene con las demás capas.
 */
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
    // Método: crea y configura el origen de datos PostgreSQL.
    public HikariDataSource dataSource(Environment environment) {
        String configuredUrl = firstNonBlank(
                environment.getProperty("SPRING_DATASOURCE_URL"),
                environment.getProperty("spring.datasource.url"),
                environment.getProperty("DATABASE_URL")
        );

        // Permite configurar Rollout mediante variables separadas y cortas.
        if (configuredUrl == null) {
            String host = environment.getProperty("DB_HOST");
            String port = firstNonBlank(environment.getProperty("DB_PORT"), "5432");
            String database = environment.getProperty("DB_NAME");

            if (host != null && database != null) {
                configuredUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database;
            }
        }

        if (configuredUrl == null) {
            throw new IllegalStateException(
                    "No se encontró una configuración PostgreSQL válida."
            );
        }

        ConnectionInfo connection = parseConnection(configuredUrl);

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(connection.jdbcUrl());
        dataSource.setDriverClassName("org.postgresql.Driver");

        String username = firstNonBlank(
                environment.getProperty("SPRING_DATASOURCE_USERNAME"),
                environment.getProperty("spring.datasource.username"),
                environment.getProperty("DB_USER"),
                connection.username()
        );

        String password = firstNonBlank(
                environment.getProperty("SPRING_DATASOURCE_PASSWORD"),
                environment.getProperty("spring.datasource.password"),
                environment.getProperty("DB_PASSWORD"),
                connection.password()
        );

        if (username != null) {
            dataSource.setUsername(username);
        }
        if (password != null) {
            dataSource.setPassword(password);
        }

        return dataSource;
    }

    // Método: convierte una URL PostgreSQL a la información de conexión que utiliza HikariCP.
    private ConnectionInfo parseConnection(String url) {
        if (url.startsWith("jdbc:postgresql://")) {
            return new ConnectionInfo(url, null, null);
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

        String username = null;
        String password = null;

        if (uri.getUserInfo() != null && uri.getUserInfo().contains(":")) {
            String[] credentials = uri.getUserInfo().split(":", 2);
            username = decode(credentials[0]);
            password = decode(credentials[1]);
        }

        return new ConnectionInfo(jdbc.toString(), username, password);
    }

    // Método: decodifica componentes de usuario o contraseña presentes en la URL.
    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    // Método: selecciona el primer valor configurado que no esté vacío.
    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private record ConnectionInfo(String jdbcUrl, String username, String password) {
    }
}
