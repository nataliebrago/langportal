# 1. Eclipse Temurin (рекомендованный релиз JRE 21)
FROM eclipse-temurin:21-jre

# Автор
LABEL maintainer="you@example.com"

# Копируем JAR в контейнер
COPY target/language-platform-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт 8080
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/app.jar"]