# BlackjackApp

API y aplicacion web para jugar al Blackjack, con autenticacion JWT, roles de usuario, gestion de jugadores, ranking y persistencia en MongoDB y MySQL.

## Tabla de Contenidos

- [Descripcion](#descripcion)
- [Configuracion de variables sensibles (JWT y Bases de Datos)](#configuracion-de-variables-sensibles-jwt-y-bases-de-datos)
- [Caracteristicas principales](#caracteristicas-principales)
- [Tecnologias](#tecnologias)
- [Estructura real del repositorio](#estructura-real-del-repositorio)
- [Instalacion y ejecucion](#instalacion-y-ejecucion)
  - [1. Clona el repositorio](#1-clona-el-repositorio)
  - [2. Configura tus variables de entorno](#2-configura-tus-variables-de-entorno)
  - [3. Ejecuta la aplicacion (local)](#3-ejecuta-la-aplicacion-local)
  - [4. Pruebas automaticas](#4-pruebas-automaticas)
  - [5. Docker (opcional)](#5-docker-opcional)
- [Endpoints principales](#endpoints-principales)
- [Seguridad y roles](#seguridad-y-roles)
- [Testing](#testing)
- [Despliegue Docker](#despliegue-docker)
- [Autor](#autor)
- [Prueba la app desplegada (Frontend)](#prueba-la-app-desplegada-frontend)

---

## Descripcion

📝 BlackjackApp es una API REST y web app para jugar partidas de Blackjack contra la banca, con registro/login, gestion y ranking de jugadores, panel de administracion, y seguridad JWT. El backend esta desarrollado con **Spring WebFlux** y persiste datos en **MongoDB** (partidas) y **MySQL** (jugadores). Incluye documentacion Swagger/OpenAPI y pruebas automatizadas.

## Configuracion de variables sensibles (JWT y Bases de Datos)

Antes de ejecutar el backend, debes definir estas variables de entorno en tu maquina local editando en application-local.yml:

```
JWT_SECRET=pon-tu-clave-secreta-aqui
MONGODB_URI=mongodb://localhost:27017
MONGODB_DATABASE=blackjack_app_games
MYSQL_R2DBC_URL=r2dbc:mysql://localhost:3306/blackjack_app_players
MYSQL_USER=user
MYSQL_PASSWORD=password
```

---

## Caracteristicas principales

- Registro y login de usuarios con JWT
- Roles: `USER`, `ADMIN`, `SUPER_USER`
- CRUD de jugadores y partidas
- Logica completa del juego de Blackjack (HIT, STAND, dealer automatico)
- Panel de ranking y gestion de jugadores para admins
- Persistencia reactiva en MongoDB y MySQL
- Manejo global de excepciones y logs estructurados
- Testing con JUnit y Mockito
- Configuracion CORS para frontend React
- Docker-ready

---

## Tecnologias

- Java 21
- Spring Boot 3.x
- Spring WebFlux
- MongoDB (Reactive)
- MySQL (R2DBC)
- Spring Security JWT
- Swagger/OpenAPI 3
- JUnit / Mockito
- Maven
- Docker

---

## Estructura real del repositorio

```
S502_BlackjackApp/
├── .dockerignore
├── .gitattributes
├── .gitignore
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
├── scripts/
│   └── init.sql
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/jusep1983/blackjack/
│   │   │       ├── BlackjackApplication.java
│   │   │       ├── admin/
│   │   │       ├── auth/
│   │   │       ├── config/
│   │   │       ├── deck/
│   │   │       ├── game/
│   │   │       ├── hand/
│   │   │       ├── passwordCheck/
│   │   │       ├── player/
│   │   │       └── shared/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       ├── application-prod.yml
│   │       ├── application-docker.yml
│   │       ├── application.properties
│   │       └── static/
│   └── test/
│       └── java/com/jusep1983/blackjack/
│           ├── auth/
│           ├── player/
│           └── BlackjackApplicationTests.java
└── target/
    (archivos generados por Maven)
```

---

## Instalacion y ejecucion

### 1. Clona el repositorio

```bash
git clone https://github.com/Jusep1983/S502_BlackjackApp.git
cd S502_BlackjackApp
```

### 2. Configura tus variables de entorno

- MongoDB: (por ejemplo, MongoDB Atlas)
- MySQL: Crea la base de datos y tabla `players` (puedes usar el script SQL incluido)
- JWT\_SECRET: Define una clave secreta robusta para tokens

Copia y personaliza el archivo `.env.example` (si existe) o configura en `application.yml` / `application.properties`.

### 3. Ejecuta la aplicacion (local)

```bash
mvn spring-boot:run
```

La API estara en: `http://localhost:8080/`

### 4. Pruebas automaticas

```bash
mvn test
```

### 5. Docker (opcional)

Para levantar el backend via Docker (requiere tener MongoDB y MySQL accesibles):

```bash
docker build -t blackjackapp-backend .
docker run --env-file .env -p 8080:8080 blackjackapp-backend
```

---

## Endpoints principales

Algunos endpoints destacados (ver Swagger en `/swagger-ui.html`):

- /auth/register: Registro de usuario (devuelve JWT)
- /auth/login: Login (devuelve JWT)
- /player/me: Info y partidas del jugador autenticado
- /player/ranking: Ranking de jugadores
- /game/new: Crear nueva partida
- /game/{id}/hit: Pedir carta (HIT)
- /game/{id}/stand: Plantarse (STAND)
- /admin/set-role/{playerId}: Cambiar rol (solo ADMIN/SUPER\_USER)
- /admin/delete-player/by-username/{userName}: Eliminar jugador (ADMIN)
- /player/updateAlias: Cambiar alias
- /game/{id}/delete: Eliminar partida (dueño)

Todos los endpoints (excepto registro/login) requieren JWT Bearer en Authorization header.

---

## Seguridad y roles

- JWT Bearer: Autenticacion y autorizacion en todos los endpoints protegidos.
- Roles:
  - USER: Juega y ve su perfil/ranking.
  - ADMIN: Puede cambiar roles/eliminar usuarios.
  - SUPER\_USER: Solo 1, no puede ser degradado ni borrado.

---

## Testing

El proyecto incluye tests unitarios para servicios, controladores y logica de juego con JUnit y Mockito. Puedes ejecutar todos los tests con:

```bash
mvn test
```

---

## Despliegue Docker

---

## Autor

Jose (Jusep1983)

---

## Prueba la app desplegada (Frontend)

Puedes probar el juego BlackjackApp en produccion aqui:

➡️ [https://s502-blackjack-app-frontend.onrender.com/](https://s502-blackjack-app-frontend.onrender.com/)

- Solo tienes que registrarte y empezar a jugar desde el navegador.
- Incluye ranking y panel de usuario.
- El backend (este repositorio) es consumido directamente por ese frontend.

> Nota: El frontend esta desarrollado en React y se encuentra en otro repositorio: [https://github.com/Jusep1983/S502-blackjack-app-frontend](https://github.com/Jusep1983/S502-blackjack-app-frontend)

---


