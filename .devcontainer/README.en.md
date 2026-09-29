# 💻 .devcontainer

<!-- badges -->
![version](https://img.shields.io/badge/version-0.1.0--SNAPSHOT-blue)
![status](https://img.shields.io/badge/status-planning-orange)
![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![build](https://img.shields.io/badge/build-Maven-C71A36?logo=apachemaven&logoColor=white)
![editor](https://img.shields.io/badge/VS_Code_%2F_Codespaces-ready-007ACC?logo=visualstudiocode&logoColor=white)
[![PT-BR](https://img.shields.io/badge/lang-PT--BR-inactive)](README.md)
[![EN](https://img.shields.io/badge/lang-EN-yellow)](README.en.md)

> 🌐 [Português](README.md) · **English**

Reproducible development environment (VS Code / GitHub Codespaces) for
**arcanum-leque** — the *Leque de Vagas* backend (Challenge 02: Spring Boot API
with in-memory data).

---

## ⚙️ What is configured

| Item | Value |
| ---- | ----- |
| Base | `mcr.microsoft.com/devcontainers/java:1-21-bookworm` via `.devcontainer/Dockerfile` (pinned — the `:21` tag became trixie and breaks) |
| Java project | [`academic/src/`](../academic/src/) — `pom.xml` + `main/` (auto-detected) |
| Build | **Maven** via the `./mvnw` wrapper (the image already has Java 21) |
| Features | **none** — keeps it simple and breakage-proof |
| Docker | ❌ inside the container. The [`../docker/`](../docker/) stack runs on the host |
| Port | `8080`–`8083` (one per track) — no database, in-memory data |
| Extensions | Java Pack, Spring Boot Dev Pack, YAML, EditorConfig |

---

## 🧩 One dev container per track

**There is no root `devcontainer.json` in `.devcontainer/`** — only one per
track, each in its own folder, all built from the shared `.devcontainer/Dockerfile`.
Only `name`, the `FRENTE` var and the port change. The **folder names are in
English**:

| Config | Track | `FRENTE` | Port |
| ------ | ----- | -------- | ---- |
| `.devcontainer/job/` | Vaga | `vaga` | 8080 |
| `.devcontainer/company/` | Empresa | `empresa` | 8081 |
| `.devcontainer/person/` | Pessoa | `pessoa` | 8082 |
| `.devcontainer/statistics/` | Estatísticas | `estatisticas` | 8083 |

Distinct ports allow running tracks **in parallel**. In VS Code: palette →
*Dev Containers: Reopen in Container* → pick the track.

---

## ▶️ How to open

- **VS Code:** palette → *Dev Containers: Reopen in Container* → pick the track
- **GitHub Codespaces:** *Code ▸ Create codespace on…* → pick the config

```mermaid
flowchart LR
    repo["repo"] --> dc[".devcontainer/FRENTE/devcontainer.json"]
    dc --> df[".devcontainer/Dockerfile · Java 21 + apt fix"]
    df --> run["port 8080-8083 · ./mvnw spring-boot:run"]
```

---

## 🗺️ See also

- [`../README.en.md`](../README.en.md#-build) — build: **Maven**
- [`CLAUDE.md`](CLAUDE.md) (PT only) · [`../docker/README.en.md`](../docker/README.en.md)
