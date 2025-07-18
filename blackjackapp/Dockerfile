# Imagen base con JDK 21
FROM eclipse-temurin:21-jdk

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los ficheros de Maven al contenedor
COPY pom.xml .
COPY src ./src

# Instalamos Maven y construimos el proyecto (sin tests para acelerar)
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

# Expone el puerto 8080 (el que usa tu API)
EXPOSE 8080

# Comando para arrancar la app (ajusta el nombre del JAR si es distinto)
CMD ["java", "-jar", "target/blackjackapp-0.0.1-SNAPSHOT.jar"]
