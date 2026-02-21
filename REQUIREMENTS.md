# Requisitos Técnicos - FASE 1

## Visão Geral

Este documento define os requisitos técnicos completos para a **FASE 1** do Sistema de Cadastro de Pessoas: uma API REST monolítica implementando o padrão Party Model com princípios de arquitetura limpa.

**Objetivos**:
- Construir uma base de qualidade de produção para fases futuras
- Demonstrar TDD/BDD com 80%+ de cobertura de testes
- Implementar padrão Party Model da indústria (padrão SAP/Oracle/Salesforce)
- Suportar cadastro de pessoa/organização genérico e internacional

---

## Índice

1. [Requisitos Funcionais](#requisitos-funcionais)
2. [Modelo de Dados](#modelo-de-dados)
3. [Especificações da API](#especificações-da-api)
4. [Regras de Validação](#regras-de-validação)
5. [Requisitos Não-Funcionais](#requisitos-não-funcionais)
6. [Estratégia de Testes](#estratégia-de-testes)
7. [Stack Tecnológico](#stack-tecnológico)
8. [Critérios de Aceitação](#critérios-de-aceitação)

---

## Requisitos Funcionais

### RF-01: Gerenciamento de Parties

**Como** usuário do sistema  
**Eu quero** gerenciar parties (pessoas e organizações)  
**Para que** eu possa manter um registro unificado de todas as entidades

**Critérios de Aceitação**:
- Criar, ler, atualizar e excluir parties
- Cada party tem um `party_number` único (auto-gerado, formato: `PTY-{UUID}`)
- Parties podem ser pessoas (PF) ou organizações (PJ)
- Suportar exclusão lógica (soft delete com timestamp `deleted_at`)
- Rastrear timestamps de criação e modificação automaticamente

---

### RF-02: Gerenciamento de Pessoa Física

**Como** usuário do sistema  
**Eu quero** cadastrar pessoas físicas com nomes estruturados e identificação  
**Para que** eu possa manter registros precisos de indivíduos

**Critérios de Aceitação**:
- Pessoa deve ter `first_name` (obrigatório)
- Pessoa pode ter `middle_name` (opcional)
- Pessoa deve ter `last_name` (obrigatório)
- Sistema auto-gera `full_name` a partir das partes do nome (via fórmula de banco)
- Pessoa deve ter um documento de identificação primário (CPF, SSN, Passaporte, etc.)
- Pessoa deve especificar `primary_identification_type`
- Pessoa pode ter `date_of_birth` (opcional, mas recomendado)
- Buscar por: nome (match parcial), documento de identificação, país

---

### RF-03: Gerenciamento de Pessoa Jurídica

**Como** usuário do sistema  
**Eu quero** cadastrar organizações legais com nomes oficiais e identificação  
**Para que** eu possa manter registros precisos de empresas

**Critérios de Aceitação**:
- Organização deve ter `legal_name` (obrigatório, razão social registrada oficialmente)
- Organização pode ter `trade_name` (opcional, nome fantasia)
- Organização pode ter `brand_name` (opcional, nome de marca/marketing)
- Organização deve ter um documento de identificação primário (CNPJ, EIN, VAT, etc.)
- Organização deve especificar `primary_identification_type`
- Organização pode ter `founded_date` (opcional)
- Buscar por: nome (qualquer um: razão social/nome fantasia/marca), documento de identificação, país

---

### RF-04: Busca e Filtro de Parties

**Como** usuário do sistema  
**Eu quero** buscar e filtrar parties por múltiplos critérios  
**Para que** eu possa encontrar rapidamente registros específicos

**Critérios de Aceitação**:
- Buscar por party number (match exato)
- Buscar por documento de identificação (match exato)
- Buscar por email (match exato, case-insensitive)
- Buscar por tipo de party (PERSON ou ORGANIZATION)
- Buscar por país (match exato, ISO 3166-1 alpha-3)
- Buscar por nome (match parcial, case-insensitive):
  - Para pessoas: buscar em full_name
  - Para organizações: buscar em legal_name, trade_name ou brand_name
- Filtrar por status (ACTIVE, INACTIVE, SUSPENDED)
- Excluir registros com soft-delete das buscas padrão
- Suportar paginação (tamanho da página, número da página, ordenação)

---

### RF-05: Validação de Documentos de Identificação

**Como** usuário do sistema  
**Eu quero** validar documentos de identificação baseado no tipo  
**Para que** eu possa garantir qualidade de dados e prevenir duplicatas

**Critérios de Aceitação**:
- Implementar padrão strategy de validação para extensibilidade
- Validar formato baseado em `identification_type`:
  - `CPF` (Brasil): 11 dígitos, dígitos verificadores válidos
  - `CNPJ` (Brasil): 14 dígitos, dígitos verificadores válidos
  - `SSN` (EUA): Formato XXX-XX-XXXX (9 dígitos)
  - `EIN` (EUA): Formato XX-XXXXXXX (9 dígitos)
  - Tipos genéricos: Sem validação de formato específico
- Remover caracteres de formatação antes de armazenar (armazenar apenas dígitos)
- Prevenir documentos de identificação duplicados:
  - Mesmo `identification_type` + `primary_identification_document` = conflito
  - Retornar HTTP 409 Conflict com erro descritivo
- Permitir documentos de identificação nulos (opcional para alguns cenários)

---

### RF-06: Gerenciamento de Status do Party

**Como** usuário do sistema  
**Eu quero** gerenciar o status do ciclo de vida do party  
**Para que** eu possa controlar estados ativo/inativo/suspenso

**Critérios de Aceitação**:
- Três status: `ACTIVE`, `INACTIVE`, `SUSPENDED`
- Status padrão na criação: `ACTIVE`
- Transições de status:
  - ACTIVE → INACTIVE (usuário desativa)
  - ACTIVE → SUSPENDED (suspensão do sistema/compliance)
  - INACTIVE → ACTIVE (usuário reativa)
  - SUSPENDED → ACTIVE (liberação do sistema/compliance)
- Soft delete é independente do status:
  - Parties deletados são excluídos das buscas independente do status
  - Status pode ser atualizado mesmo em parties deletados (para propósitos de auditoria)

---

### RF-07: Trilha de Auditoria

**Como** administrador do sistema  
**Eu quero** rastrear timestamps de criação e modificação  
**Para que** eu possa auditar mudanças e manter conformidade

**Critérios de Aceitação**:
- Todas as entidades têm `created_at` (auto-definido no insert)
- Todas as entidades têm `updated_at` (auto-atualizado em toda modificação)
- Soft delete usa timestamp `deleted_at` (null = não deletado)
- Timestamps usam timezone UTC
- Timestamps gerenciados automaticamente por JPA `@EntityListeners(AuditingEntityListener.class)`

---

## Modelo de Dados

### Relacionamento de Entidades

```
┌─────────────────────┐
│       PARTY         │
│  (Entidade Base)    │
├─────────────────────┤
│ id (UUID, PK)       │
│ party_number (UQ)   │
│ party_type (ENUM)   │
│ status (ENUM)       │
│ country (CHAR 3)    │
│ email               │
│ phone               │
│ notes               │
│ created_at          │
│ updated_at          │
│ deleted_at          │
└─────────────────────┘
           △
           │
           │ (Herança: Joined)
           │
    ┌──────┴──────┐
    │             │
┌───┴──────────┐  │
│   PERSON     │  │
├──────────────┤  │
│ id (PK, FK)  │  │
│ first_name   │  │
│ middle_name  │  │
│ last_name    │  │
│ full_name    │  │ (computado)
│ date_of_birth│  │
│ primary_     │  │
│   identification_│
│   document   │  │
│ primary_     │  │
│   identification_│
│   type       │  │
└──────────────┘  │
                  │
         ┌────────┴─────────┐
         │  ORGANIZATION    │
         ├──────────────────┤
         │ id (PK, FK)      │
         │ legal_name       │
         │ trade_name       │
         │ brand_name       │
         │ founded_date     │
         │ primary_         │
         │   identification_│
         │   document       │
         │ primary_         │
         │   identification_│
         │   type           │
         └──────────────────┘
```

### Tabela: `party`

| Coluna        | Tipo         | Constraints                  | Descrição                            |
|---------------|--------------|------------------------------|--------------------------------------|
| id            | UUID         | PRIMARY KEY                  | Identificador único                  |
| party_number  | VARCHAR(50)  | UNIQUE, NOT NULL             | Chave de negócio (PTY-{UUID})        |
| party_type    | VARCHAR(20)  | NOT NULL                     | PERSON ou ORGANIZATION               |
| status        | VARCHAR(20)  | NOT NULL, DEFAULT 'ACTIVE'   | ACTIVE, INACTIVE, SUSPENDED          |
| country       | CHAR(3)      | NOT NULL                     | ISO 3166-1 alpha-3 (ex: BRA, USA)    |
| email         | VARCHAR(255) | NULL, INDEX                  | Email de contato                     |
| phone         | VARCHAR(20)  | NULL                         | Telefone de contato                  |
| notes         | TEXT         | NULL                         | Notas adicionais                     |
| created_at    | TIMESTAMP    | NOT NULL, DEFAULT now()      | Timestamp de criação (UTC)           |
| updated_at    | TIMESTAMP    | NOT NULL, DEFAULT now()      | Timestamp da última atualização (UTC)|
| deleted_at    | TIMESTAMP    | NULL, INDEX                  | Timestamp de soft delete             |

**Índices**:
- `idx_party_number` em `party_number` (único)
- `idx_party_email` em `email` (para busca)
- `idx_party_deleted_at` em `deleted_at` (para filtrar registros ativos)

---

### Tabela: `person`

| Coluna                          | Tipo         | Constraints                | Descrição                      |
|---------------------------------|--------------|----------------------------|--------------------------------|
| id                              | UUID         | PRIMARY KEY, FOREIGN KEY   | Referencia party(id)           |
| first_name                      | VARCHAR(100) | NOT NULL                   | Primeiro nome                  |
| middle_name                     | VARCHAR(100) | NULL                       | Nome(s) do meio                |
| last_name                       | VARCHAR(100) | NOT NULL                   | Sobrenome                      |
| full_name                       | VARCHAR(302) | GENERATED, INDEX           | Nome completo auto-computado   |
| date_of_birth                   | DATE         | NULL                       | Data de nascimento             |
| primary_identification_document | VARCHAR(50)  | NULL, INDEX                | Documento de ID principal (apenas dígitos) |
| primary_identification_type     | VARCHAR(20)  | NULL                       | CPF, SSN, PASSPORT, etc.       |

**Índices**:
- `idx_person_full_name` em `full_name` (para busca)
- `idx_person_identification` em `(primary_identification_type, primary_identification_document)` (para unicidade + busca)

**Coluna Computada**:
```sql
full_name = CONCAT_WS(' ', first_name, middle_name, last_name)
-- Implementado como @Formula no JPA
```

---

### Tabela: `organization`

| Coluna                          | Tipo         | Constraints                | Descrição                         |
|---------------------------------|--------------|----------------------------|-----------------------------------|
| id                              | UUID         | PRIMARY KEY, FOREIGN KEY   | Referencia party(id)              |
| legal_name                      | VARCHAR(255) | NOT NULL, INDEX            | Razão social oficial              |
| trade_name                      | VARCHAR(255) | NULL, INDEX                | Nome fantasia                     |
| brand_name                      | VARCHAR(255) | NULL                       | Nome de marca/marketing           |
| founded_date                    | DATE         | NULL                       | Data de fundação da empresa       |
| primary_identification_document | VARCHAR(50)  | NULL, INDEX                | Documento de ID principal (CNPJ, EIN, etc.)|
| primary_identification_type     | VARCHAR(20)  | NULL                       | CNPJ, EIN, VAT, etc.              |

**Índices**:
- `idx_org_legal_name` em `legal_name` (para busca)
- `idx_org_trade_name` em `trade_name` (para busca)
- `idx_org_identification` em `(primary_identification_type, primary_identification_document)` (para unicidade + busca)

---

## Especificações da API

### URL Base
```
http://localhost:8080/api/v1
```

### Códigos de Resposta Comuns

| Código | Significado           | Uso                                      |
|--------|-----------------------|------------------------------------------|
| 200    | OK                    | GET, PUT bem-sucedidos                   |
| 201    | Created               | POST bem-sucedido                        |
| 204    | No Content            | DELETE bem-sucedido                      |
| 400    | Bad Request           | Erro de validação                        |
| 404    | Not Found             | Recurso não encontrado                   |
| 409    | Conflict              | Documento de identificação duplicado     |
| 500    | Internal Server Error | Erro inesperado do servidor              |

---

### Endpoints da API

#### 1. Criar Pessoa

**POST** `/parties/persons`

**Corpo da Requisição**:
```json
{
  "firstName": "João",
  "middleName": "Pedro",
  "lastName": "Silva",
  "dateOfBirth": "1990-05-15",
  "country": "BRA",
  "email": "joao.silva@example.com",
  "phone": "+55-11-98765-4321",
  "primaryIdentificationDocument": "123.456.789-09",
  "primaryIdentificationType": "CPF",
  "notes": "Cliente desde 2024"
}
```

**Resposta** (201 Created):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "partyNumber": "PTY-550e8400-e29b-41d4-a716-446655440000",
  "partyType": "PERSON",
  "status": "ACTIVE",
  "country": "BRA",
  "email": "joao.silva@example.com",
  "phone": "+55-11-98765-4321",
  "notes": "Cliente desde 2024",
  "person": {
    "firstName": "João",
    "middleName": "Pedro",
    "lastName": "Silva",
    "fullName": "João Pedro Silva",
    "dateOfBirth": "1990-05-15",
    "primaryIdentificationDocument": "12345678909",
    "primaryIdentificationType": "CPF"
  },
  "createdAt": "2024-02-21T10:30:00Z",
  "updatedAt": "2024-02-21T10:30:00Z"
}
```

---

#### 2. Criar Organização

**POST** `/parties/organizations`

**Corpo da Requisição**:
```json
{
  "legalName": "Acme Corporação Ltda",
  "tradeName": "Acme Corp",
  "brandName": "ACME",
  "foundedDate": "2010-01-15",
  "country": "BRA",
  "email": "contato@acme.com.br",
  "phone": "+55-11-3333-4444",
  "primaryIdentificationDocument": "12.345.678/0001-95",
  "primaryIdentificationType": "CNPJ",
  "notes": "Empresa Fortune 500"
}
```

**Resposta** (201 Created):
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "partyNumber": "PTY-660e8400-e29b-41d4-a716-446655440001",
  "partyType": "ORGANIZATION",
  "status": "ACTIVE",
  "country": "BRA",
  "email": "contato@acme.com.br",
  "phone": "+55-11-3333-4444",
  "notes": "Empresa Fortune 500",
  "organization": {
    "legalName": "Acme Corporação Ltda",
    "tradeName": "Acme Corp",
    "brandName": "ACME",
    "foundedDate": "2010-01-15",
    "primaryIdentificationDocument": "12345678000195",
    "primaryIdentificationType": "CNPJ"
  },
  "createdAt": "2024-02-21T10:35:00Z",
  "updatedAt": "2024-02-21T10:35:00Z"
}
```

---

#### 3. Obter Party por ID

**GET** `/parties/{id}`

**Resposta** (200 OK):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "partyNumber": "PTY-550e8400-e29b-41d4-a716-446655440000",
  "partyType": "PERSON",
  "status": "ACTIVE",
  "country": "BRA",
  "email": "joao.silva@example.com",
  "phone": "+55-11-98765-4321",
  "person": {
    "firstName": "João",
    "middleName": "Pedro",
    "lastName": "Silva",
    "fullName": "João Pedro Silva",
    "dateOfBirth": "1990-05-15",
    "primaryIdentificationDocument": "12345678909",
    "primaryIdentificationType": "CPF"
  },
  "createdAt": "2024-02-21T10:30:00Z",
  "updatedAt": "2024-02-21T10:30:00Z"
}
```

**Resposta de Erro** (404 Not Found):
```json
{
  "timestamp": "2024-02-21T10:40:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Party não encontrado com id: 550e8400-e29b-41d4-a716-446655440000",
  "path": "/api/v1/parties/550e8400-e29b-41d4-a716-446655440000"
}
```

---

#### 4. Atualizar Pessoa

**PUT** `/parties/persons/{id}`

**Corpo da Requisição**:
```json
{
  "firstName": "João",
  "middleName": "Pedro",
  "lastName": "Silva Santos",
  "dateOfBirth": "1990-05-15",
  "country": "BRA",
  "email": "joao.santos@example.com",
  "phone": "+55-11-98765-9999",
  "primaryIdentificationDocument": "12345678909",
  "primaryIdentificationType": "CPF",
  "status": "ACTIVE"
}
```

**Resposta** (200 OK): Mesma estrutura de Obter Party por ID

---

#### 5. Buscar Parties

**GET** `/parties/search`

**Parâmetros de Query**:
- `partyNumber` (string): Match exato
- `identificationType` (string): Match exato (CPF, CNPJ, SSN, EIN, etc.)
- `identificationDocument` (string): Match exato (apenas dígitos)
- `email` (string): Match exato (case-insensitive)
- `country` (string): Match exato (ISO 3166-1 alpha-3)
- `partyType` (string): PERSON ou ORGANIZATION
- `status` (string): ACTIVE, INACTIVE, SUSPENDED
- `name` (string): Match parcial (nome completo da pessoa ou nomes da organização)
- `page` (int): Número da página (padrão: 0)
- `size` (int): Tamanho da página (padrão: 20, máx: 100)
- `sort` (string): Campo e direção de ordenação (ex: `createdAt,desc`)

**Exemplo de Requisição**:
```
GET /parties/search?country=BRA&partyType=PERSON&name=João&page=0&size=10&sort=createdAt,desc
```

**Resposta** (200 OK):
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "partyNumber": "PTY-550e8400-e29b-41d4-a716-446655440000",
      "partyType": "PERSON",
      "status": "ACTIVE",
      "country": "BRA",
      "email": "joao.silva@example.com",
      "person": {
        "firstName": "João",
        "lastName": "Silva",
        "fullName": "João Pedro Silva"
      },
      "createdAt": "2024-02-21T10:30:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1
}
```

---

#### 6. Deletar Party (Soft Delete)

**DELETE** `/parties/{id}`

**Resposta** (204 No Content): Corpo vazio

---

#### 7. Obter Party por Party Number

**GET** `/parties/number/{partyNumber}`

**Resposta** (200 OK): Mesma estrutura de Obter Party por ID

---

#### 8. Verificar se Party Existe

**HEAD** `/parties/{id}`

**Resposta**:
- 200 OK: Party existe
- 404 Not Found: Party não existe

---

## Regras de Validação

### Validação Geral

| Campo                           | Regra                                                     |
|---------------------------------|-----------------------------------------------------------|
| first_name                      | Não vazio, máx 100 caracteres                            |
| last_name                       | Não vazio, máx 100 caracteres                            |
| middle_name                     | Máx 100 caracteres (se fornecido)                        |
| legal_name                      | Não vazio, máx 255 caracteres                            |
| trade_name                      | Máx 255 caracteres (se fornecido)                        |
| brand_name                      | Máx 255 caracteres (se fornecido)                        |
| email                           | Formato de email válido (RFC 5322), máx 255 caracteres   |
| phone                           | Máx 20 caracteres, permitir formatos internacionais      |
| country                         | Exatamente 3 caracteres, maiúsculas, ISO 3166-1 alpha-3  |
| status                          | Deve ser: ACTIVE, INACTIVE ou SUSPENDED                  |
| party_type                      | Deve ser: PERSON ou ORGANIZATION                         |
| primary_identification_type     | Máx 20 caracteres, maiúsculas (CPF, CNPJ, SSN, EIN, etc.)|
| primary_identification_document | Máx 50 caracteres, armazenar apenas dígitos (remover formatação) |

---

### Validação de Documentos de Identificação

#### CPF (Brasil - Pessoa Física)
- **Formato**: 11 dígitos
- **Validação**: Verificar dígitos verificadores usando algoritmo do CPF
- **Armazenamento**: Apenas dígitos (sem pontos/traços)
- **Exemplo de Entrada**: `123.456.789-09` → Armazenar como `12345678909`

#### CNPJ (Brasil - Pessoa Jurídica)
- **Formato**: 14 dígitos
- **Validação**: Verificar dígitos verificadores usando algoritmo do CNPJ
- **Armazenamento**: Apenas dígitos
- **Exemplo de Entrada**: `12.345.678/0001-95` → Armazenar como `12345678000195`

#### SSN (EUA - Pessoa Física)
- **Formato**: 9 dígitos (XXX-XX-XXXX)
- **Validação**: Apenas formato (sem validação de dígito verificador)
- **Armazenamento**: Apenas dígitos
- **Exemplo de Entrada**: `123-45-6789` → Armazenar como `123456789`

#### EIN (EUA - Pessoa Jurídica)
- **Formato**: 9 dígitos (XX-XXXXXXX)
- **Validação**: Apenas formato
- **Armazenamento**: Apenas dígitos
- **Exemplo de Entrada**: `12-3456789` → Armazenar como `123456789`

#### Tipos Genéricos (Passaporte, VAT, etc.)
- **Validação**: Nenhuma (aceitar qualquer alfanumérico)
- **Armazenamento**: Como fornecido (pode incluir letras)
- **Comprimento Máximo**: 50 caracteres

---

### Restrições de Unicidade

1. **Party Number**: Deve ser único em todos os parties
2. **Documento de Identificação**: Combinação de `(identification_type, identification_document)` deve ser única
   - Exemplo: Não pode ter dois parties com `CPF = 12345678909`
   - Exemplo: Pode ter `CPF = 12345678909` E `SSN = 123456789` (tipos diferentes)

**Resposta de Erro para Duplicata** (409 Conflict):
```json
{
  "timestamp": "2024-02-21T10:45:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Party já existe com CPF: 12345678909",
  "path": "/api/v1/parties/persons"
}
```

---

## Requisitos Não-Funcionais

### RNF-01: Performance
- **Tempo de Resposta**: 95% das requisições da API devem completar em < 500ms (excluindo tempo de consulta do banco)
- **Throughput**: Suportar pelo menos 100 requisições/segundo (instância única)
- **Consultas de Banco**: Usar índices para todas as operações de busca
- **Paginação**: Tamanho padrão de página = 20, máx = 100

### RNF-02: Escalabilidade
- **Escalamento Horizontal**: Aplicação deve ser stateless (pronta para FASE 4)
- **Pool de Conexões de Banco**: HikariCP com min=5, max=20 conexões

### RNF-03: Confiabilidade
- **Uptime**: Meta de 99.9% de disponibilidade (excluindo manutenção planejada)
- **Tratamento de Erros**: Todas as exceções devem retornar códigos HTTP apropriados e mensagens de erro
- **Transações de Banco**: Todas as operações de escrita devem ser transacionais (ACID)

### RNF-04: Segurança
- **Injeção SQL**: Usar consultas parametrizadas (apenas JPA/JPQL)
- **Validação de Entrada**: Validar todas as entradas de usuário na camada de API
- **Dados Sensíveis**: Sem senhas ou credenciais na FASE 1 (adicionado na FASE 5)

### RNF-05: Manutenibilidade
- **Cobertura de Código**: Mínimo de 80% de cobertura de linha, 70% de cobertura de branch
- **Qualidade de Código**: Análise SonarQube sem issues críticos
- **Documentação**: Documentação OpenAPI/Swagger para todos os endpoints
- **Logging**: Logging estruturado com SLF4J + Logback

### RNF-06: Observabilidade
- **Health Checks**: Endpoints Spring Boot Actuator (`/actuator/health`, `/actuator/info`)
- **Métricas**: Expor métricas Micrometer (requisições, tempos de resposta, erros)
- **Monitoramento de Banco**: Logar consultas lentas (> 1 segundo)

---

## Estratégia de Testes

### Distribuição de Testes
- **70% Testes Unitários**: Camada de serviço, validadores, utilitários
- **20% Testes de Integração**: Camada de repository com TestContainers
- **10% Testes de API**: Camada de controller com MockMvc

### Testes Unitários (70%)

**Alvo**: Lógica de negócio da camada de serviço e validação

**Ferramentas**: JUnit 5, Mockito, AssertJ

**Exemplos de Casos de Teste**:
```java
@Test
void deveCriarPessoaComCpfValido() {
    // Given
    CreatePersonRequest request = requestPessoaValido();
    
    // When
    PartyResponse response = partyService.createPerson(request);
    
    // Then
    assertThat(response.getPartyType()).isEqualTo(PartyType.PERSON);
    assertThat(response.getPerson().getFullName()).isEqualTo("João Pedro Silva");
}

@Test
void deveLancarExcecaoQuandoCpfInvalido() {
    // Given
    CreatePersonRequest request = requestPessoaComCpfInvalido();
    
    // When & Then
    assertThatThrownBy(() -> partyService.createPerson(request))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("CPF inválido");
}
```

**Áreas de Cobertura**:
- Lógica de validação (algoritmos CPF, CNPJ, SSN, EIN)
- Regras de negócio (transições de status, soft delete)
- Mapeamento de DTOs (conversões entidade ↔ DTO)
- Casos extremos (valores nulos, strings vazias, condições de limite)

---

### Testes de Integração (20%)

**Alvo**: Camada de repository com banco PostgreSQL real

**Ferramentas**: JUnit 5, TestContainers, Spring Boot Test

**Exemplos de Casos de Teste**:
```java
@SpringBootTest
@Testcontainers
class PartyRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @Autowired
    private PartyRepository partyRepository;
    
    @Test
    void deveBuscarPartyPorDocumentoIdentificacao() {
        // Given
        Person person = criarPessoaComCpf("12345678909");
        partyRepository.save(person);
        
        // When
        Optional<Party> found = partyRepository.findByIdentificationDocument("CPF", "12345678909");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getPerson().getPrimaryIdentificationDocument())
            .isEqualTo("12345678909");
    }
}
```

**Áreas de Cobertura**:
- Consultas JPQL (busca, filtro, paginação)
- Constraints de banco (unicidade, chaves estrangeiras)
- Comportamento de soft delete (filtragem de deleted_at)
- Gerenciamento de transações (rollback, commit)
- Performance de índices (verificar planos de consulta)

---

### Testes de API (10%)

**Alvo**: Controllers REST end-to-end

**Ferramentas**: JUnit 5, MockMvc, Spring Boot Test, JsonPath

**Exemplos de Casos de Teste**:
```java
@SpringBootTest
@AutoConfigureMockMvc
class PartyControllerApiTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void deveCriarPessoaERetornar201() throws Exception {
        String requestBody = """
            {
              "firstName": "João",
              "lastName": "Silva",
              "country": "BRA",
              "email": "joao@example.com",
              "primaryIdentificationDocument": "123.456.789-09",
              "primaryIdentificationType": "CPF"
            }
            """;
        
        mockMvc.perform(post("/api/v1/parties/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.partyType").value("PERSON"))
            .andExpect(jsonPath("$.person.fullName").value("João Silva"))
            .andExpect(jsonPath("$.person.primaryIdentificationDocument").value("12345678909"));
    }
    
    @Test
    void deveRetornar409QuandoCpfDuplicado() throws Exception {
        // Given: pessoa com CPF já existe
        criarPessoaComCpf("12345678909");
        
        // When: tentar criar outra com mesmo CPF
        String requestBody = requestPessoaComCpf("12345678909");
        
        // Then
        mockMvc.perform(post("/api/v1/parties/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value(containsString("já existe")));
    }
}
```

**Áreas de Cobertura**:
- Códigos de status HTTP (201, 200, 400, 404, 409, 500)
- Estrutura JSON de requisição/resposta
- Mensagens de erro de validação
- Paginação e ordenação
- Negociação de conteúdo (JSON)

---

### Requisitos de Cobertura de Código

**Configuração JaCoCo** (`pom.xml`):
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <phase>verify</phase>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                            <limit>
                                <counter>BRANCH</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**Exclusões**:
- DTOs (classes de dados sem lógica)
- Classes de configuração (`@Configuration`)
- Classe principal da aplicação (`PartyServiceApplication.java`)
- Código gerado (Lombok, MapStruct)

---

## Stack Tecnológico

### Dependências Core

```xml
<!-- Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>3.2.2</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>3.2.2</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
    <version>3.2.2</version>
</dependency>

<!-- Database -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>

<!-- Documentation -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>

<!-- Utilities -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

---

### Configuração da Aplicação

**application.yml**:
```yaml
spring:
  application:
    name: party-service
  
  datasource:
    url: jdbc:postgresql://localhost:5432/partydb
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          time_zone: UTC
  
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
  
server:
  port: 8080
  servlet:
    context-path: /api/v1

logging:
  level:
    com.study.party: INFO
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

**application-dev.yml**:
```yaml
spring:
  jpa:
    show-sql: true
  h2:
    console:
      enabled: false

logging:
  level:
    com.study.party: DEBUG
```

---

## Critérios de Aceitação

### CA-01: Estrutura do Projeto
- ✅ Projeto Maven multi-módulo com POM pai
- ✅ Arquitetura limpa: camadas domain, application, infrastructure
- ✅ Estrutura de pacotes: `com.study.party.{domain|application|infrastructure}`

### CA-02: Modelo de Domínio
- ✅ Entidades Party, Person, Organization com anotações JPA apropriadas
- ✅ Chaves primárias UUID, party_number auto-gerado
- ✅ Soft delete com timestamp `deleted_at`
- ✅ Campos de auditoria com `@EntityListeners(AuditingEntityListener.class)`

### CA-03: Camada de Repository
- ✅ Repositories Spring Data JPA
- ✅ Consultas customizadas com JPQL `@Query`
- ✅ Métodos de busca: por documento, email, nome, país, status
- ✅ Filtragem de soft delete em todas as consultas

### CA-04: Camada de Serviço
- ⏳ `PartyService` com operações CRUD
- ⏳ Padrão strategy de validação para documentos de identificação
- ⏳ Validadores: CPF, CNPJ, SSN, EIN, Padrão
- ⏳ Tratamento de exceções: ValidationException, NotFoundException, DuplicateException

### CA-05: API REST
- ⏳ Controllers: PartyController, PersonController, OrganizationController
- ⏳ DTOs: CreatePersonRequest, CreateOrganizationRequest, PartyResponse, etc.
- ⏳ Tratador de exceções com códigos HTTP apropriados
- ⏳ Documentação OpenAPI acessível em `/swagger-ui.html`

### CA-06: Migração de Banco
- ⏳ Migração Flyway: `V1__create_party_model.sql`
- ⏳ Todas as tabelas, índices, constraints definidos
- ⏳ Migração executa com sucesso em banco limpo

### CA-07: Setup Docker
- ⏳ `docker-compose.yml` com serviço PostgreSQL
- ⏳ Variáveis de ambiente para conexão de banco
- ⏳ Montagem de volume para persistência de dados

### CA-08: Testes
- ⏳ 70% testes unitários (camada de serviço + validadores)
- ⏳ 20% testes de integração (repositories com TestContainers)
- ⏳ 10% testes de API (controllers com MockMvc)
- ⏳ Cobertura JaCoCo: 80% linha, 70% branch (imposto por Maven verify)

### CA-09: Documentação
- ✅ README.md com visão geral do projeto e instruções de setup
- ✅ REQUIREMENTS.md com especificações completas (este documento)
- ✅ docs/IDENTIFICATION_STRATEGY.md com decisão arquitetural
- ⏳ JavaDoc para APIs públicas (métodos de serviço, controllers)

### CA-10: Qualidade de Código
- ⏳ Sem issues críticos/blocker do SonarQube
- ⏳ Estilo de código consistente (Google Java Style Guide)
- ⏳ Sem warnings do compilador
- ⏳ Todos os testes passando (`mvn clean verify`)

---

## Próximos Passos (Implementação FASE 1)

### Sprint 1: Camada de Serviço (Atual)
1. Implementar `PartyService` com operações create/read/update/delete
2. Criar padrão strategy de validação e validadores (CPF, CNPJ, SSN, EIN)
3. Escrever testes unitários para camada de serviço (meta: 80+ testes)
4. Tratamento de exceções (exceções customizadas + respostas de erro)

### Sprint 2: API REST
1. Criar DTOs (objetos de requisição/resposta)
2. Implementar controllers (PartyController, PersonController, OrganizationController)
3. Configurar tratador de exceções (`@ControllerAdvice`)
4. Escrever testes de API com MockMvc (meta: 15+ testes)
5. Configurar SpringDoc OpenAPI

### Sprint 3: Banco de Dados & Docker
1. Criar migração Flyway a partir de `database/schema-phase1.sql`
2. Escrever testes de integração com TestContainers (meta: 25+ testes)
3. Criar `docker-compose.yml` para desenvolvimento local
4. Testar end-to-end com containers Docker

### Sprint 4: Documentação & Polimento
1. Completar JavaDoc para todas as APIs públicas
2. Verificar cobertura de testes (relatório JaCoCo)
3. Executar análise SonarQube
4. Atualizar README.md com instruções finais de setup
5. Criar CHANGELOG.md

---

## Referências

- **Padrão Party Model**: Martin Fowler - Analysis Patterns (Capítulo 2)
- **SAP Party Model**: https://help.sap.com/docs/SAP_S4HANA_CLOUD/
- **Melhores Práticas Spring Boot**: https://docs.spring.io/spring-boot/docs/current/reference/html/
- **Design de API REST**: https://restfulapi.net/
- **Desenvolvimento Orientado a Testes**: Kent Beck - Test Driven Development: By Example

---

**Versão do Documento**: 1.0  
**Última Atualização**: 21/02/2024  
**Status**: FASE 1 - Em Progresso
