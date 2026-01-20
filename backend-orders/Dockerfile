# BASE IMAGE FOR BUILDING THE APPLICATION
FROM ghcr.io/graalvm/native-image-community:25 AS builder
WORKDIR /app
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true
COPY src src

# COMPILE JAR
FROM builder AS build-java
RUN ./gradlew bootJar -x test --no-daemon

# COMPILE NATIVE IMAGE
FROM builder AS build-native
RUN ./gradlew nativeCompile -x test --no-daemon

# FINAL IMAGE FOR JAVA APPLICATION
FROM eclipse-temurin:25-jre-alpine AS final-java
WORKDIR /app
COPY --from=build-java /app/build/libs/*.jar orders.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "orders.jar"]

# FINAL IMAGE FOR NATIVE APPLICATION
FROM alpine:latest AS final-native
RUN apk add --no-cache gcompat libc6-compat
WORKDIR /app
COPY --from=build-native /app/build/native/nativeCompile/orders .

EXPOSE 8080
ENTRYPOINT ["./orders"]
