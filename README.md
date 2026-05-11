# Ordem de Serviço API

![Java 21](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Docker](https://img.shields.io/badge/Docker-ready-blue)
![Tests](https://img.shields.io/badge/Tests-Testcontainers-success)
![CI](https://img.shields.io/badge/CI-GitHub%20Actions-black)

API REST para gestão corporativa de chamados e ordens de serviço, construída com Java 21, Spring Boot, PostgreSQL, Flyway, autenticação JWT, controle de perfis, auditoria, histórico de status, paginação, métricas operacionais e testes de integração com Testcontainers.

## Objetivo do Projeto

Este projeto simula um sistema backend corporativo com regras de negócio reais, segurança, auditoria, versionamento otimista, documentação OpenAPI e testes automatizados. A proposta é demonstrar uma API de portfólio com práticas próximas às usadas em aplicações profissionais.

## Principais Funcionalidades

- Autenticação JWT
- Controle de perfis `ADMIN`, `TECNICO` e `SOLICITANTE`
- CRUD de usuários, departamentos e técnicos
- Abertura de ordens de serviço
- Atribuição de técnico
- Início, finalização e cancelamento de OS
- Histórico de transições de status
- Auditoria com `atualizadoPor` e `atualizadoEm`
- Paginação e filtros
- Métricas operacionais e SLA
- Versionamento otimista com `@Version`
- Soft delete lógico
- Documentação Swagger/OpenAPI
- Testes com PostgreSQL real via Testcontainers
- CI com GitHub Actions

## Stack

- Java 21
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- PostgreSQL
- Flyway
- Docker Compose
- Swagger/OpenAPI
- JUnit
- MockMvc
- Testcontainers
- GitHub Actions

## Arquitetura

O projeto segue uma organização em camadas:

| Camada | Responsabilidade |
|---|---|
| `controller` | Expõe os endpoints REST, valida requests e aplica regras de autorização por perfil |
| `dto` | Define objetos de entrada e saída da API |
| `service` | Centraliza regras de negócio e orquestra operações |
| `repository` | Acessa o banco com Spring Data JPA |
| `entity` | Mapeia as tabelas do banco com JPA |
| `exception` | Padroniza respostas de erro da API |
| `security` | Implementa autenticação JWT e integração com Spring Security |
| `config` | Configura OpenAPI/Swagger |

## Regras de Negócio

- Toda ordem de serviço nasce com status `ABERTA`
- Apenas OS `ABERTA` pode ser iniciada
- Apenas OS `EM_ANDAMENTO` pode ser finalizada
- Finalização exige `descricaoTecnica`
- OS `FINALIZADA` ou `CANCELADA` não pode seguir fluxo inválido
- Técnico e departamento precisam estar ativos para serem usados
- Alterações de status geram registros de histórico
- Versionamento otimista evita conflitos concorrentes em atualizações

## Perfis de Acesso

| Perfil | Permissões principais |
|---|---|
| `ADMIN` | Gerencia usuários, departamentos, técnicos, ordens, métricas e fluxos operacionais |
| `TECNICO` | Consulta dados operacionais, inicia e finaliza ordens de serviço |
| `SOLICITANTE` | Abre ordens de serviço e cancela ordens conforme regras atuais |

## Endpoints Principais

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/auth/login` | Autentica usuário e retorna JWT |
| `POST` | `/usuarios` | Cria usuário |
| `GET` | `/usuarios` | Lista usuários ativos |
| `POST` | `/departamentos` | Cria departamento |
| `GET` | `/departamentos` | Lista departamentos ativos |
| `POST` | `/tecnicos` | Cria técnico |
| `GET` | `/tecnicos` | Lista técnicos ativos |
| `POST` | `/ordens-servico` | Cria ordem de serviço |
| `GET` | `/ordens-servico` | Lista ordens com paginação e filtros |
| `GET` | `/ordens-servico/{id}` | Busca ordem por ID |
| `PATCH` | `/ordens-servico/{id}/atribuir-tecnico` | Atribui técnico à OS |
| `PATCH` | `/ordens-servico/{id}/iniciar` | Inicia OS |
| `PATCH` | `/ordens-servico/{id}/finalizar` | Finaliza OS |
| `PATCH` | `/ordens-servico/{id}/cancelar` | Cancela OS |
| `GET` | `/ordens-servico/{id}/historico` | Lista histórico da OS |
| `GET` | `/ordens-servico/metricas` | Consulta métricas operacionais |

## Como Rodar Localmente

```bash
git clone https://github.com/justmetro/ordem-servico-api.git
cd ordem-servico-api
docker compose up -d
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## Swagger/OpenAPI

Com a aplicação rodando, acesse:

```text
http://localhost:8080/swagger-ui/index.html
```

## Fluxo Rápido de Teste no Swagger

1. Criar um usuário `ADMIN` em `POST /usuarios`
2. Fazer login em `POST /auth/login`
3. Copiar o token retornado
4. Clicar em `Authorize`
5. Informar `Bearer TOKEN`
6. Testar os endpoints protegidos

## Exemplos JSON

### Criar Usuário ADMIN

```json
{
  "nome": "Administrador",
  "email": "admin@example.com",
  "senha": "admin123",
  "role": "ADMIN"
}
```

### Login

```json
{
  "email": "admin@example.com",
  "senha": "admin123"
}
```

### Criar Departamento

```json
{
  "nome": "Financeiro",
  "sigla": "FIN"
}
```

### Criar Técnico

```json
{
  "nome": "Carlos Silva",
  "email": "carlos.silva@example.com",
  "especialidade": "Infraestrutura"
}
```

### Criar OS

```json
{
  "titulo": "Computador não liga",
  "descricao": "Equipamento não apresenta sinal de energia.",
  "solicitante": "Maria Oliveira",
  "prioridade": "ALTA",
  "departamentoId": 1
}
```

### Finalizar OS

```json
{
  "descricaoTecnica": "Fonte substituída e equipamento validado com sucesso."
}
```

## Como Rodar Testes

```bash
./mvnw clean test
```

No Windows:

```powershell
.\mvnw.cmd clean test
```

Os testes usam Testcontainers com PostgreSQL real. É necessário estar com o Docker Desktop aberto para que os containers sejam iniciados automaticamente.

## Docker Compose

O `docker-compose.yml` sobe um PostgreSQL local para uso em desenvolvimento. Os testes automatizados não dependem desse banco local, pois usam Testcontainers.

## CI/CD

O projeto possui workflow de CI com GitHub Actions. A cada `push` ou `pull_request` para a branch `main`, o pipeline executa:

```bash
./mvnw clean test
```

Como os testes usam Testcontainers, o próprio workflow usa Docker disponível no runner do GitHub Actions.

## Pitch para Currículo

API REST em Java/Spring Boot para gestão corporativa de ordens de serviço, com autenticação JWT, PostgreSQL, Flyway, auditoria, histórico de status, paginação, métricas operacionais, Testcontainers, Docker e CI.

## Próximos Passos

- Melhorar escopo por usuário logado
- Adicionar refresh token
- Deploy
- Exportação CSV
- Dashboard frontend
