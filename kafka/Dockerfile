FROM wurstmeister/kafka:0.10.1.0
MAINTAINER A. Mahmood (arshadm@spinor.io)

ENV KAFKA_CONFIG=$KAFKA_HOME/config/server.properties
ENV GRAPHITE_JAR="kafka_2.11-graphite-0.4.1.jar"
ENV METRICS_JAR="metrics-graphite-2.2.0.jar"

RUN wget -q http://search.maven.org/remotecontent?filepath=com/pakulov/kafka/kafka_2.11-graphite/0.4.1/${GRAPHITE_JAR} -O /opt/kafka_${SCALA_VERSION}-${KAFKA_VERSION}/libs/${GRAPHITE_JAR}
RUN wget -q http://search.maven.org/remotecontent?filepath=com/yammer/metrics/metrics-graphite/2.2.0/${METRICS_JAR} -O /opt/kafka_${SCALA_VERSION}-${KAFKA_VERSION}/libs/${METRICS_JAR}
