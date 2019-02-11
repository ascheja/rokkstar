FROM azul/zulu-openjdk:8u192 as compile
WORKDIR /compile
COPY . /compile
RUN ./gradlew build
WORKDIR /compile/build/distributions
RUN tar xf rokkstar-1.0-SNAPSHOT.tar

FROM azul/zulu-openjdk:8u192
COPY --from=compile /compile/build/distributions/rokkstar-1.0-SNAPSHOT /opt/rokkstar
ENTRYPOINT ["/opt/rokkstar/bin/rokkstar"]
