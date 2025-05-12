FROM registry.access.redhat.com/ubi9/openjdk-21:latest
LABEL maintainer=bssolutions@kcbgroup.com
ENV PORT 8080
ENV TZ=Africa/Nairobi
COPY target/*.jar /app/application.jar
WORKDIR /app
# change user
USER root
# Update packages (if available)
RUN microdnf update -y
# Clean up
RUN microdnf clean all -y
# revert user
USER jboss
ENTRYPOINT exec java $JAVA_OPTS -jar application.jar
