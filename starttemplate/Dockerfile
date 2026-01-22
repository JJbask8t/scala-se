
# FROM sbtscala/scala-sbt:eclipse-temurin-jammy-21.0.2_1.9.9_3.4.0

FROM sbtscala/scala-sbt:eclipse-temurin-jammy-21.0.2_13_1.10.4_3.5.2

# Installing libraries for GUI (Swing/AWT) operation inside a Linux container
RUN apt-get update && \
    apt-get install -y libxrender1 libxtst6 libxi6 libxext6 && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY . /app

RUN sbt compile

# 6. Команда запуска приложения
CMD ["sbt", "run"]