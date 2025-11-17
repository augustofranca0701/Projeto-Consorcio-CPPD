# ================================
# STAGE 1 — BUILD (Maven + JDK)
# ================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copia apenas o pom primeiro para aproveitar cache de dependências
COPY pom.xml .
RUN mvn -e -B dependency:go-offline

# Agora copia o restante do código
COPY src ./src

# Compila o projeto (gera o jar dentro de /app/target)
RUN mvn -e -B clean package -DskipTests


# ================================
# STAGE 2 — RUNTIME (JDK enxuto)
# ================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia apenas o jar empacotado do stage anterior
COPY --from=build /app/target/*.jar app.jar

# Porta padrão da sua API
EXPOSE 8080

# Usa o profile definido pelo ambiente do Docker/Compose
CMD ["sh", "-c", "java -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar app.jar"]
