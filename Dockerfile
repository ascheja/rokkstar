FROM azul/zulu-openjdk:8u192 as compile
WORKDIR /compile
COPY . /compile
RUN ./gradlew build
WORKDIR /compile/runner/build/distributions
RUN tar xf runner-1.0-SNAPSHOT.tar

FROM azul/zulu-openjdk:8u192
COPY --from=compile /compile/runner/build/distributions/runner-1.0-SNAPSHOT /opt/rokkstar
ENTRYPOINT ["/opt/rokkstar/bin/rokkstar"]
