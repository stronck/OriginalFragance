# Etapa de construcción
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo de configuración de Maven
COPY pom.xml .

# Descarga las dependencias necesarias
RUN mvn dependency:go-offline

# Copia el código fuente
COPY src ./src

# Compila el proyecto y empaqueta el archivo JAR
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jdk

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Rollout utilizará el puerto 8080 del contenedor
EXPOSE 8080

# Ejecuta la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
