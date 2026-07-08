# ---------- Build ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# 先複製 pom.xml
COPY pom.xml .

# 下載 Maven 相依套件
RUN mvn dependency:go-offline

# 複製全部程式
COPY src ./src

# 編譯
RUN mvn clean package -DskipTests


# ---------- Run ----------
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java","-jar","app.jar"]