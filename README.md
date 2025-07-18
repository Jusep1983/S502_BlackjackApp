# 🎲 S502 - BlackjackApp

Aplicación API REST para jugar al Blackjack (21) de forma individual contra el dealer.

Esta versión incluye una estructura modular escalable, preparada para implementar autenticación con JWT y control de roles (`USER`, `ADMIN`), y con posibilidad de integrar un frontend en el futuro.

---

## 🚀 Tecnologías

- ☕ Java 21
- ⚡ Spring Boot + WebFlux (reactivo)
- 🔐 Spring Security (JWT, próximamente)
- 🗄️ MongoDB + MySQL (reactivos)
- 📚 Swagger / OpenAPI
- 🧪 JUnit 5 + Mockito
- 🐳 Docker / Docker Compose

---

## 📁 Estructura Modular

com.jusep1983.blackjack
├── config # Configuración general y seguridad
├── auth # Login, JWT, roles
├── player # Gestión de jugadores
├── game # Lógica del juego de blackjack
├── shared # Excepciones, respuestas comunes, enums

yaml
Copiar
Editar

---

## 🧪 Pruebas

Puedes ejecutar los tests con:

```bash
./mvnw test
🐳 Próximamente
Frontend React con login y modo oscuro 🎨

Sistema de apuestas y multijugador

Seguridad con JWT + control de acceso

👨‍💻 Autor
Proyecto desarrollado por @Jusep1983 dentro del bootcamp Java Backend – IT Academy
