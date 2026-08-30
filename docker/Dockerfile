# syntax=docker/dockerfile:1

# ============================================================================
# arcanum-leque · Dockerfile multi-stage
#   base       → imagem base + workdir + dependências Maven em cache
#   debug      → base + ferramentas de debug/observação + testes + hot reload
#   test       → base + código, roda `mvnw verify` (unit + integração)
#   builder    → build "leve" (profile `light`) + JRE mínima via jlink
#   production → só o essencial: runtime jlink + camadas da app + healthcheck
#
# Build tool: Maven (via wrapper ./mvnw) — decisão do time.
# Contexto de build = RAIZ do repo; o projeto Java está em academic/src/.
# Sem banco: persistência é lista em memória.
# ============================================================================

ARG JAVA_VERSION=21
ARG APP_PORT=8080
ARG DEBUG_PORT=5005

# ----------------------------------------------------------------------------
# base — imagem base comum e workdir; resolve dependências uma única vez
# ----------------------------------------------------------------------------
FROM eclipse-temurin:${JAVA_VERSION}-jdk-jammy AS base
WORKDIR /app

# Copia só o necessário para baixar dependências (melhor cache de camadas)
COPY academic/src/mvnw ./
COPY academic/src/.mvn/ .mvn/
COPY academic/src/pom.xml ./
RUN chmod +x mvnw \
 && ./mvnw -B -q dependency:go-offline

# ----------------------------------------------------------------------------
# debug — mesma base, com dependências de debug, boas práticas e testes
# ----------------------------------------------------------------------------
FROM base AS debug
ARG APP_PORT
ARG DEBUG_PORT

# Ferramentas de diagnóstico dentro do container
RUN apt-get update \
 && apt-get install -y --no-install-recommends \
      curl \
      wget \
      git \
      procps \
      htop \
      less \
      vim-tiny \
      jq \
      netcat-openbsd \
      dnsutils \
      tini \
 && rm -rf /var/lib/apt/lists/*

# Baixa também as dependências de teste e plugins usados no ciclo de vida
RUN ./mvnw -B -q dependency:go-offline -Dsilent=true -DincludeScope=test || true

COPY academic/src/main/ main/
COPY academic/src/test/ test/

# Hot reload ligado. O JDWP (porta de debug) é ativado só pelo serviço `web`
# do compose, via `-Dspring-boot.run.jvmArguments` — NÃO via JAVA_TOOL_OPTIONS,
# senão a JVM do Maven e a JVM da app tentam abrir a mesma porta e a app quebra.
ENV SPRING_DEVTOOLS_RESTART_ENABLED=true

EXPOSE ${APP_PORT} ${DEBUG_PORT}
ENTRYPOINT ["tini", "--"]
CMD ["./mvnw", "-B", "spring-boot:run"]

# ----------------------------------------------------------------------------
# test — roda a suíte completa; usado pelo serviço `test` do compose
# ----------------------------------------------------------------------------
FROM base AS test
COPY academic/src/main/ main/
COPY academic/src/test/ test/
ENV SPRING_PROFILES_ACTIVE=test
CMD ["./mvnw", "-B", "verify"]

# ----------------------------------------------------------------------------
# builder — build final "comprimido para o essencial"
#   1. profile `light`: AOT + classes sem símbolos de debug + sem DevTools
#   2. extrai as camadas do jar (layertools)
#   3. jlink: JRE sob medida, só com os módulos que a app usa
# ----------------------------------------------------------------------------
FROM base AS builder
COPY academic/src/main/ main/
COPY academic/src/test/ test/

# 1 + 2 — jar leve e em camadas
RUN ./mvnw -B -q -Plight clean package -DskipTests \
 && mkdir -p target/extracted \
 && java -Djarmode=layertools -jar target/arcanum-leque.jar extract --destination target/extracted

# 3 — runtime mínimo. Lista de módulos generosa mas enxuta; regenerar com:
#     jdeps --print-module-deps --ignore-missing-deps --multi-release 21 target/arcanum-leque.jar
RUN "$JAVA_HOME/bin/jlink" \
      --add-modules java.base,java.logging,java.naming,java.desktop,java.management,java.instrument,java.security.jgss,java.security.sasl,java.sql,java.xml,java.net.http,java.prefs,jdk.management,jdk.jfr,jdk.crypto.ec,jdk.crypto.cryptoki,jdk.unsupported,jdk.zipfs \
      --strip-debug \
      --no-header-files \
      --no-man-pages \
      --compress zip-6 \
      --output /javaruntime

# ----------------------------------------------------------------------------
# production — imagem final mínima: runtime jlink + camadas da aplicação
# ----------------------------------------------------------------------------
FROM debian:12-slim AS production
ARG APP_PORT

ENV JAVA_HOME=/opt/java
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# curl (healthcheck) + tini (PID 1) + CA certs; usuário não-root
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl tini ca-certificates \
 && rm -rf /var/lib/apt/lists/* \
 && groupadd --system spring \
 && useradd --system --gid spring --home-dir /app --create-home spring

# JRE sob medida vinda do jlink
COPY --from=builder /javaruntime ${JAVA_HOME}

WORKDIR /app

# Camadas do Spring Boot, da menos volátil para a mais volátil
COPY --from=builder --chown=spring:spring /app/target/extracted/dependencies/ ./
COPY --from=builder --chown=spring:spring /app/target/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /app/target/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /app/target/extracted/application/ ./

USER spring:spring

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseContainerSupport"
ENV SPRING_PROFILES_ACTIVE=production
ENV APP_PORT=${APP_PORT}
EXPOSE ${APP_PORT}

# Healthcheck: sequência de comandos, a cada 10s, até 10 tentativas.
HEALTHCHECK --interval=10s --timeout=5s --retries=10 --start-period=30s \
  CMD curl -fsS "http://localhost:${APP_PORT}/actuator/health" \
      || curl -fsS "http://localhost:${APP_PORT}/estatisticas" \
      || curl -fsS "http://localhost:${APP_PORT}/vagas" \
      || exit 1

ENTRYPOINT ["tini", "--", "sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
