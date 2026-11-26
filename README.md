# Aprendo IA Jugando en Santiago

## 1. Descripción del proyecto

**Aprendo IA Jugando en Santiago** es una aplicación móvil educativa gamificada desarrollada para **Triskeledu SpA**, cuyo objetivo es fomentar el aprendizaje sobre inteligencia artificial mediante actividades interactivas y la exploración de distintos puntos turísticos de Santiago.

La aplicación combina geolocalización, códigos QR, recompensas, contenido educativo y servicios backend para entregar una experiencia de aprendizaje interactiva.

## 2. Integrantes

- Marco Antonio Hernández Fritz

## 3. Funcionalidades implementadas

- Registro e inicio de sesión de usuarios.
- Dashboard principal con navegación entre módulos.
- Mapa turístico de Santiago con geolocalización.
- Visualización de puntos turísticos.
- Escaneo de códigos QR asociados a puntos turísticos.
- Sistema de recompensas por escaneo de códigos QR (+10 puntos).
- Captura de selfies turísticas mediante cámara frontal (+100 puntos).
- Registro de actividades e historial del usuario.
- Colección de puntos turísticos.
- Visualización del progreso del usuario.
- Perfil del jugador.
- Módulos de administración y gestión de contenido.
- Consulta y visualización de contenido educativo audiovisual.

## 4. Arquitectura y tecnologías utilizadas

### Aplicación móvil

- Kotlin.
- Android Studio.
- Jetpack Compose.
- Material Design 3.
- SQLite para almacenamiento local.
- CameraX para acceso a la cámara.
- Google ML Kit para lectura de códigos QR.
- OpenStreetMap / osmdroid para visualización de mapas.
- Geolocalización del dispositivo Android.
- Retrofit para comunicación con servicios backend.

### Backend

El backend está desarrollado con **Java y Spring Boot**, utilizando una arquitectura basada en tres microservicios independientes:

| Microservicio | Responsabilidad | Puerto |
|---|---|---:|
| microservicio-usuarios | Registro, autenticación y gestión de usuarios. | 8081 |
| microservicio-productos | Administración y consulta de productos. | 8082 |
| microservicio-lugares | Administración y consulta de puntos turísticos. | 8083 |

Cada microservicio cuenta con su propio proyecto Maven y una base de datos H2 independiente.

### API externa

Se utiliza **YouTube Data API v3** para consultar contenido audiovisual educativo y permitir su visualización desde la aplicación.

## 5. Estructura del repositorio

```text
AprendoIAJugando/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   └── test/
│   └── release/
│       └── app-release.apk
│
├── backend/
│   ├── microservicio-usuarios/
│   ├── microservicio-productos/
│   └── microservicio-lugares/
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
├── .gitignore
└── README.md
```

## 6. Microservicios y endpoints

Todos los servicios exponen una API REST y utilizan JSON para el intercambio de información.

### 6.1. Microservicio de usuarios

**Puerto:** `8081`

**URL base:** `http://localhost:8081/api/usuarios`

| Método HTTP | Endpoint | Descripción |
|---|---|---|
| GET | `/api/usuarios` | Lista todos los usuarios. |
| GET | `/api/usuarios/{id}` | Busca un usuario por su identificador. |
| POST | `/api/usuarios` | Registra un nuevo usuario. |
| PUT | `/api/usuarios/{id}` | Actualiza los datos de un usuario. |
| DELETE | `/api/usuarios/{id}` | Elimina un usuario. |
| POST | `/api/usuarios/login` | Inicia sesión utilizando las credenciales recibidas. |

### 6.2. Microservicio de productos

**Puerto:** `8082`

**URL base:** `http://localhost:8082/api/productos`

| Método HTTP | Endpoint | Descripción |
|---|---|---|
| GET | `/api/productos` | Lista todos los productos. |
| GET | `/api/productos/activos` | Lista los productos activos. |
| GET | `/api/productos/{id}` | Busca un producto por su identificador. |
| POST | `/api/productos` | Registra un nuevo producto. |
| PUT | `/api/productos/{id}` | Actualiza un producto existente. |
| DELETE | `/api/productos/{id}` | Elimina un producto. |

### 6.3. Microservicio de lugares

**Puerto:** `8083`

**URL base:** `http://localhost:8083/api/lugares`

| Método HTTP | Endpoint | Descripción |
|---|---|---|
| GET | `/api/lugares` | Lista todos los lugares turísticos. |
| GET | `/api/lugares/activos` | Lista los lugares activos. |
| GET | `/api/lugares/{id}` | Busca un lugar por su identificador. |
| GET | `/api/lugares/qr/{codigo}` | Busca un lugar mediante su código QR. |
| POST | `/api/lugares` | Registra un nuevo lugar turístico. |
| PUT | `/api/lugares/{id}` | Actualiza los datos de un lugar. |
| DELETE | `/api/lugares/{id}` | Elimina un lugar. |

### Respuestas HTTP

Los controladores utilizan códigos HTTP para representar los resultados de las operaciones:

- `200 OK`: operación realizada correctamente.
- `201 Created`: recurso creado correctamente.
- `204 No Content`: recurso eliminado correctamente.

Las solicitudes que crean o actualizan recursos reciben datos mediante cuerpos JSON.

## 7. Bases de datos

Los microservicios utilizan **H2 Database** como sistema de almacenamiento, con persistencia en archivos y configuración mediante Spring Data JPA / Hibernate.

| Microservicio | Base de datos | Archivo |
|---|---|---|
| Usuarios | H2 | `./data/usuariosdb` |
| Productos | H2 | `./data/productosdb` |
| Lugares | H2 | `./data/lugaresdb` |

La configuración de Hibernate utiliza `ddl-auto=update` para actualizar el esquema de las tablas según las entidades del proyecto.

### Consola H2

Los tres microservicios tienen habilitada la consola web de H2 en:

`/h2-console`

Direcciones locales:

- Usuarios: `http://localhost:8081/h2-console`
- Productos: `http://localhost:8082/h2-console`
- Lugares: `http://localhost:8083/h2-console`

Para conectarse desde la consola se debe utilizar la URL JDBC configurada en el `application.properties` de cada microservicio.

## 8. Instalación y ejecución

### 8.1. Requisitos

- Android Studio.
- JDK compatible con la configuración de los proyectos.
- Dispositivo Android o emulador.
- Conexión a Internet para los servicios que utilizan APIs externas.

### 8.2. Ejecución de los microservicios

Cada microservicio se ejecuta independientemente.

Ingresar a la carpeta correspondiente:

**Usuarios:**

```powershell
cd backend/microservicio-usuarios
```

**Productos:**

```powershell
cd backend/microservicio-productos
```

**Lugares:**

```powershell
cd backend/microservicio-lugares
```

Ejecutar el servicio desde su carpeta utilizando Maven Wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Repetir el procedimiento para iniciar los tres microservicios. Cada uno debe permanecer ejecutándose en su respectiva terminal.

### 8.3. Ejecución de la aplicación Android

1. Clonar este repositorio.
2. Abrir el proyecto desde Android Studio.
3. Esperar la sincronización de Gradle.
4. Configurar las direcciones de los servicios backend y las credenciales necesarias para las API externas.
5. Conectar un dispositivo Android o iniciar un emulador.
6. Ejecutar la aplicación desde Android Studio.
7. Conceder los permisos necesarios para utilizar la cámara y la ubicación.

**Nota:** Si se utiliza un emulador Android, las direcciones del backend alojado en el mismo computador deben configurarse considerando la dirección de acceso del emulador. Si se utiliza un dispositivo físico, debe configurarse la dirección IP local del computador.

## 9. Pruebas unitarias

El proyecto Android incorpora pruebas unitarias para verificar la lógica de los componentes Repository y ViewModel.

Para ejecutar las pruebas desde la raíz del proyecto, utilizar:

**Windows:**

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

**Linux / macOS:**

```bash
./gradlew :app:testDebugUnitTest
```

Estas pruebas permiten verificar el funcionamiento de la lógica de acceso a datos y de los componentes encargados de gestionar el estado de la aplicación.

## 10. APK de la aplicación

El repositorio incluye el archivo APK correspondiente a la versión firmada de la aplicación:

`app/release/app-release.apk`

Este archivo permite instalar y probar la aplicación en un dispositivo Android compatible.

## 11. Tablero de gestión del proyecto

El desarrollo y seguimiento de las actividades se organiza mediante Trello:

[Aprendo IA Jugando - Tablero Trello](https://trello.com/b/WmQdFgo8/aprendo-ia-jugando-dsy1105)

## 12. Propósito académico

Proyecto desarrollado para la asignatura **Desarrollo de Aplicaciones Móviles (DSY1105)**, perteneciente a la carrera Ingeniería en Informática de Duoc UC.

- **Institución:** Duoc UC.
- **Empresa / contexto:** Triskeledu SpA.
- **Proyecto:** Aprendo IA Jugando en Santiago.