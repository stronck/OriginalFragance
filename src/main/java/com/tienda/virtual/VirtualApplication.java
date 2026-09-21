// Clase principal que inicia la aplicación Spring Boot.
package com.tienda.virtual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Autor: Camilo Tibatá Salguero
 * Proyecto: E-commerce
 * Descripción: spring boot.
 */

@SpringBootApplication
public class VirtualApplication {

	public static void main(String[] args) {
		SpringApplication.run(VirtualApplication.class, args);
	}

}
