# 🎲 S501-_Spring_Framework_Avançat_amb_WebFlux

Proyecto API REST para un juego de Blackjack de un jugador contra el dealer, desarrollado con Spring Boot y WebFlux.

---

## 📋 Descripción

Esta API implementa la lógica básica del juego Blackjack para un solo jugador. Permite crear partidas, gestionar turnos (pedir carta, plantarse), y ver el estado del juego.

Está desarrollado usando:

- ☕ Java 21
- 🚀 Spring Boot con WebFlux (programación reactiva)
- 🗄️ Persistencia en MongoDB y MySQL (reactivos)
- 📚 Documentación Swagger para probar los endpoints
- ✅ Testing con JUnit y Mockito
- 🛠️ Manejo global de excepciones

---

## 🎯 Funcionalidades principales

- ➕ Crear una nueva partida asociada a un jugador
- 🃏 Pedir carta (hit)
- ✋ Plantarse (stand)
- 🔍 Consultar estado del juego
- 🗃️ Persistencia reactiva en MongoDB y MySQL
- ⚠️ Control de errores y validaciones básicas

---

## 🛠️ Tecnologías usadas

- ☕ Java 21
- 🚀 Spring Boot 3.5.0 con WebFlux
- 🍃 MongoDB reactive driver
- 🔗 R2DBC con MySQL para persistencia reactiva
- 🧪 JUnit 5 + Mockito para testing
- 📖 Swagger/OpenAPI para documentación REST
- 📬 Postman para probar endpoints manualmente

---

## ⚙️ Prerrequisitos

- ☕ Java 21 instalado
- 🐳 Docker (opcional, para bases de datos o dockerizar la app)
- 🍃 MongoDB y 🐬 MySQL corriendo local o remoto
- 💻 IDE recomendado: IntelliJ IDEA / VS Code

---

## 🚀 Cómo ejecutar el proyecto (clica en el desplegable para ver cada nivel):

<details>
   <summary><strong> 🚀 Nivel 1: Uso local </strong></summary>
   
   ---
   
   1. Clona el repositorio:
      ```bash
      git clone https://github.com/Jusep1983/blackjack-api.git
      ```
      ```bash
      cd blackjack-api
      ```
   2. Ajusta src/main/resources/application.yml con tus credenciales de MySQL y MongoDB locales.
   
   Ejecuta:
   
   ```bash
   ./mvnw spring-boot:run
   ```
   3. Abre Swagger UI:
   
   
   http://localhost:8080/swagger-ui/index.html
   
   4. Accede al frontend:
   
   http://localhost:8080/index.html
   
</details>

<details>
   <summary><strong> 🐳 Nivel 2: Ejecución con Docker Compose </strong></summary>

   ---
   
   1. Construye y levanta contenedores:
   
   ```bash
   docker-compose up -d --build
   ```
   2. La API y Swagger estarán en:
   
      http://localhost:8080/swagger-ui/index.html
   
      http://localhost:8080/index.html
   
   3. Para parar (sin borrar volúmenes):
   
   ```bash
   
   docker-compose stop
   ```
   4. Para reiniciar contenedores parados:
   
   ```bash
   docker-compose start
   ```
   5. Para detener y eliminar contenedores y volúmenes:
   
   ```bash
   
   docker-compose down -v
   ```
</details>

<details>
   <summary><strong> 🔧 Nivel 3: Despliegue en Render</strong></summary>

   ---
   
   1. Conecta tu repo de GitHub a Render.
   
   2. Define variables de entorno en Render (Environment):
   
   ```env
   SPRING_PROFILES_ACTIVE=docker
   SPRING_R2DBC_URL=<tu_URL_R2DBC>
   SPRING_R2DBC_USERNAME=<usuario>
   SPRING_R2DBC_PASSWORD=<password>
   SPRING_DATA_MONGODB_URI=<tu_URI_MongoDB>
   ```
   3. Render detecta application-docker.yml y usa esas variables.
   
   4. Haz manual deploy y prueba en:
   
   ```arduino
   https://<tu-app>.onrender.com/swagger-ui/index.html
   ```
   5. Mi aplicacion estara disponibvle para probar ya desplegada en:
   
   - Web mediante frontend sencillo:
     
   https://s501-blackjack-api.onrender.com/index.html
   
   - Swagger:
     
   https://s501-blackjack-api.onrender.com/swagger-ui/index.html#/

</details>

---

## 📜 Endpoints principales

| Método | Endpoint               | Descripción                  |
|--------|------------------------|------------------------------|
| POST   | `/game/new`            | Crear nueva partida          |
| GET    | `/game/{id}`           | Detalles de una partida      |
| POST   | `/game/{id}/play`      | Realizar jugada en partida   |
| DELETE | `/game/{id}/delete`    | Borrar partida               |
| GET    | `/ranking`             | Obtener ranking de jugadores |
| PUT    | `/player/{playerId}`   | Cambiar nombre de jugador    |

---

## 🧪 Pruebas
Ejecuta todos los tests:

```bash
./mvnw test
```

---

## 🐳 Imagen Docker pública
Docker Hub

Ejecutar sin build:

```bash
docker pull jusep83/blackjack-blackjack-api:latest
```
```bash
docker run -p 8080:8080 --env-file .env jusep83/blackjack-blackjack-api:latest
```
---
## 📁 Estructura del proyecto


```text
.
├── Dockerfile                                # Imagen de la API para Docker
├── docker-compose.yml                        # Orquestación de la API + MySQL + MongoDB
├── README.md                                 # Documentación del proyecto
├── pom.xml                                   # Dependencias del proyecto Maven
├── .dockerignore                             # Archivos que Docker debe ignorar
├── .gitignore                                # Archivos ignorados por Git
│
├── src
│   └── main
│       ├── java
│       │   └── com.jusep1983.blackjack
│       │       ├── config                    # Configuración general
│       │       ├── controller                # Controladores REST (GameController, PlayerController, etc.)
│       │       ├── dto                       # DTOs para entrada y salida
│       │       ├── enums                     # Enumeraciones: tipos de jugada, estado del juego...
│       │       ├── exception                 # Excepciones personalizadas y GlobalExceptionHandler
│       │       ├── model                     # Entidades (Player, Game, Card, etc.)
│       │       ├── repository                # Interfaces de persistencia (Mongo y MySQL reactivos)
│       │       ├── response                  # Clases de respuesta estructurada (opcional)
│       │       ├── service                   # Interfaces e implementaciones de lógica de negocio
│       │       └── BlackjackApplication.java # Clase principal con el método `main`
│       │
│       └── resources
│           ├── static
│           │   └── index.html                # Frontend embebido muy básico
│           ├── application.yml               # Config local (localhost)
│           ├── application-docker.yml        # Config para entorno Docker o Render
│           └── application.properties        # (vacío o no usado)
│
└── test
    └── java
        └── com.jusep1983.blackjack
            ├── service                       # Tests de PlayerService, GameService, etc.
            └── controller                    # Tests de PlayerController, GameController...
```
---
## 👤 Autor
Josep1983

## 📄 Licencia
MIT
