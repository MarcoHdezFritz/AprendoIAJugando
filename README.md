# Aprendo IA Jugando en Santiago

## 1. Descripción del proyecto

**Aprendo IA Jugando en Santiago** es una aplicación móvil educativa gamificada desarrollada en el contexto de **Triskeledu SpA**, cuyo objetivo es fomentar el aprendizaje sobre inteligencia artificial mediante actividades interactivas y la exploración de distintos puntos turísticos de Santiago de Chile.

La aplicación combina geolocalización, códigos QR, recompensas, contenido educativo audiovisual y servicios backend para entregar una experiencia de aprendizaje interactiva.

## 2. Integrante

- **Marco Antonio Hernández Fritz**

## 3. Funcionalidades implementadas

La aplicación incorpora los siguientes módulos y funcionalidades:

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

### 4.1. Aplicación móvil

La aplicación móvil fue desarrollada utilizando las siguientes tecnologías:

| Tecnología | Utilización |
|---|---|
| Kotlin | Lenguaje de programación principal. |
| Android Studio | Entorno de desarrollo. |
| Jetpack Compose | Construcción de interfaces de usuario. |
| Material Design 3 | Diseño visual y componentes de interfaz. |
| SQLite | Almacenamiento local de información. |
| CameraX | Acceso a la cámara del dispositivo. |
| Google ML Kit | Reconocimiento y lectura de códigos QR. |
| OpenStreetMap / osmdroid | Visualización de mapas y puntos turísticos. |
| Geolocalización Android | Obtención de la ubicación del dispositivo. |
| Retrofit | Comunicación HTTP con el backend. |

### 4.2. Arquitectura MVVM

La aplicación utiliza el patrón arquitectónico **MVVM (Model-View-ViewModel)**, que permite separar las responsabilidades de la interfaz, la lógica de presentación y el acceso a los datos.

Sus principales componentes son:

- **View (Screens):** contiene las pantallas y componentes visuales desarrollados con Jetpack Compose. Se encarga de representar la información y recibir las interacciones del usuario.
- **ViewModel:** administra los estados de la interfaz y coordina la lógica de presentación, comunicándose con los repositorios.
- **Repository:** centraliza el acceso a los datos y coordina las operaciones necesarias para obtener o enviar información.
- **Model:** representa las estructuras de datos utilizadas por la aplicación, como usuarios, productos y lugares.

Esta arquitectura facilita la organización del código, la separación de responsabilidades y la realización de pruebas unitarias sobre la lógica de los ViewModels y repositorios.

### 4.3. Backend y arquitectura de microservicios

El backend está desarrollado con **Java y Spring Boot**, utilizando una arquitectura basada en tres microservicios independientes, un servidor de descubrimiento de servicios y un API Gateway.

| Componente | Responsabilidad | Puerto |
|---|---|---:|
| Eureka Server | Registro y descubrimiento de los servicios backend. | 8761 |
| API Gateway | Punto de entrada para las solicitudes de la aplicación y enrutamiento hacia los microservicios. | 8080 |
| microservicio-usuarios | Registro, autenticación y gestión de usuarios. | 8081 |
| microservicio-productos | Administración y consulta de productos. | 8082 |
| microservicio-lugares | Administración y consulta de puntos turísticos. | 8083 |

Cada microservicio cuenta con su propio proyecto Maven, controladores REST, entidades, repositorios y una base de datos H2 independiente.

**Eureka Server** permite registrar los microservicios y facilitar su descubrimiento dentro de la arquitectura backend.

**API Gateway** centraliza las solicitudes provenientes de la aplicación Android y las enruta hacia el microservicio correspondiente, utilizando la configuración de rutas y el descubrimiento de servicios.

La aplicación Android realiza sus solicitudes HTTP mediante Retrofit, comunicándose con el API Gateway a través del puerto **8080**. El intercambio de información se realiza en formato JSON.

### 4.4. API externa

La aplicación utiliza **YouTube Data API v3** para consultar contenido audiovisual educativo relacionado con la inteligencia artificial.

Esta integración permite obtener información de videos educativos y facilitar su visualización desde la aplicación.

## 5. Estructura del repositorio

La organización general del proyecto es la siguiente:

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
│   ├── Proyecto Eureka Server/
│   ├── Proyecto API Gateway/
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

Los tres microservicios exponen APIs REST y utilizan JSON para el intercambio de información.

Las siguientes rutas corresponden a los recursos disponibles. La aplicación Android accede a ellos a través del API Gateway, utilizando el puerto 8080.

### 6.1. Microservicio de usuarios

**Puerto interno:** `8081`

**URL de acceso mediante API Gateway:** `http://localhost:8080/api/usuarios`

| Método HTTP | Endpoint | Descripción |
|---|---|---|
| GET | `/api/usuarios` | Lista todos los usuarios. |
| GET | `/api/usuarios/{id}` | Busca un usuario por su identificador. |
| POST | `/api/usuarios` | Registra un nuevo usuario. |
| PUT | `/api/usuarios/{id}` | Actualiza los datos de un usuario. |
| DELETE | `/api/usuarios/{id}` | Elimina un usuario. |
| POST | `/api/usuarios/login` | Inicia sesión utilizando las credenciales recibidas. |

### 6.2. Microservicio de productos

**Puerto interno:** `8082`

**URL de acceso mediante API Gateway:** `http://localhost:8080/api/productos`

| Método HTTP | Endpoint | Descripción |
|---|---|---|
| GET | `/api/productos` | Lista todos los productos. |
| GET | `/api/productos/activos` | Lista los productos activos. |
| GET | `/api/productos/{id}` | Busca un producto por su identificador. |
| POST | `/api/productos` | Registra un nuevo producto. |
| PUT | `/api/productos/{id}` | Actualiza un producto existente. |
| DELETE | `/api/productos/{id}` | Elimina un producto. |

### 6.3. Microservicio de lugares

**Puerto interno:** `8083`

**URL de acceso mediante API Gateway:** `http://localhost:8080/api/lugares`

| Método HTTP | Endpoint | Descripción |
|---|---|---|
| GET | `/api/lugares` | Lista todos los lugares turísticos. |
| GET | `/api/lugares/activos` | Lista los lugares activos. |
| GET | `/api/lugares/{id}` | Busca un lugar por su identificador. |
| GET | `/api/lugares/qr/{codigo}` | Busca un lugar mediante su código QR. |
| POST | `/api/lugares` | Registra un nuevo lugar turístico. |
| PUT | `/api/lugares/{id}` | Actualiza los datos de un lugar. |
| DELETE | `/api/lugares/{id}` | Elimina un lugar. |

### 6.4. Respuestas HTTP

Los controladores utilizan códigos HTTP para representar los resultados de las operaciones:

- `200 OK`: operación realizada correctamente.
- `201 Created`: recurso creado correctamente.
- `204 No Content`: recurso eliminado correctamente.

Las solicitudes que crean o actualizan recursos reciben información mediante cuerpos JSON.

## 7. Bases de datos y persistencia

### 7.1. Persistencia del backend

Los microservicios utilizan **H2 Database** como sistema de almacenamiento, con persistencia en archivos y configuración mediante Spring Data JPA / Hibernate.

| Microservicio | Base de datos | Archivo |
|---|---|---|
| Usuarios | H2 | `./data/usuariosdb` |
| Productos | H2 | `./data/productosdb` |
| Lugares | H2 | `./data/lugaresdb` |

La configuración de Hibernate utiliza `ddl-auto=update` para actualizar el esquema de las tablas según las entidades del proyecto.

### 7.2. Consola H2

Los tres microservicios tienen habilitada la consola web de H2 en `/h2-console`.

Direcciones locales:

- Usuarios: `http://localhost:8081/h2-console`
- Productos: `http://localhost:8082/h2-console`
- Lugares: `http://localhost:8083/h2-console`

Para conectarse desde la consola se debe utilizar la URL JDBC configurada en el archivo `application.properties` de cada microservicio.

Estas consolas se consultan directamente en los puertos de los microservicios, no a través del API Gateway.

### 7.3. Persistencia local

La aplicación Android también utiliza SQLite para el almacenamiento local de información.

Esto permite gestionar información en el dispositivo y complementar las operaciones realizadas mediante los servicios backend.

## 8. Comunicación entre la aplicación y el backend

La comunicación entre Android y el backend se implementa mediante **Retrofit**, una biblioteca que facilita el consumo de APIs REST.

La aplicación utiliza el API Gateway como punto de entrada para comunicarse con los microservicios.

El flujo general de comunicación es:

1. El usuario realiza una acción desde la interfaz.
2. El ViewModel recibe la interacción y coordina la operación.
3. El Repository solicita los datos correspondientes.
4. Retrofit realiza la petición HTTP hacia el API Gateway mediante el puerto 8080.
5. API Gateway recibe la solicitud y determina el servicio de destino.
6. Eureka facilita el descubrimiento del microservicio correspondiente.
7. Spring Boot procesa la solicitud mediante su controlador.
8. El servicio y el repositorio backend gestionan la operación sobre la base de datos.
9. El backend devuelve una respuesta JSON a través del Gateway.
10. La aplicación procesa el resultado y actualiza el estado de la interfaz.

### Diferencia entre API externa y microservicios propios

- **Microservicios propios:** son servicios backend desarrollados con Spring Boot para gestionar la información y las operaciones de usuarios, productos y lugares.
- **API externa:** corresponde a YouTube Data API v3, un servicio de terceros que permite consultar contenido audiovisual educativo.

Ambos mecanismos utilizan solicitudes HTTP, pero tienen diferentes responsabilidades y fuentes de información.

## 9. Instalación y ejecución

### 9.1. Requisitos

Para ejecutar el proyecto se necesita:

- Android Studio.
- JDK compatible con la configuración de los proyectos.
- Maven Wrapper incluido en los proyectos backend.
- Dispositivo Android o emulador.
- Android SDK Platform-Tools para utilizar ADB.
- Conexión a Internet para los servicios que utilizan APIs externas.

### 9.2. Ejecución del backend

Para iniciar correctamente la arquitectura backend, se deben ejecutar sus componentes en el siguiente orden:

1. **Eureka Server:** iniciar el servidor de descubrimiento de servicios en el puerto `8761`.
2. **Microservicio de usuarios:** iniciar el servicio en el puerto `8081`.
3. **Microservicio de productos:** iniciar el servicio en el puerto `8082`.
4. **Microservicio de lugares:** iniciar el servicio en el puerto `8083`.
5. **API Gateway:** iniciar el Gateway en el puerto `8080`.

Cada componente debe ejecutarse desde la carpeta de su respectivo proyecto Spring Boot.

En Windows, utilizar Maven Wrapper desde la carpeta correspondiente:

```powershell
.\mvnw.cmd spring-boot:run
```

Repetir el procedimiento para cada proyecto backend, manteniendo cada servicio ejecutándose en su respectiva terminal.

Una vez iniciados los componentes, Eureka permite consultar el registro de servicios desde:

`http://localhost:8761`

El API Gateway recibe las solicitudes de la aplicación Android mediante:

`http://localhost:8080`

### 9.3. Ejecución de la aplicación Android

1. Clonar este repositorio.
2. Abrir el proyecto desde Android Studio.
3. Esperar la sincronización de Gradle.
4. Configurar las direcciones de los servicios backend y las credenciales necesarias para las APIs externas.
5. Conectar un dispositivo Android o iniciar un emulador.
6. Ejecutar la aplicación desde Android Studio.
7. Conceder los permisos necesarios para utilizar la cámara y la ubicación.

### 9.4. Conexión con un dispositivo Android físico mediante ADB

Cuando se utiliza un dispositivo Android físico conectado al computador mediante USB, se debe habilitar la depuración USB y utilizar el siguiente comando:

```bash
adb reverse tcp:8080 tcp:8080
```

Este comando permite redirigir las solicitudes realizadas desde el puerto 8080 del dispositivo hacia el puerto 8080 del computador.

De esta manera, la aplicación Android puede comunicarse con el API Gateway utilizando la dirección local configurada para el dispositivo, sin necesidad de exponer directamente los puertos internos de los microservicios.

**Importante:** el comando debe ejecutarse con el dispositivo conectado y reconocido por ADB. Si se desconecta el dispositivo o se pierde la redirección, puede ser necesario ejecutar nuevamente el comando.

Para utilizar un emulador Android, se debe configurar la dirección de acceso al backend considerando la dirección especial del emulador para acceder al computador anfitrión.

## 10. Pruebas unitarias y cobertura

El proyecto Android incorpora pruebas unitarias destinadas a verificar el comportamiento esperado de la lógica implementada en los componentes **ViewModel y Repository**.

Estas pruebas permiten validar el funcionamiento de la lógica de presentación y del acceso a los datos, utilizando simulaciones (*mocks*) cuando corresponde para aislar los componentes evaluados.

### 10.1. Ejecución de pruebas

Para ejecutar las pruebas unitarias desde la raíz del proyecto:

**Windows:**

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

**Linux / macOS:**

```bash
./gradlew :app:testDebugUnitTest
```

### 10.2. Cobertura con JaCoCo

Se utiliza **JaCoCo 0.8.14** para generar informes de cobertura del código.

Los resultados obtenidos durante el desarrollo fueron:

| Componente | Cobertura de instrucciones |
|---|---:|
| ViewModel | 93% |
| Repository | 89% |

Ambos componentes superan el objetivo de cobertura del 80% establecido para la lógica evaluada.

Los informes permiten identificar las instrucciones ejecutadas durante las pruebas y detectar sectores de código que requieren validación adicional.

## 11. APK firmada

El proyecto incluye el archivo APK correspondiente a la versión Release firmada digitalmente de la aplicación:

```text
app/release/app-release.apk
```

Este archivo permite instalar y ejecutar la aplicación en un dispositivo Android compatible.

La generación del APK firmado se realiza mediante las herramientas de compilación de Android Studio, utilizando la configuración de firma correspondiente a la versión Release.

**Importante:** el archivo de claves de firma y sus contraseñas deben mantenerse protegidos y no deben publicarse en el repositorio.

## 12. Gestión y planificación del proyecto

El seguimiento de las actividades y la organización del desarrollo se realiza mediante Trello.

**Tablero del proyecto:**

[Aprendo IA Jugando - Trello](https://trello.com/b/WmQdFgo8/aprendo-ia-jugando-dsy1105)

El control de versiones y el almacenamiento del código fuente se gestionan mediante Git y GitHub.

**Repositorio del proyecto:**

[AprendoIAJugando - GitHub](https://github.com/MarcoHdezFritz/AprendoIAJugando)

## 13. Propósito académico

Proyecto desarrollado para la asignatura **Desarrollo de Aplicaciones Móviles (DSY1105)**, perteneciente a la carrera Ingeniería en Informática de Duoc UC.

- **Institución:** Duoc UC.
- **Empresa / contexto:** Triskeledu SpA.
- **Proyecto:** Aprendo IA Jugando en Santiago.
- **Integrante:** Marco Antonio Hernández Fritz.

---

**Aprendo IA Jugando en Santiago**

Aplicación móvil educativa gamificada para fomentar el aprendizaje de inteligencia artificial mediante la exploración turística y actividades interactivas.