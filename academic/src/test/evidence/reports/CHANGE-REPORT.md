# Change report: Vaga (job) front

- **Author:** Carlos Neto (CarlosNeto-dev)
- **Date:** 2026-10-13
- **Work window:** 02:41 to 02:56 (UTC-03:00), in three stages (see section 7)
- **Branch:** `develop` (changes **not committed yet**; they are staged in Git)
- **Final test run:** 2026-10-13, 02:54:51 to 02:55:53 (UTC-03:00), inside a
  `maven:3.9-eclipse-temurin-21` container (Java 21.0.12, Maven 3.9)

## 1. Result

| Suite | Tests | Failures | Errors |
|---|---:|---:|---:|
| `VagaServiceTest` (business rules, no Spring) | 30 | 0 | 0 |
| `VagaControllerTest` (HTTP + error handling) | 13 | 0 | 0 |
| `SmokeControllerTest` (already existed) | 1 | 0 | 0 |
| **Total** | **44** | **0** | **0** |

`BUILD SUCCESS`. Evidence in this folder:

- `logs/`: Maven output of the final run.
- `surefire/`: one Surefire report per test class.
- `reports/`: this report.

## 2. Final structure

```
faculdade/vaga/
├── model/        Vaga
├── dto/          VagaEntrada, VagaResposta
├── controller/   VagaController
├── service/      VagaService
├── repository/   VagaRepository
└── exception/    RegraDeNegocioException, VagaDuplicadaException, VagaNaoEncontradaException
```

Tests mirror it under `src/test/java/.../vaga/controller` and `.../vaga/service`.

## 3. What was renamed

| Before | After |
|---|---|
| package `faculdade/job/` | `faculdade/vaga/` |
| package `faculdade/company/` | `faculdade/empresa/` |
| package `faculdade/statistics/` | `faculdade/estatisticas/` |
| `Job.java` | `vaga/model/Vaga.java` |
| `JobAwnser.java` (typo) | `vaga/dto/VagaResposta.java` |
| `JobEntry.java` | `vaga/dto/VagaEntrada.java` |
| `JobController.java` | `vaga/controller/VagaController.java` |
| `JobService.java` | `vaga/service/VagaService.java` |
| `JobRepository.java` | `vaga/repository/VagaRepository.java` |
| `src/test/evidencias/` | `src/test/evidence/` (and `relatorios` to `reports`) |

The old files had a name different from the public class inside them
(`Job.java` held `record Vaga`), which made the project not compile. Each file
now matches its class.

References updated because of the folder moves: `.github/CODEOWNERS` (comments),
`.github/workflows/release.yml` (search for `estatisticas/`),
`.github/CONTRIBUTING.md` and `.en.md` (the "only the package is English" rule no
longer applied) and the JavaDoc of `Application.java`.

Kept as they were: `config/`, `smoke/` (temporary scaffolding),
`.devcontainer/{job,company,person,statistics}` (folder names are referenced by
the container setup), `.github/`, `academic/`, `src/main`, `src/test`, `resources`.

## 4. What was removed

- `job/Job.java`, `job/JobAwnser.java`, `job/JobController.java`,
  `job/JobEntry.java`, `job/JobService.java` (replaced by the `Vaga*` files).
- `pessoa/.gitkeep` (the folder already has code; the removal came from the
  earlier pull).
- In `VagaController`: the `Optional.map(...).orElse(notFound)` chains and the
  `boolean` returned by `apagar`. The "not found" decision moved from the
  controller to exceptions in the service.
- The old JavaDocs (they mentioned "aula 03/04" and the `Job*` layout, and one
  claimed "Jackson 3 / Boot 4", which does not match the `pom.xml`, Spring Boot
  3.3.5). All were rewritten with `@author Carlos Neto`.
- All HTML tags (`<p>`, `<b>`, `<i>`, `<ul>`, `<li>`, `<h2>`, `<pre>`) from the
  JavaDocs I changed. Only JavaDoc's own tags remain (`{@code}`, `{@link}`,
  `@author`, `@since`). `SmokeController` was not touched.

## 5. What was added

**New files in `vaga/exception/`:** `VagaNaoEncontradaException` (404),
`VagaDuplicadaException` (409), `RegraDeNegocioException` (422).
**`config/TratadorDeErros`:** three new handlers returning `ProblemDetail`.

**Rules in `VagaService`:**

| Rule | Response when broken |
|---|---|
| null or blank id | 422 |
| missing vaga (get, replace, delete) | 404 |
| null input | 422 |
| title 5 to 120 chars; area up to 60; location up to 80; description up to 2000 | 422 |
| surrounding spaces trimmed; empty optional field becomes `null` | normalized |
| seniority only Estágio / Júnior / Pleno / Sênior (any case, accents optional; stored in canonical form) | 422 |
| company slug only `a-z`, `0-9` and hyphens (lower-cased on input) | 422 |
| Pleno or Sênior cannot accept beginners; missing `aceitaIniciante` means `false` | 422 |
| same company and same title (ignoring case and accents) | 409 |
| `criar`, `trocar` and `apagar` synchronized (duplicate check and write are one step) | n/a |
| logging of create, replace and delete (only id and company) | n/a |

**Security:** `descricao` stays out of the response; an `id` sent by the client
is ignored (the API generates the UUID); a failed rule writes nothing.

## 6. Test cases (44)

`VagaServiceTest`: list (12 initial) · get by id (existing, with spaces,
missing, null/blank) · create (UUID and persistence, normalization, free-form
seniority, null `aceitaIniciante`, null input, short/long title, long
description, invalid seniority, invalid slug, Pleno/Sênior with beginner,
duplicate with other case/accent, same title at another company, failure does
not persist) · replace (keeps id, same title on the same vaga, duplicate,
missing, blank id, invalid input leaves the vaga untouched) · delete (removes,
twice, null id) · response has no `descricao` · 20 concurrent creations of the
same title (1 passes, 19 become duplicates).

`VagaControllerTest`: GET list / by id / 404 · POST 201 with `Location`, client
id ignored, 400 with field list, 400 without body, 422 (seniority and beginner),
409 · PUT 200 / 404 · DELETE 204, repeat gives 404.

The test method names and `@DisplayName` texts are in Portuguese, like the rest
of the class names.

## 7. Stages

1. **02:41 to 02:47:** rename to Portuguese, new `VagaService` rules, new
   exceptions, JavaDocs, tests. First run: 44/44.
2. **02:48 to 02:52:** `vaga` split into subfolders and HTML removed from
   JavaDocs. Run at 02:51: 44/44.
3. **02:53 to 02:56:** subfolders renamed to English (`model`, `controller`,
   `service`, `repository`, `exception`) and the evidence folder translated.
   Final run at 02:54:51: 44/44. The logs in this folder are from that run; the
   earlier runs were replaced.

## 8. Limitations and notes

- **Java and Maven are not installed on this machine.** Tests ran in Docker
  (`docker run maven:3.9-eclipse-temurin-21 mvn clean test`). Docker Desktop was
  started during the task.
- The `src/main/test` folder mentioned in the request does not exist; I used
  `academic/src/test` (the `testSourceDirectory` in `pom.xml`).
- **Pre-existing problem, not caused by this change:** the CI job in
  `.github/workflows/release.yml` fails the build if `pom.xml` contains
  `spring-boot-starter-validation`, and the current `pom.xml` does. Worth
  reviewing before the next push.
- Data is still in memory: restarting the application restores the 12 vagas.
- `empresa/` and `estatisticas/` only have `.gitkeep`; whoever owns those fronts
  must use the new package names.
