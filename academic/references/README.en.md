# 📖 academic/references

<!-- badges -->
![version](https://img.shields.io/badge/version-0.1.0--SNAPSHOT-blue)
![status](https://img.shields.io/badge/status-stable-brightgreen)
![docs](https://img.shields.io/badge/docs-4-informational)
![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?logo=springboot&logoColor=white)
[![PT-BR](https://img.shields.io/badge/lang-PT--BR-inactive)](README.md)
[![EN](https://img.shields.io/badge/lang-EN-yellow)](README.en.md)

> 🌐 [Português](README.md) · **English**

Theory that underpins **arcanum-leque**, organized from the *Introduction to
Spring Boot* course (Um Leque de Tecnologia), lesson *Beans and Dependency
Injection*.

---

## 📚 Index

| # | File | Topic | Read when… |
| - | ---- | ----- | ---------- |
| 01 | [`01-ioc-e-injecao-de-dependencia.en.md`](01-ioc-e-injecao-de-dependencia.en.md) | IoC, container, beans, injection types | you want to understand *why* constructor injection |
| 02 | [`02-estereotipos-spring.en.md`](02-estereotipos-spring.en.md) | `@Component`, `@Service`, `@Repository`, `@RestController`, `@Bean` | you are about to annotate a class |
| 03 | [`03-arquitetura-em-tres-camadas.en.md`](03-arquitetura-em-tres-camadas.en.md) | `controller → service → repository` | you are deciding what goes in each layer |
| 04 | [`04-desafio-02-leque-de-vagas.en.md`](04-desafio-02-leque-de-vagas.en.md) | Full statement of Challenge 02 | you need exact fields, data minimums, routes |

> 🧭 Product context, audience and **team decisions** live in
> [`../planning/`](../planning/README.en.md), not here.

---

## 🗺️ Where to start

```mermaid
flowchart TD
    P["planning/ · product context"] --> S04["04 · challenge statement"]
    S04 --> S03["03 · three layers"]
    S03 --> S01["01 · IoC and injection"]
    S03 --> S02["02 · Spring stereotypes"]
    S01 --> CODE["code your front"]
    S02 --> CODE
```

---

## ✅ Rules these docs lock in

- **Constructor injection only**, `final` field; **no `new`** between classes.
- Stereotypes: `@Repository` / `@Service` / `@RestController`.
- No business logic in the controller; `record`s without annotations.
- `empresaSlug` (job) == `slug` (company), identical string.
- No password until lesson 10.
- Names required by the statement stay in **Portuguese**.

---

## 🔗 Source

Lesson 02 — *Beans and Dependency Injection*
<https://aulas.umlequedetecnologia.com.br/cursos/introducao-spring-boot/aula-02-beans-e-injecao/desafio>

## 🗺️ See also

- [`../README.en.md`](../README.en.md) · [`../../README.en.md`](../../README.en.md)
- [`../planning/README.en.md`](../planning/README.en.md) — context and decisions
- [`CLAUDE.md`](CLAUDE.md) (PT only)
