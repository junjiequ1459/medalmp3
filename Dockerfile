# 1) Build stage
FROM maven:3.8.8-eclipse-temurin-17 AS builder
WORKDIR /build

# Copy the entire multi‑module project
COPY . .

# Pre‑fetch only the dependencies needed for audio‑api (and anything it needs)
RUN mvn -B -pl audio-api -am dependency:go-offline

# Now compile & package just audio‑api
RUN mvn -B -f audio-api/pom.xml package -DskipTests

# 2) Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the fat‑jar from the builder
COPY --from=builder /build/audio-api/target/audio-api-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
