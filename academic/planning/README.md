# 🧭 academic/planning

<!-- badges -->
![versão](https://img.shields.io/badge/vers%C3%A3o-0.1.0--SNAPSHOT-blue)
![status](https://img.shields.io/badge/status-planejamento-orange)
![escopo](https://img.shields.io/badge/escopo-discovery_%2B_decis%C3%B5es-blueviolet)
![licença](https://img.shields.io/badge/licen%C3%A7a-MIT-green)
[![PT-BR](https://img.shields.io/badge/lang-PT--BR-yellow)](README.md)
[![EN](https://img.shields.io/badge/lang-EN-inactive)](README.en.md)

> 🌐 **Português** · [English](README.en.md)

Discovery e decisões do time **antes de codar** — o entendimento do produto, as
perguntas levantadas e o que foi combinado.

---

## 🧭 planning/ vs references/

```mermaid
flowchart LR
    P["planning/ · contexto, personas, decisões · evolui"] -->|informa| R["references/ · teoria e enunciado · estável"]
```

| | `planning/` | `references/` |
| --- | --- | --- |
| **O que é** | entendimento e acordos do time | teoria + enunciado |
| **Muda quando** | a cada conversa/decisão | quase nunca |
| **Autoridade** | decisões do time vencem suposições | fonte da verdade da arquitetura |

---

## 📄 Documentos

| Arquivo | Assunto |
| ------- | ------- |
| [`contexto-e-publico-alvo.md`](contexto-e-publico-alvo.md) | Tema do projeto, relação das frentes com o tema, ausência de enredo de negócio, público-alvo, faixa etária, leigo vs. técnico, personas, regras duras e **decisões do time** |

---

## ✅ Registro de decisões

| # | Decisão | Motivo | Impacto |
| - | ------- | ------ | ------- |
| D1 | **Persistência = lista em memória** (sem banco) | ~12 vagas, 5 empresas, 6 pessoas, dados fixos — banco real seria over-engineering | `docker/` e `.devcontainer/` não sobem SGBD; só o repository conhece a origem dos dados |
| D2 | **`/estatisticas` agrega só vagas** | segue o enunciado; evita acoplar a frente 4 às outras três | `EstatisticasService` injeta apenas `VagaRepository` |
| D3 | **Vaga ↔ Empresa é vínculo de domínio, não de código** | `empresaSlug` é `String` (por valor) → frentes 1 e 2 andam em paralelo | nenhum `import` entre as frentes; validação de slug fica para aula futura |
| D4 | **Build = Maven** (wrapper `./mvnw`) | ferramenta única, padrão do Spring Initializr; evita manter dois caminhos de build | `docker/` e `.devcontainer/` usam só Maven; sem Gradle no projeto |
| D5 | **Projeto Java em `academic/src/`**, pacote base `br.edu.faculdade`, pacotes de frente em **inglês** (`job`/`company`/`person`/`statistics`) | estrutura montada pelo dono do repo; só o **pacote** é inglês — classes e campos seguem o PT do enunciado (`VagaRepository`, `todas()`, …) | `.dockerignore` (raiz, único) deixa só `academic/src/` no contexto; layout Maven não-padrão (`pom.xml` + `main/`) via `<sourceDirectory>` |
| D6 | **Build "leve" = profile `light`** (`./mvnw -Plight`) + **jlink** no stage de produção | pedido: "transformar tudo num arquivo Java mais leve" | AOT + classes sem símbolos de debug + sem DevTools; imagem de produção com JRE sob medida |
| D7 | **Fluxo = fork + PR, GitFlow enxuto** (`main` · `develop` · `feat/<frente>`) | squad pequena, dono único de merge; `main` sempre avaliável | contribuidores dão fork, PR para `develop`; `develop → main` via PR do Carlos. Detalhe em [`../../.github/CONTRIBUTING.md`](../../.github/CONTRIBUTING.md) |

---

## 🗺️ Ver também

- [`../README.md`](../README.md) · [`../../README.md`](../../README.md)
- [`../references/README.md`](../references/README.md) — teoria e enunciado
- [`CLAUDE.md`](CLAUDE.md)
