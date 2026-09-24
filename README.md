# OriginalFragance

Tienda virtual de perfumes desarrollada con **Spring Boot 3.4.2**, **Java 17**, **PostgreSQL**, **Spring Data JPA**, HTML, CSS y JavaScript.

## Descripción

OriginalFragance es una aplicación web de comercio electrónico para consultar productos, registrarse, iniciar sesión, administrar un carrito y crear pedidos. También permite consultar los pedidos y generar el comprobante de compra en PDF.

La aplicación está organizada en una arquitectura por capas:

- **Controller:** recibe las solicitudes HTTP y controla el acceso a los endpoints.
- **Service:** contiene la lógica de negocio.
- **Repository:** comunica la aplicación con PostgreSQL mediante Spring Data.
- **Model:** representa las entidades persistidas.
- **Frontend:** páginas HTML, CSS y JavaScript servidas directamente por Spring Boot.
- **Resources:** configuración de Spring Boot y datos iniciales de la base de datos.

## Tecnologías

- Java 17
- Spring Boot 3.4.2
- Spring Web
- Spring Data JPA
- PostgreSQL
- BCrypt para las contraseñas
- iTextPDF 5.5.13.2 para generar facturas
- Lombok
- Bootstrap 5.3.3
- Font Awesome
- Maven

## Funcionalidades

### Cliente

- Visualización del catálogo de productos.
- Registro de usuarios.
- Inicio y cierre de sesión.
- Actualización del perfil.
- Carrito asociado a la sesión.
- Agregar y retirar productos del carrito.
- Creación de pedidos.
- Consulta de los pedidos propios.
- Generación de comprobantes de compra en PDF.
- Información de medios de pago y transportadoras.
- Consulta de términos y condiciones.
- Consulta de política de tratamiento de datos.

### Administrador

Los usuarios con rol `admin` pueden acceder al panel administrativo para:

- Consultar usuarios.
- Consultar todos los pedidos.
- Marcar un pedido como pago exitoso.
- Consultar/generar facturas de los pedidos.
- Visualizar la información administrativa mediante tablas adaptadas a pantallas pequeñas.

El registro público crea usuarios con rol `user`. El rol administrativo se conserva en los usuarios que ya están configurados como administradores.

## Estructura principal

```text
OriginalFragance/
├── src/
│   ├── main/
│   │   ├── java/com/tienda/virtual/
│   │   │   ├── config/
│   │   │   │   └── DatabaseConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── CarritoController.java
│   │   │   │   ├── PedidoController.java
│   │   │   │   ├── ProductoController.java
│   │   │   │   └── UsuarioController.java
│   │   │   ├── model/
│   │   │   │   ├── Pedido.java
│   │   │   │   ├── Producto.java
│   │   │   │   └── Usuario.java
│   │   │   ├── repository/
│   │   │   │   ├── PedidoRepository.java
│   │   │   │   ├── ProductoRepository.java
│   │   │   │   └── UsuarioRepository.java
│   │   │   ├── service/
│   │   │   │   ├── CarritoService.java
│   │   │   │   ├── CarritoServiceImpl.java
│   │   │   │   ├── PedidoService.java
│   │   │   │   ├── PedidoServiceImpl.java
│   │   │   │   ├── UsuarioService.java
│   │   │   │   └── UsuarioServiceImpl.java
│   │   │   └── VirtualApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── data.sql
│   │       └── static/
│   │           ├── index.html
│   │           ├── iniciarsesion.html
│   │           ├── registrarse.html
│   │           ├── carrito.html
│   │           ├── user.html
│   │           ├── admin.html
│   │           ├── politica-datos.html
│   │           ├── terminos-condiciones.html
│   │           ├── productocarrito.js
│   │           ├── usuarios.js
│   │           ├── styles.css
│   │           └── img/
│   └── test/
│       └── java/com/tienda/virtual/
│           └── VirtualApplicationTests.java
├── sql/
│   └── ecommerce.sql
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── mvnw
└── mvnw.cmd
```

## Base de datos

La aplicación utiliza PostgreSQL.

La configuración de conexión se obtiene mediante variables de entorno. La clase `DatabaseConfig` acepta una URL PostgreSQL mediante `DATABASE_URL` o `SPRING_DATASOURCE_URL`, además de usuario y contraseña cuando se proporcionan por separado.

Hibernate utiliza:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Por lo tanto, las tablas se crean o actualizan de acuerdo con las entidades Java.

### Datos iniciales

`src/main/resources/data.sql` contiene la estructura SQL complementaria y los datos iniciales de la tienda.

`sql/ecommerce.sql` mantiene el mismo contenido para disponer de una copia del script de base de datos.

Los dos archivos se encuentran sincronizados.

La tabla `usuario` utiliza el campo `rol` para distinguir entre:

- `user`
- `admin`

La inserción inicial del administrador comprueba la existencia del mismo nombre de usuario antes de insertarlo nuevamente.

## Sesiones y autenticación

La aplicación utiliza `HttpSession` para mantener la sesión del usuario.

El flujo principal es:

1. El usuario envía sus credenciales.
2. El backend busca el usuario por nombre de usuario o correo.
3. BCrypt compara la contraseña recibida con la contraseña almacenada.
4. Si las credenciales son correctas, se guardan los datos necesarios en la sesión.
5. Los endpoints protegidos comprueban la sesión antes de realizar operaciones.
6. Los endpoints administrativos verifican que el rol sea `admin`.

Las contraseñas almacenadas utilizan BCrypt y no se devuelven en las respuestas JSON normales del usuario.

## Endpoints principales

### Productos

```text
GET /api/productos
```

Devuelve los productos disponibles.

### Usuarios

```text
POST   /api/usuarios/registrar
POST   /api/usuarios/iniciar-sesion
GET    /api/usuarios/sesion
POST   /api/usuarios/cerrar-sesion
GET    /api/usuarios
PUT    /api/usuarios
DELETE /api/usuarios/{id}
```

### Carrito

```text
POST   /api/carrito/agregar
GET    /api/carrito
DELETE /api/carrito/{indice}
```

### Pedidos

```text
POST /api/pedidos
GET  /api/pedidos/mis-pedidos
GET  /api/pedidos/{id}/factura
GET  /api/pedidos
POST /api/pedidos/{id}/pago-exitoso
```

Los endpoints administrativos comprueban el rol `admin`.

## Generación de facturas

Los pedidos almacenan el detalle de compra y la información necesaria para generar el comprobante.

El servicio de pedidos reconstruye los productos del pedido y utiliza `CarritoServiceImpl` con iTextPDF para generar el PDF.

La factura contiene información del comprador, pedido, productos, precios, total y datos de contacto de la tienda.

## Configuración del puerto

El servidor utiliza:

```properties
server.port=${PORT:8080}
```

Esto permite utilizar el puerto proporcionado por el entorno mediante la variable `PORT`; si no existe, se utiliza el puerto `8080`.

## Ejecución local

Con Maven:

```bash
./mvnw spring-boot:run
```

En Windows:

```cmd
mvnw.cmd spring-boot:run
```

También se puede construir el proyecto:

```bash
./mvnw clean package -DskipTests
```

y ejecutar el JAR generado:

```bash
java -jar target/virtual-0.0.1-SNAPSHOT.jar
```

La aplicación quedará disponible en:

```text
http://localhost:8080
```

si no se define otro valor mediante `PORT`.


## Docker

El proyecto incluye un `Dockerfile` con dos etapas:

1. Una etapa Maven que descarga dependencias, compila y empaqueta la aplicación.
2. Una etapa basada en Eclipse Temurin 17 que ejecuta el JAR.

También existe `docker-compose.yml` para el entorno local con PostgreSQL.

## Comentarios del código

Los archivos de la aplicación están documentados con comentarios explicativos para facilitar su lectura, estudio y mantenimiento.

Los comentarios abarcan:

- Configuración y arranque.
- Conexión con PostgreSQL.
- Controladores y endpoints.
- Entidades y modelos.
- Repositorios.
- Servicios y lógica de negocio.
- Autenticación y sesiones.
- Generación de facturas.
- Páginas HTML.
- JavaScript del frontend.
- Hoja de estilos.
- Scripts SQL.
- Configuración Maven y Docker.

Los comentarios no forman parte de la lógica de ejecución de la aplicación.
