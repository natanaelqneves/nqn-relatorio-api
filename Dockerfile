# Estágio 1: Compilação (Build) da API usando Maven e Java 17
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY relatorios_semob .
# 🎯 Pula os testes de contexto locais para não travar a compilação na Render
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# Estágio 2: Execução da API usando uma imagem otimizada e segura da Eclipse Temurin
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Informa a porta interna padrão que o container escuta
EXPOSE 8080

# Executa o jar da aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]