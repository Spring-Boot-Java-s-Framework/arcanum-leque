<!--
  Título no padrão Conventional Commits: "feat(vaga): record + repository + rotas".
  Um PR por frente / por passo lógico. Mantenha pequeno e revisável.
  Base do PR = `develop` (nunca `main`). Origem = sua branch feat/* no seu fork.
  Fluxo completo: .github/CONTRIBUTING.md
-->

## O que este PR faz

<!-- 1–3 frases. Qual frente / qual parte do Desafio 02. -->

## Frente

- [ ] Vaga
- [ ] Empresa
- [ ] Pessoa
- [ ] Estatísticas
- [ ] Infra / documentação

## Checklist do Desafio 02

- [ ] Injeção de dependência **apenas por construtor**, em campo `final`
- [ ] **Nenhum `new`** entre classes do projeto
- [ ] Estereótipos corretos: `@Repository` / `@Service` / `@RestController`
- [ ] **Sem regra de negócio no controller** (nada de `if`/`for` de decisão)
- [ ] `record`s **sem** anotação do Spring
- [ ] Nomes exigidos pelo enunciado em português (`titulo`, `todas()`, `porArea`, …)
- [ ] Persistência é **lista em memória** (sem banco / sem JPA)
- [ ] As rotas da frente respondem no Postman
- [ ] `empresaSlug` das vagas casa com `slug` das empresas (se aplicável)
- [ ] Números de `/estatisticas` batem com `/vagas` (se aplicável)

## Checklist geral

- [ ] Fork + branch `feat/<frente>`; **base do PR = `develop`**
- [ ] Commits no padrão Conventional Commits
- [ ] Branch sincronizada com `upstream/develop` (rebase, sem conflitos)
- [ ] `README.md` atualizado se mudou algo relevante (equipe, rotas, decisões)
- [ ] Decisão nova registrada em `academic/planning/` (PT **e** `.en.md`)
- [ ] Documentação bilíngue mantida em par (`*.md` + `*.en.md`)

## Como testar

```bash
# ex.:
# cd academic/src && ./mvnw spring-boot:run
# curl http://localhost:8080/vagas
```

## Notas para quem revisa

<!-- Pontos de atenção, decisões em aberto, o que NÃO revisar aqui. -->
