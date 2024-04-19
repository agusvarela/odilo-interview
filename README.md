# Odilo Interview Challenge

![](https://img.shields.io/badge/build-success-brightgreen.svg)

## Autor
- [Agustin Varela](https://www.linkedin.com/in/agus-varela10)

## Stack

![](https://img.shields.io/badge/Java_17-✓-blue.svg)
![](https://img.shields.io/badge/Spring_boot-✓-blue.svg)
![](https://img.shields.io/badge/Spring_security-✓-blue.svg)
![](https://img.shields.io/badge/Postgres-✓-blue.svg)
![](https://img.shields.io/badge/Redis-✓-blue.svg)
![](https://img.shields.io/badge/Kafka-✓-blue.svg)
![](https://img.shields.io/badge/Maven-✓-blue.svg)
![](https://img.shields.io/badge/Postman-✓-blue.svg)
![](https://img.shields.io/badge/OpenApi_3.0-✓-blue.svg)
![](https://img.shields.io/badge/Docker-✓-blue.svg)

-------------------

## Estructura de la aplicación

La aplicación esta realizada con una arquitectura de MVC, posee controllers, services y components, como asi tambien repositories para la bd.

Es una APIRest que disponibiliza recursos tanto para usuarios `no autenticados`, como para usuarios con `ROLE_USER` y con `ROLE_ADMIN`.

Asegúrate de tener instalados [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html), [Maven](https://maven.apache.org/install.html) and [Docker](https://docs.docker.com/get-docker/) para poder correr la aplicación.

-------------------

## Configuración de Seguridad

El proyecto utiliza Spring Security para asegurar los endpoints de administrador (admin) y usuario (user). 
Se ha configurado una clase `SecurityConfig` para definir reglas de autorización y autenticación específicas para cada tipo de usuario.

* Los endpoints de administrador requieren el rol `ADMIN` para acceder.
* Los endpoints de usuario requieren el rol `USER` para acceder.

-------------------

## Autenticación JWT

El proyecto utiliza un filtro de autenticación personalizado `JwtAuthenticationFilter` para manejar la autenticación basada en JWT (JSON Web Token). 
Este filtro intercepta las solicitudes entrantes y verifica la validez de los tokens JWT proporcionados en el encabezado de autorización `Bearer`. 
Si el token es válido, se autentica al usuario y se le concede acceso al recurso solicitado.

A destacar que en este filter, se definio un `shouldNotFilter` para no filtrar por autenticacion aquellas url que no lo necesitas y tienen acceso total a usuarios no autenticados.

El proceso de autenticación con JWT incluye:

* **Generación de Tokens JWT**: Cuando un usuario inicia sesión correctamente, se genera un token JWT que contiene información de autenticación y roles.
* **Validación del Token JWT**: Cada solicitud entrante se verifica para determinar si contiene un token JWT válido en el encabezado de autorización.
* **Autenticación del Usuario**: Si el token es válido (es decir, no esta expirado, que fue generado internamente por la aplicacion y no por un tercero), el filtro de autenticación verifica la identidad del usuario y otorga acceso al recurso solicitado según sus roles.

-------------------

## Gestión de Caché con Redis

Para mejorar el rendimiento y reducir la carga en la base de datos, el proyecto utiliza `Redis` como caché para almacenar los tokens JWT y los detalles de los usuarios.
Esto ayuda a evitar posibles sobrecargas de la base de datos al reducir la cantidad de consultas necesarias.

* **Tokens JWT**: Los tokens JWT generados se almacenan en Redis con un tiempo de expiración de 5 minutos, lo que coincide con el tiempo de expiración del token JWT.
* **Detalles de Usuarios**: Los detalles de los usuarios, se almacenan en Redis para acceder rápidamente sin tener que pasar por la base de datos directamente, tanto busquedas por id como por username (puesto que estas son mas recurrentes al querer autenticar al user).

-------------------

## Base de Datos PostgreSQL

El proyecto utiliza una base de datos PostgreSQL para almacenar datos de usuarios y roles.

Se inicializa con un usuario administrador que tiene el rol de "ADMIN" para proporcionar acceso inicial a las funcionalidades administrativas de la aplicación.

-------------------

## JOB de Carga de Usuarios Administradores en Redis

Al iniciar la aplicación, se ejecuta un job que carga los usuarios administradores en la caché de Redis.
Esto se debe a que los usuarios admin son inicializados a través del archivo `data.sql` que realiza un insert inicial en la base de datos cuando se inicializa la app.

-------------------

## Kafka para Suscripción a Eventos

Se implementó un mecanismo de suscripción utilizando Kafka para que diferentes microservicios clientes de este servicio de registro y login, pertenecientes a la aplicación ACME, puedan recibir updates de todas las operaciones realizadas por un usuario (registros, login, eliminación de usuario, etc.).

* **Topic `user_events`**: Se lo utiliza para enviar eventos relacionados con operaciones de usuarios, como registros, logins y eliminación de usuarios. A modo de ejemplo solo se propaga estas 3 operaciones importante del rol user _(dandose a entender que se pueden propagar todas las operaciones que realiza el user si se desea)_ y se envian bajo un mismo topico, pero diferenciados por keys como `register`, `login`, `delete` esto para evitar tener que crear multiples topicos por el momento y tener diferenciadas las operaciones, pero se puede separar cada operacion en un topico diferente.
* **Kafka Listener**: Se configuró un Kafka listener para escuchar los eventos en el topic `user_events`. Esto permite tener un registro de que Kafka está funcionando correctamente y se loguea cada evento escuchado.

-------------------

## Endpoints Disponibles

A continuación se detallan los endpoints disponibles en la aplicación, separados por permisos:

##### Sin necesidad de permisos
* `POST /odilo/api/auth/register`: Endpoint para registrar un nuevo usuario. Se verifica que el `username` o `email` no se encuentren ya registrados, como asi tambien que la `password` posea entre 6 y 20 caracteres. Y no menos importante, se verifica que sea mayor de 18 años.
* `POST /odilo/api/auth/login`: Endpoint para iniciar sesión tanto con rol admin o user y obtener un token JWT.

##### Con permisos de USER (Estas url necesitan un Authorization Bearer)
* `GET /odilo/api/users/{userId}`: Endpoint para obtener información de un usuario específico (por motivos de seguridad decidi que solo pueda consultar su propio id que se chequea contra el JWT para que no pueda acceder a informacion de otros users tal como la de un admin por ejemplo, aunque se podria filtrar la busqueda por rol tambien, pero use el sentido comun cuando un user consulta su info personal).
* `DELETE /odilo/api/users/{userId}`: Endpoint para eliminar un user, que en realidad es a si mismo, esto se verifica que el id sea el mismo que posee el username proveniente del JWT.

##### Con permisos de ADMIN (Estas url necesitan un Authorization Bearer)

Para users admin, se puede hacer login con `username: admin` y `password: adminpass`

* `GET /odilo/api/admin/users`: Endpoint para obtener la lista de todos los usuarios.
* `PUT /odilo/api/admin/users/{userId}`: Endpoint para actualizar la pass de un user en especifico. En este endpoint se verifica que la nueva password no sea la misma que la actual.

Para probar estos endpoints, se recomienda:

* Utilizar el Postman Collection proporcionado para realizar solicitudes a los endpoints.
* Iniciar la aplicación y revisar la documentación `Swagger` para obtener información detallada sobre los endpoints y cómo utilizarlos. (Esto se detalla a continuación)

-------------------

## Ejecutar esta API localmente

1. Asegúrate de tener instalados [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html), [Maven](https://maven.apache.org/install.html) and [Docker](https://docs.docker.com/get-docker/).

2. Clona este repositorio

```
git clone https://github.com/agusvarela/odilo-interview.git
```

3. Navega hasta la carpeta del proyecto

```
cd odilo-interview
```

4. Instala las dependencias de Maven y buildea la app

```
mvn clean verify
```

5. En la carpeta del proyecto, ejecuta el proyecto en contenedores Docker usando docker-compose. Este comando descargará y ejecutará las imágenes necesarias desde DockerHub (Asegúrate de que el puerto 8080 no esté en uso)

```
docker-compose up
```

----------

## Documentación

Una vez que la aplicación esté en funcionamiento, puedes encontrar toda la información para probarla a través de Swagger:
http://localhost:8080/swagger-ui/index.html

La documentación de OpenApi:
* http://localhost:8080/api-docs (formato json)
* http://localhost:8080/api-docs.yaml (formato yaml)
