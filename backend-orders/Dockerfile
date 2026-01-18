FROM ghcr.io/graalvm/native-image-community:25 AS build

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

COPY src ./src

RUN chmod +x ./gradlew

RUN ./gradlew nativeCompile

FROM alpine:latest

RUN apk add --no-cache gcompat libc6-compat

WORKDIR /app

COPY --from=build /app/build/native/nativeCompile/orders .

EXPOSE 8080

ENTRYPOINT ["./orders"]
