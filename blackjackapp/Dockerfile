# Imagen base con JDK 21
FROM eclipse-temurin:21-jdk

# Directorio de trabajo
WORKDIR /app

# Copiar los archivos necesarios
COPY pom.xml .
COPY src ./src

# Instalar Maven y construir (sin tests)
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests && \
    apt-get remove -y maven && apt-get autoremove -y

# Exponer el puerto
EXPOSE 8080

# Ejecutar la app
CMD ["java", "-jar", "target/blackjackapp-0.0.1-SNAPSHOT.jar"]
