# Sistema de Cadastro de Pessoas

Um projeto de estudo abrangente implementando padrões modernos de arquitetura de software através de um sistema global de cadastro de pessoas e organizações baseado no padrão **Party Model** (inspirado em SAP, Oracle e Salesforce).

## Visão Geral do Projeto

Este é um **projeto focado em aprendizado** projetado para demonstrar a evolução da arquitetura de software de uma aplicação monolítica simples para um ecossistema de microserviços distribuído com event sourcing e padrões avançados de segurança.

### Domínio de Negócio

Um **sistema de cadastro universal** para:
- **Pessoas Físicas (PF)**: Indivíduos com nomes estruturados, documentos de identificação (CPF, SSN, Passaporte, etc.)
- **Pessoas Jurídicas (PJ)**: Empresas com razão social/nome fantasia, documentos de identificação (CNPJ, EIN, etc.)

Funcionalidades Principais:
- Padrão Party Model (entidade base unificada)
- Genérico e internacional (suporta qualquer país)
- Sistema flexível de identificação
- Exclusão lógica (soft delete) e trilha de auditoria
- API RESTful com documentação OpenAPI

## Evolução da Arquitetura (5 Fases)

### FASE 1: MVP Monolítico 🔄 (Em Desenvolvimento)
**Objetivo**: Construir API REST fundacional com arquitetura limpa

**Stack Atual**:
- Java 21 + Spring Boot 4.0.2
- PostgreSQL 16 + Liquibase migrations
- Podman/Docker para desenvolvimento local
- JUnit 5 + Mockito + TestContainers

**Implementado**:
- ✅ Party Model de 3 tabelas (party, person, organization) com BIGINT IDs
- ✅ Migrations Liquibase executadas com sucesso
- ✅ Banco PostgreSQL rodando em container
- ✅ Sequence para party_number (inicia em 1.000.000)
- ✅ Soft delete com campo `is_deleted` + `deleted_at`
- ✅ Auditoria com timestamps (created_at, updated_at)
- ✅ Check constraints para validação de tipos
- ✅ Índices otimizados para busca

**Pendente**:
- ⏳ Entidades JPA (Party, Person, Organization)
- ⏳ Repositories Spring Data JPA
- ⏳ Camada de serviço com padrão strategy de validação
- ⏳ API REST com DTOs e tratamento de exceções
- ⏳ Docker Compose multi-serviço
- ⏳ Suíte de testes abrangente (70% unit, 20% integration, 10% API)

---

### FASE 2: CQRS Simplificado (Futuro)
**Objetivo**: Separar leituras de escritas usando eventos em memória

**Mudanças**:
- Dividir em lados Command e Query
- Adicionar tabela `party_identification` (múltiplos documentos por party)
- Barramento de eventos em memória (Spring ApplicationEventPublisher)
- Modelos de leitura (views desnormalizadas)
- Padrão CQRS sem event store externo

**Novas Funcionalidades**:
- Pessoa pode ter CPF + RG + CNH + Passaporte
- Organização pode ter CNPJ + Inscrição Estadual + Inscrição Municipal
- Projeções de consulta otimizadas

---

### FASE 3: Event Sourcing (Futuro)
**Objetivo**: Armazenar eventos como fonte da verdade

**Adições ao Stack**:
- Axon Framework ou EventStoreDB
- Event store como banco de dados primário
- Reconstrução de estado a partir de eventos
- Capacidades de replay de eventos

**Novas Funcionalidades**:
- Histórico completo de auditoria
- Consultas de viagem no tempo
- Versionamento e upcasting de eventos

---

### FASE 4: Microserviços + Mensageria (Futuro)
**Objetivo**: Quebrar monólito em serviços independentes

**Adições ao Stack**:
- RabbitMQ para comunicação assíncrona
- Service mesh (Istio ou Linkerd)
- API Gateway (Kong)
- Rastreamento distribuído (Jaeger)

**Serviços**:
- Party Service (núcleo)
- Identification Service (documentos)
- Address Service (endereços)
- Contact Service (email/telefone)

---

### FASE 5: Segurança + Produção (Futuro)
**Objetivo**: Deploy pronto para produção com segurança empresarial

**Adições ao Stack**:
- Keycloak (OAuth2 + OIDC)
- Kong API Gateway
- Kubernetes (EKS/AKS/GKE)
- Terraform para infraestrutura
- Monitoramento Prometheus + Grafana

**Funcionalidades**:
- Multi-tenancy
- Controle de acesso baseado em papéis (RBAC)
- Rate limiting e throttling
- Circuit breakers (Resilience4j)

---

## Stack Tecnológico

### Core
- **Linguagem**: Java 21
- **Framework**: Spring Boot 4.0.2
- **Ferramenta de Build**: Maven (multi-módulo)
- **Banco de Dados**: PostgreSQL 16
- **Migração**: Liquibase 5.0.1

### Ecossistema Spring
- Spring Data JPA
- Spring Web (REST)
- Spring Validation
- Spring Boot Actuator
- SpringDoc OpenAPI

### Testes
- JUnit 5 + Mockito
- TestContainers (PostgreSQL)
- JaCoCo (cobertura de código: 80% linha, 70% branch)
- MockMvc (testes de API)

### DevOps
- Podman/Docker + Docker Compose
- Minikube (Kubernetes local)
- Git + GitHub

---

## Estrutura do Projeto

```
person-registration-system/
├── services/
│   └── foundation/                 # FASE 1: Serviço monolítico
│       ├── src/main/java/
│       │   └── com/akstack/foundation/
│       │       ├── config/         # Spring configurations
│       │       ├── domain/
│       │       │   ├── model/      # JPA entities
│       │       │   └── repository/ # Spring Data repositories
│       │       ├── mapper/         # MapStruct mappers
│       │       ├── service/
│       │       │   ├── exception/  # Business exceptions
│       │       │   ├── validation/ # Validators (Strategy Pattern)
│       │       │   └── [Services]  # Business logic
│       │       ├── web/
│       │       │   ├── controller/ # REST controllers
│       │       │   ├── dto/        # Request/Response DTOs
│       │       │   └── exception/  # GlobalExceptionHandler
│       │       └── FoundationApplication.java
│       ├── src/main/resources/
│       │   ├── db/changelog/       # Migrations Liquibase
│       │   │   ├── db.changelog-master.xml
│       │   │   └── changes/
│       │   │       └── 001-create-party-model.xml
│       │   ├── application.yml
│       │   ├── application-dev.yml
│       │   ├── application-prod.yml
│       │   └── liquibase.properties
│       └── src/test/
│           ├── java/.../unit/      # Testes unitários (70%)
│           ├── java/.../integration/  # Testes de integração (20%)
│           └── java/.../api/       # Testes de API (10%)
├── infrastructure/
│   ├── docker/                     # Arquivos Docker/Podman
│   │   ├── seed-data.sql           # Dados fake para testes
│   │   └── PODMAN_POSTGRES.md      # Instruções Podman
│   └── kubernetes/                 # Manifestos K8s (FASE 5)
├── docs/
│   └── IDENTIFICATION_STRATEGY.md  # ADR sobre documentos de identificação
├── pom.xml                         # POM pai Maven
├── AGENTS.md                       # Guia para agentes de código
├── REQUIREMENTS.md                 # Requisitos técnicos detalhados
└── README.md                       # Este arquivo
```

---

## Como Começar (FASE 1)

### Pré-requisitos
- Java 21+
- Maven 3.8+
- Podman ou Docker Desktop
- Git

### Desenvolvimento Local

1. **Clone o repositório**:
```bash
git clone https://github.com/seuusuario/person-registration-system.git
cd person-registration-system
```

2. **Inicie o PostgreSQL com Podman**:
```bash
podman run -d \
  --name akstack-foundation-postgres \
  -e POSTGRES_DB=akstack_foundation \
  -e POSTGRES_USER=akstack \
  -e POSTGRES_PASSWORD=akstack123 \
  -p 5432:5432 \
  -v akstack-foundation-data:/var/lib/postgresql/data \
  postgres:16-alpine
```

3. **Execute as migrations do Liquibase**:
```bash
cd services/foundation
mvn clean process-resources liquibase:update
```

4. **Compile o projeto**:
```bash
mvn clean install
```

5. **Execute a aplicação**:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

6. **Acesse a API**:
- API: http://localhost:8080/api/v1
- Swagger UI: http://localhost:8080/swagger-ui.html (quando implementado)
- Actuator: http://localhost:8080/actuator/health

### Popular com Dados Fake

```bash
podman exec -i akstack-foundation-postgres psql -U akstack -d akstack_foundation < infrastructure/docker/seed-data.sql
```

### Executando os Testes

```bash
# Todos os testes (quando implementados)
mvn test

# Com relatório de cobertura
mvn clean test jacoco:report

# Ver cobertura: target/site/jacoco/index.html
```

---

## Decisões de Design Principais

### 1. Padrão Party Model
**Por quê**: Padrão da indústria (SAP, Oracle, Salesforce) para gerenciamento unificado de pessoa/organização.

**Estrutura Implementada**:
- `party`: Entidade base com campos comuns
  - `id` (BIGINT auto-increment) - Chave primária interna
  - `party_number` (VARCHAR, unique) - Identificador de negócio (gerado via sequence)
  - `party_type` (VARCHAR) - PERSON ou ORGANIZATION
  - `email`, `phone` - Contatos
  - `is_active`, `is_deleted` - Status e soft delete
  - `created_at`, `updated_at`, `deleted_at` - Auditoria
- `person`: Pessoa física
  - `first_name`, `middle_name`, `last_name` - Nomes estruturados
  - `full_name` - Nome completo (precisa ser calculado via entidade JPA)
  - `date_of_birth`, `gender` - Dados pessoais
  - `primary_identification_type`, `primary_identification_document` - Documentos
- `organization`: Pessoa jurídica
  - `legal_name` - Razão social (obrigatório)
  - `trade_name`, `brand_name` - Nome fantasia e marca (opcionais)
  - `founding_date` - Data de fundação
  - `primary_identification_type`, `primary_identification_document` - Documentos

Veja [IDENTIFICATION_STRATEGY.md](docs/IDENTIFICATION_STRATEGY.md) para a justificativa detalhada.

### 2. Design de Banco Evolutivo
**FASE 1**: Um documento de identificação primário por party (simples, normalizado)
**FASE 2**: Adicionar tabela `party_identification` para múltiplos documentos (CPF + RG + CNH)

**Por quê**: Comece simples, evolua baseado em necessidades reais. Estratégia de migração documentada.

### 3. Genérico e Internacional
Sem campos específicos do Brasil na estrutura de tabelas. Suporta qualquer país via:
- `identification_type` flexível (CPF, CNPJ, SSN, EIN, Passaporte, etc.)
- Padrão strategy de validação agnóstico ao país
- Formatos de nome internacionais estruturados

### 4. Desenvolvimento Orientado a Testes
- TDD/BDD desde o início
- 70% unit, 20% integration, 10% API
- TestContainers para testes com PostgreSQL real
- Mínimo de 80% de cobertura de código imposto pela CI

---

## Documentação

- **[REQUIREMENTS.md](REQUIREMENTS.md)**: Requisitos técnicos completos e critérios de aceitação
- **[docs/IDENTIFICATION_STRATEGY.md](docs/IDENTIFICATION_STRATEGY.md)**: Decisão arquitetural sobre posicionamento de documentos
- **Documentação da API**: Auto-gerada via SpringDoc OpenAPI em `/swagger-ui.html`

---

## Princípios de Desenvolvimento

1. **Foco em Arquitetura, Não em Lógica de Negócio**: Este é um projeto de aprendizado. Mantenha as regras de negócio simples.
2. **Teste Tudo**: Cobertura mínima de 80%. Abordagem TDD.
3. **Código Limpo**: Siga princípios SOLID, padrões DDD e melhores práticas Spring.
4. **Documente Decisões**: ADRs (Architecture Decision Records) para escolhas importantes.
5. **Evolua Gradualmente**: Complete cada fase antes de passar para a próxima.

---

## Status Atual

**Progresso FASE 1**:
- ✅ Estrutura do projeto Maven multi-módulo
- ✅ Configuração Spring Boot 4.0.2 com dependências
- ✅ Schema de banco de dados (Liquibase migrations)
- ✅ PostgreSQL rodando em container Podman
- ✅ Migrations executadas com sucesso (4 ChangeSets)
- ✅ Script de seed data com dados fake (6 pessoas + 7 organizações)
- ⏳ Modelo de domínio (entidades Party, Person, Organization)
- ⏳ Camada de repository (consultas JPQL)
- ⏳ Camada de serviço com validadores
- ⏳ Controllers REST API e DTOs
- ⏳ Docker Compose multi-serviço
- ⏳ Suíte de testes

**Próximos Passos**:
1. Criar entidades JPA mapeando as tabelas existentes
2. Implementar repositories Spring Data JPA
3. Implementar camada de serviço com padrão strategy de validação
4. Criar API REST com DTOs e tratamento de exceções
5. Construir suíte de testes abrangente
6. Criar Docker Compose para ambiente completo

---

## Contribuindo

Este é um projeto de estudo pessoal, mas feedback e sugestões são bem-vindos! Sinta-se à vontade para:
- Abrir issues para perguntas ou sugestões
- Enviar PRs para melhorias
- Compartilhar seus próprios insights de arquitetura

---

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## Autor

**Projeto de Estudo** - Aprendendo padrões modernos de arquitetura de software através de implementação prática.

Contato: [Suas informações de contato]

---

## Agradecimentos

Inspirado por padrões empresariais de:
- SAP Party Model
- Oracle Customer Data Hub
- Modelo Account/Contact do Salesforce
- Padrões de Martin Fowler (Analysis Patterns, Patterns of Enterprise Application Architecture)
- Domain-Driven Design por Eric Evans
