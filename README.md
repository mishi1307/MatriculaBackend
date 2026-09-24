# MatriculaBackend

API REST para la gestión académica de carreras, cursos, estudiantes y matrículas, desarrollada con Spring Boot, Java 21 y Oracle Database.

## Requisitos

- JDK 21.
- Oracle Database accesible en `localhost:1522`, servicio `xepdb1`.
- Maven (se incluye el Maven Wrapper del proyecto).

## Configuración de Oracle

La aplicación usa por defecto el usuario `MATRICULADB` y la contraseña `1234567`. Para crear el usuario, ejecuta `database/crear_usuario.sql` conectado al servicio `xepdb1` con un usuario administrador de Oracle.

La configuración de desarrollo se encuentra en `src/main/resources/application-dev.yaml`. Puedes reemplazar las credenciales por variables de entorno:

```powershell
$env:DB_USERNAME = "MATRICULADB"
$env:DB_PASSWORD = "1234567"
```

Hibernate actualiza el esquema automáticamente al iniciar (`ddl-auto: update`). Para cargar los datos de ejemplo, ejecuta `datos_semilla.sql` conectado como `MATRICULADB`.

## Ejecutar

En Windows, desde la raíz del proyecto:

```powershell
.\mvnw.cmd spring-boot:run
```

La API inicia en `http://localhost:8080`.

## Documentación y comprobación

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Estado de la aplicación y conexión a Oracle: `GET http://localhost:8080/api/v1/health`
- Colección de solicitudes para Postman: `postman/EduAndes.postman_collection.json`

## Recursos principales

- Carreras: `/api/v1/carreras`
- Cursos: `/api/v1/cursos`
- Estudiantes: `/api/v1/estudiantes`
- Matrículas: `/api/v1/matriculas`

La matrícula se registra mediante `POST /api/v1/matriculas` y se anula mediante `PATCH /api/v1/matriculas/{id}/anular`.
