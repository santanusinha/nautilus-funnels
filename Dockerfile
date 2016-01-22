FROM ubuntu:14.04


RUN \
  apt-get install -y --no-install-recommends software-properties-common \
  && add-apt-repository ppa:webupd8team/java \
  && apt-get update \
  && echo debconf shared/accepted-oracle-license-v1-1 select true |  debconf-set-selections \
  && echo debconf shared/accepted-oracle-license-v1-1 seen true |  debconf-set-selections \
  && apt-get install -y --no-install-recommends oracle-java8-installer

EXPOSE 8080
EXPOSE 8081

VOLUME /var/log/entitystore

ADD src/main/resources/elasticsearch.yml elasticsearch.yml
ADD src/main/resources/dev-config.yml dev-config.yml
ADD target/nautilus*.jar server.jar

CMD sh -c "java -jar -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl -XX:+${GC_ALGO} -Xms${JAVA_PROCESS_MIN_HEAP} -Xmx${JAVA_PROCESS_MAX_HEAP} server.jar server ${CONFIG_ENV}-config.yml"

