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

### FASE 1: MVP Monolítico ✅ (Atual)
**Objetivo**: Construir API REST fundacional com arquitetura limpa

**Stack**:
- Java 21 + Spring Boot 3.2.2
- PostgreSQL + Flyway migrations
- Docker Compose para desenvolvimento local
- JUnit 5 + TestContainers

**Funcionalidades**:
- Party Model de 3 tabelas (party, person, organization)
- Operações CRUD com validação
- Um documento de identificação primário por party
- JPA/Hibernate com padrão repository
- 80% de cobertura de testes (unit + integration + API)

**Entregas**:
- ✅ Modelo de domínio (entidades + repositories)
- ⏳ Camada de serviço com padrão strategy de validação
- ⏳ API REST com DTOs e tratamento de exceções
- ⏳ Migrações de banco com Flyway
- ⏳ Setup Docker Compose
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
- **Framework**: Spring Boot 3.2.2
- **Ferramenta de Build**: Maven (multi-módulo)
- **Banco de Dados**: PostgreSQL 16
- **Migração**: Flyway

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
- Docker + Docker Compose
- Minikube (Kubernetes local)
- Git + GitHub

---

## Estrutura do Projeto

```
person-registration-system/
├── services/
│   └── party-service/              # FASE 1: Serviço monolítico
│       ├── src/main/java/
│       │   └── com/study/party/
│       │       ├── domain/         # Entidades de domínio & repositories
│       │       ├── application/    # Camada de serviço & lógica de negócio
│       │       └── infrastructure/ # Controllers REST, DTOs, config
│       ├── src/main/resources/
│       │   ├── db/migration/       # Scripts SQL do Flyway
│       │   ├── application.yml
│       │   └── application-dev.yml
│       └── src/test/
│           ├── java/.../unit/      # Testes unitários (70%)
│           ├── java/.../integration/  # Testes de integração (20%)
│           └── java/.../api/       # Testes de API (10%)
├── database/
│   ├── schema-phase1.sql           # ATUAL: 3 tabelas (party, person, org)
│   └── schema-phase2.sql           # FUTURO: +tabela party_identification
├── infrastructure/
│   ├── docker/                     # Arquivos Docker Compose
│   └── kubernetes/                 # Manifestos K8s + scripts de setup
├── docs/
│   ├── IDENTIFICATION_STRATEGY.md  # Decisão arquitetural: posicionamento de documentos
│   └── ...
├── REQUIREMENTS.md                 # Requisitos técnicos detalhados
└── README.md                       # Este arquivo
```

---

## Como Começar (FASE 1)

### Pré-requisitos
- Java 21+
- Maven 3.8+
- Docker Desktop
- Git

### Desenvolvimento Local

1. **Clone o repositório**:
```bash
git clone https://github.com/seuusuario/person-registration-system.git
cd person-registration-system
```

2. **Inicie o PostgreSQL com Docker Compose**:
```bash
cd infrastructure/docker
docker-compose up -d postgres
```

3. **Compile o projeto**:
```bash
cd services/party-service
mvn clean install
```

4. **Execute a aplicação**:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

5. **Acesse a API**:
- API: http://localhost:8080/api/v1
- Swagger UI: http://localhost:8080/swagger-ui.html
- Actuator: http://localhost:8080/actuator/health

### Executando os Testes

```bash
# Todos os testes (unit + integration + API)
mvn test

# Com relatório de cobertura
mvn clean test jacoco:report

# Ver cobertura: target/site/jacoco/index.html
```

---

## Decisões de Design Principais

### 1. Padrão Party Model
**Por quê**: Padrão da indústria (SAP, Oracle, Salesforce) para gerenciamento unificado de pessoa/organização.

**Estrutura**:
- `party`: Entidade base com campos comuns (party_number, status, country)
- `person`: Pessoa física com nomes estruturados + identificação
- `organization`: Pessoa jurídica com razão social/nome fantasia/marca + identificação

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
- ✅ Estrutura do projeto e configuração Maven
- ✅ Modelo de domínio (entidades Party, Person, Organization)
- ✅ Camada de repository (consultas JPQL)
- ⏳ Camada de serviço (em progresso)
- ⏳ Controllers REST API e DTOs
- ⏳ Migrações Flyway
- ⏳ Setup Docker Compose
- ⏳ Suíte de testes

**Próximos Passos**:
1. Implementar camada de serviço com padrão strategy de validação
2. Criar API REST com DTOs e tratamento de exceções
3. Configurar migrações Flyway a partir do schema-phase1.sql
4. Construir suíte de testes abrangente
5. Configurar Docker Compose para desenvolvimento local

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
