FROM eclipse-temurin:17 as build

WORKDIR /app
COPY . .

# Gradlew 스크립트에 실행 권한을 부여합니다.
RUN chmod +x gradlew 

# Gradle 빌드를 수행하고, JAR 파일을 적절한 위치로 이동합니다.
RUN ./gradlew bootJar && mv build/libs/*.jar app.jar

# 새로운 스테이지
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /app/app.jar .

CMD ["java", "-jar", "app.jar"]
EXPOSE 8080
