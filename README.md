# Unidad 2 - Evidencia de Aprendizaje 1

Aplicación Java SE (Swing + JDBC) para realizar operaciones CRUD sobre la tabla `customer` definida en `actividad-1-create-table.sql`. El proyecto reutiliza el esquema creado en la unidad anterior y se conecta a PostgreSQL mediante JDBC puro.

## Contenido del repositorio

- `actividad-1-create-table.sql`: definición completa de la base de datos y datos semilla.
- `pom.xml`: configuración Maven (Java 17).
- `src/main/java`: código fuente del proyecto.
  - `com.saborpaisa.crud.config.DatabaseConfig`: lectura de `db.properties`.
  - `com.saborpaisa.crud.dao.CustomerDao`: operaciones CRUD JDBC.
  - `com.saborpaisa.crud.service.CustomerService`: validaciones básicas.
  - `com.saborpaisa.crud.ui.CustomerForm`: formulario Swing con botones INSERT/UPDATE/DELETE/SELECT.
  - `com.saborpaisa.crud.App`: punto de entrada.
- `src/main/resources/db.properties`: propiedades de conexión (editar según tu entorno).

## Requisitos previos

- Java 17+
- Maven 3.9+
- PostgreSQL con la BD creada a partir de `actividad-1-create-table.sql`

## Configuración

1. Crea la base de datos (si aún no existe):
   ```bash
   psql -U postgres -f actividad-1-create-table.sql
   ```
2. Actualiza `src/main/resources/db.properties` con la URL, usuario y contraseña correctos.

## Ejecución

```bash
mvn clean package
java -jar target/java-se-crud-1.0-SNAPSHOT-jar-with-dependencies.jar
```

La ventana principal (`CustomerForm`) permite:
- `Insertar`: crea un registro con los datos del formulario.
- `Actualizar`: requiere seleccionar un cliente en la tabla o cargarlo con `Buscar ID`.
- `Eliminar`: elimina por ID (pide confirmación).
- `Buscar ID`: SELECT puntual y rellenado del formulario.

La tabla se alimenta directamente desde la BD, garantizando que cada operación se refleja inmediatamente.

## Backup de la base de datos (20 pts)

La evidencia de respaldo solicitada es el propio archivo `actividad-1-create-table.sql`. No se requiere generar dumps adicionales: basta con adjuntar este script en la entrega.

## Video explicativo

Guía sugerida para el video:
1. Mostrar el script SQL utilizado para crear la BD.
2. Ejecutar el formulario y demostrar las operaciones CRUD.
3. Enseñar brevemente el código de `CustomerDao` y `CustomerForm`.
4. Cerrar mostrando el archivo de backup generado.

Con esto se cubren los requisitos funcionales y de documentación solicitados para la actividad. ¡Éxitos en la presentación!

