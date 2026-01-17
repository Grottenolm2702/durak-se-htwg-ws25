FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y curl gnupg && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | \
    gpg --dearmor -o /usr/share/keyrings/sbt.gpg && \
    echo "deb [signed-by=/usr/share/keyrings/sbt.gpg] https://repo.scala-sbt.org/scalasbt/debian all main" > /etc/apt/sources.list.d/sbt.list && \
    apt-get update && apt-get install -y sbt libxrender1 libxtst6 libxi6 libxrandr2 libxcursor1 libgl1 libgtk-3-0 openjfx && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /durak

COPY build.sbt .
COPY project ./project

RUN sbt update

COPY src ./src

RUN sbt compile

CMD sbt run
