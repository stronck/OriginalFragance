/*
 * DOCUMENTACIÓN DEL ARCHIVO: VirtualApplication.java
 *
 * Este bloque explica el propósito general del archivo sin modificar su lógica.
 * Las clases, métodos, atributos, anotaciones y llamadas que siguen pertenecen
 * a la implementación funcional de la aplicación y se conservan exactamente.
 * La información detallada se centra en qué responsabilidad cumple cada parte,
 * cómo participa en el flujo de la tienda y qué relación tiene con las demás capas.
 */
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
