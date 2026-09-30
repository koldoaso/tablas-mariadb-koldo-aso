# Tablas MariaDB

Aplicación de escritorio desarrollada con **JavaFX** que permite gestionar una tabla de personas almacenada en una base de datos **MariaDB**.

La aplicación permite:

- Añadir personas.
- Mostrar las personas almacenadas en MariaDB.
- Eliminar varias personas mediante borrado lógico.
- Restaurar las personas eliminadas.
- Mantener los datos de forma persistente mediante MariaDB.
- Registrar la actividad y los errores de la aplicación en `application.log`.
- Detectar automáticamente el idioma del sistema.
- Mostrar la interfaz en español o inglés mediante internacionalización con `ResourceBundle`.
- Mostrar fechas utilizando el formato correspondiente a la configuración regional del sistema.

La base de datos se ejecuta mediante **Docker Compose**, permitiendo crear automáticamente el entorno necesario para ejecutar la aplicación.

## Tecnologías utilizadas

- Java
- JavaFX
- Maven
- JDBC
- MariaDB
- MariaDB Connector/J
- Docker
- Docker Compose
- Java ResourceBundle
- Java Logging API

## Requisitos

Para ejecutar el proyecto es necesario tener instalado:

- Java / JDK
- Maven
- Docker Desktop

También es necesario que Docker Desktop esté iniciado antes de levantar la base de datos.

## Configuración inicial

El proyecto utiliza un archivo `.env` para configurar MariaDB.

Existe un archivo:

```text
.env.example
```

que sirve como plantilla.

Se debe crear un archivo `.env` en la raíz del proyecto con una configuración similar a:

```env
DB_NAME=tables_mariadb
DB_USER=tables_user
DB_PASSWORD=change_this_password

MARIADB_ROOT_PASSWORD=change_this_root_password
```

El archivo `.env` no debe subirse al repositorio, ya que contiene credenciales.

Las credenciales reales se encuentran excluidas mediante `.gitignore`.

## Iniciar la base de datos

Desde la carpeta raíz del proyecto ejecutar:

```powershell
docker compose up -d
```

Docker Compose iniciará automáticamente el contenedor de MariaDB.

Durante la primera inicialización:

- Se crea la base de datos `tables_mariadb`.
- Se crea el usuario configurado en `.env`.
- Se ejecuta el script SQL situado en:

```text
docker/init/init.sql
```

- Se crea la tabla `person`.
- Se crea un volumen Docker para mantener los datos de forma persistente.

Se puede comprobar el estado del contenedor mediante:

```powershell
docker compose ps
```

Cuando MariaDB esté correctamente iniciado, el contenedor debería aparecer en estado `healthy`.

## Acceder manualmente a MariaDB

Para comprobar manualmente la base de datos se puede ejecutar:

```powershell
docker exec -it tables-mariadb mariadb -u tables_user -p tables_mariadb
```

MariaDB solicitará la contraseña configurada en el archivo `.env`.

Una vez dentro se pueden consultar las tablas mediante:

```sql
SHOW TABLES;
```

Por ejemplo:

```text
+--------------------------+
| Tables_in_tables_mariadb |
+--------------------------+
| person                   |
+--------------------------+
```

Para salir:

```sql
exit;
```

## Configurar la conexión de JavaFX

La aplicación no contiene credenciales directamente en el código fuente.

La conexión se configura mediante variables de entorno.

En PowerShell:

```powershell
$env:DB_URL="jdbc:mariadb://localhost:3306/tables_mariadb"
$env:DB_USER="tables_user"
$env:DB_PASSWORD="change_this_password"
```

La contraseña debe coincidir con la contraseña utilizada para crear el usuario de MariaDB.

La aplicación obtiene estas variables mediante:

```java
System.getenv("DB_URL");
System.getenv("DB_USER");
System.getenv("DB_PASSWORD");
```

Esto evita almacenar credenciales sensibles dentro del repositorio.

## Ejecutar la aplicación

Una vez iniciado MariaDB y configuradas las variables de entorno:

```powershell
mvn javafx:run
```

Maven descargará las dependencias necesarias y ejecutará la aplicación JavaFX.

El flujo general de ejecución es:

```text
JavaFX
   |
   v
TablasController
   |
   v
PersonaDao
   |
   v
DataBaseConnection
   |
   v
JDBC
   |
   v
MariaDB
   |
   v
Docker
```

## Detener MariaDB

Para detener los contenedores:

```powershell
docker compose down
```

Los datos de MariaDB se conservarán gracias al volumen Docker.

Para volver a iniciar la base de datos:

```powershell
docker compose up -d
```

## Reiniciar completamente la base de datos

Si se desea eliminar también toda la información almacenada y volver a ejecutar los scripts de inicialización:

```powershell
docker compose down -v
docker compose up -d
```

> El comando `docker compose down -v` elimina los volúmenes asociados al proyecto y, por tanto, todos los datos almacenados en MariaDB.

Los scripts situados en `docker/init/` solamente se ejecutan automáticamente cuando MariaDB se inicializa sobre un volumen vacío.

## Compilar el proyecto

Para limpiar y compilar el proyecto:

```powershell
mvn clean compile
```

Para generar el paquete Maven:

```powershell
mvn clean package
```

El resultado se almacenará dentro de:

```text
target/
```

## Internacionalización

La aplicación utiliza `ResourceBundle` para separar los textos de la interfaz del código Java.

Actualmente dispone de traducciones para:

- Español.
- Inglés.

Los recursos de idioma se encuentran en:

```text
src/main/resources/
├── i18n.properties
├── i18n_en.properties
└── i18n_es.properties
```

El idioma se selecciona automáticamente mediante:

```java
Locale.getDefault();
```

Por ejemplo:

```text
es_ES
```

utiliza la traducción española, mientras que:

```text
en_US
```

utiliza la traducción inglesa.

Si no existe una traducción específica para el idioma del sistema, se utiliza:

```text
i18n.properties
```

como idioma por defecto.

Los textos del FXML utilizan claves del `ResourceBundle`.

Ejemplo:

```xml
<Label text="%label.firstName" />
```

En lugar de almacenar directamente:

```xml
<Label text="First Name:" />
```

Los textos generados desde Java, como los `Tooltip` y los mensajes de error, también se obtienen mediante el mismo sistema de traducciones.

## Formato regional de fechas

Las fechas mostradas en la tabla utilizan el formato correspondiente a la configuración regional del sistema mediante:

```java
DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
```

De esta forma una misma fecha puede mostrarse de forma diferente dependiendo de la locale configurada.

## Base de datos

La tabla principal utilizada por la aplicación es:

```sql
person
```

Su estructura es similar a:

```sql
CREATE TABLE IF NOT EXISTS person (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

El campo:

```text
deleted
```

permite implementar borrado lógico.

Cuando una persona es eliminada, el registro no se elimina físicamente de MariaDB.

En su lugar se establece:

```sql
deleted = TRUE
```

Los registros visibles se obtienen mediante:

```sql
WHERE deleted = FALSE
```

La opción de restaurar vuelve a establecer los registros como activos.

## Acceso a datos

El acceso a MariaDB se realiza mediante JDBC y el patrón DAO.

La clase:

```text
PersonaDao
```

contiene las operaciones relacionadas con la tabla `person`.

Entre ellas:

- Consultar personas.
- Insertar personas.
- Realizar borrado lógico.
- Restaurar registros.

Las consultas utilizan `PreparedStatement` para evitar construir sentencias SQL mediante concatenación directa.

Las eliminaciones múltiples se realizan dentro de una transacción.

## Estructura principal del proyecto

```text
tablas-mariadb-koldo-aso/
│
├── compose.yaml
├── .env <- Debe ser creado por el propio usuario
├── .env.example
├── .gitignore
├── pom.xml
├── README.md
│
├── docker/
│   └── init/
│       └── init.sql
│
└── src/
    └── main/
        ├── java/
        │   ├── module-info.java
        │   │
        │   └── dein/
        │       └── koldo/
        │           └── tablesmariadb/
        │               ├── TablasMariaDBApplication.java
        │               ├── Launcher.java
        │               │
        │               ├── controller/
        │               │   └── TablasController.java
        │               │
        │               ├── dao/
        │               │   └── PersonaDao.java
        │               │
        │               ├── database/
        │               │   └── DataBaseConnection.java
        │               │
        │               ├── model/
        │               │   └── PersonaModel.java
        │               │
        │               └── util/
        │                   └── LoggerConfig.java
        │
        └── resources/
            ├── i18n.properties
            ├── i18n_en.properties
            ├── i18n_es.properties
            │
            └── dein/
                └── koldo/
                    └── tablesmariadb/
                        └── tablas-view.fxml
```

## Organización del código

La aplicación utiliza una separación por responsabilidades.

### `controller`

Contiene los controladores JavaFX.

`TablasController` gestiona la interacción entre la interfaz gráfica y el resto de la aplicación.

### `dao`

Contiene las operaciones de acceso a datos.

`PersonaDao` realiza las operaciones SQL necesarias sobre MariaDB.

### `database`

Contiene la configuración de acceso a la base de datos.

`DataBaseConnection` crea las conexiones JDBC utilizando variables de entorno.

### `model`

Contiene los modelos de datos.

`PersonaModel` representa una persona almacenada en la aplicación.

### `util`

Contiene funcionalidades auxiliares.

`LoggerConfig` configura el sistema de logging utilizado por la aplicación.

## Logs

Durante la ejecución se genera el archivo:

```text
application.log
```

El archivo almacena información relacionada con la actividad de la aplicación.

Entre otros eventos se registran:

- Inicio de la aplicación.
- Inicialización del controlador.
- Locale detectada.
- Personas cargadas.
- Personas creadas.
- Personas eliminadas.
- Personas restauradas.
- Errores de acceso a MariaDB.
- Intentos de realizar operaciones incorrectas.

Los errores SQL se registran junto con su excepción completa para facilitar la detección de problemas.

Los detalles internos de las excepciones no se muestran directamente al usuario.

## Documentación

El código incluye documentación interna mediante JavaDoc.

Las principales clases, atributos y métodos contienen documentación sobre:

- Su responsabilidad.
- Parámetros.
- Valores devueltos.
- Posibles excepciones.
- Funcionamiento general.

La documentación JavaDoc puede generarse mediante Maven cuando el proyecto tenga configurado el plugin correspondiente.

## Seguridad

El proyecto evita almacenar credenciales directamente en el código fuente.

Las credenciales se proporcionan mediante:

- `.env` para Docker Compose.
- Variables de entorno para JavaFX.

El archivo `.env` está excluido del sistema de control de versiones.

El repositorio solamente contiene:

```text
.env.example
```

como referencia de las variables necesarias.

Los errores internos de MariaDB se registran en `application.log`, pero la interfaz muestra mensajes genéricos para evitar exponer información interna de la base de datos.

## Autor

Koldo