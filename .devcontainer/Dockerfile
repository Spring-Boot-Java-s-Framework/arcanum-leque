# Base comum dos 4 dev containers de frente (job / company / person / statistics).
#
# Por que um Dockerfile e não só "image":
#   A imagem mcr.microsoft.com/devcontainers/java embute um apt source do Yarn
#   (dl.yarnpkg.com) cuja chave GPG está quebrada (NO_PUBKEY 62D54FD4003F6525).
#   Qualquer `apt-get update` — inclusive o das Features (docker-in-docker etc.) —
#   falha por causa disso. Removemos o source ANTES das Features rodarem
#   (o Dockerfile do usuário é construído primeiro; as Features vêm depois).

ARG BASE=mcr.microsoft.com/devcontainers/java:1-21-bookworm
FROM ${BASE}

RUN rm -f /etc/apt/sources.list.d/yarn.list \
 && apt-get update \
 && rm -rf /var/lib/apt/lists/*
