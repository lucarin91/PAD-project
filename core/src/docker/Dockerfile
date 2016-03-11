FROM java:8
VOLUME /tmp
ADD core-0.1.jar app.jar
# RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-jar","app.jar"]
