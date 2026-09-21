# Etapa de construcción
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo de configuración de Maven
COPY pom.xml .

# Descarga las dependencias necesarias (esto se cacheará si no hay cambios en pom.xml)
RUN mvn dependency:go-offline

# Copia el resto del código fuente
COPY src ./src

# Compila el proyecto y empaqueta en un archivo JAR
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jdk

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto en el que la aplicación se ejecutará
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
