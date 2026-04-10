# 1. Eclipse Temurin (рекомендованный релиз JRE 21)
FROM eclipse-temurin:21-jre

# Автор
LABEL maintainer="qabrago@gmail.com"

WORKDIR /app

# Копируем JAR (подбираем нужный)
COPY target/*.jar app.jar

# Открываем порт 8080
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]