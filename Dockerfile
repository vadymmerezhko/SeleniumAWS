FROM ubuntu:latest

ENV HOME=/tmp
ENV DISPLAY=:D.S

# Update APT
RUN apt update
RUN apt upgrade -y

# Install Java 21
RUN apt install -y openjdk-21-jdk

# Install Playwright Firefox dependencies
RUN apt install -y libxkbcommon-x11-0
RUN apt install -y libgbm1
RUN apt install -y libgtk-3-0
RUN apt install -y libdbus-glib-1-2

COPY target/dependency/*.jar ./
COPY target/classes ./
COPY config.properties ./

ENTRYPOINT [ "/usr/bin/java", "-cp", "./*", "com.amazonaws.services.lambda.runtime.api.client.AWSLambda" ]

CMD [ "org.example.servers.TestServerRequestHandler::handleRequest" ]


