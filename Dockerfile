# Используем официальный образ Java 21
FROM eclipse-temurin:21-jdk

# Рабочая директория внутри контейнера
WORKDIR /

# Копируем JAR-файл (предварительно собранный через Maven/Gradle)
COPY web_hockey-0.0.1-SNAPSHOT.jar app.jar

# Указываем порт, который будет слушать приложение
EXPOSE 8080

# Команда запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]

COPY application.yml ./config/
