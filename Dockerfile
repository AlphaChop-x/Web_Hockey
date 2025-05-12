# Используем официальный образ Java 17
FROM eclipse-temurin:21-jdk

# Рабочая директория внутри контейнера
WORKDIR /app

# Копируем JAR-файл (предварительно собранный через Maven/Gradle)
COPY target/web_hockey-0.0.1-SNAPSHOT.jar app.jar

# Указываем порт, который будет слушать приложение
EXPOSE 8080

# Команда запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]