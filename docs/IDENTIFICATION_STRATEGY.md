# Estratégia de Documentos de Identificação - Registro de Decisão Arquitetural

## Status
**Aceito** - Fevereiro 2024

---

## Contexto

Ao projetar o Sistema de Cadastro de Pessoas baseado no padrão **Party Model**, enfrentamos uma decisão arquitetural crítica:

**Onde os documentos de identificação (CPF, CNPJ, SSN, EIN, etc.) devem ser armazenados?**

### Opções Consideradas

1. **Opção A**: Armazenar campos de identificação na tabela base `party`
2. **Opção B**: Armazenar campos de identificação nas tabelas especializadas (`person`, `organization`)
3. **Opção C**: Criar uma tabela separada `party_identification` com relacionamento um-para-muitos

---

## Decisão

Decidimos implementar a **Opção B** para a FASE 1, com uma evolução planejada para a **Opção C** na FASE 2.

### FASE 1 (Atual): Identificação nas Tabelas Especializadas
- Tabela `person` tem: `primary_identification_document`, `primary_identification_type`
- Tabela `organization` tem: `primary_identification_document`, `primary_identification_type`
- Cada party tem **um documento de identificação primário**

### FASE 2 (Futuro): Múltiplos Documentos de Identificação
- Adicionar tabela `party_identification` com colunas: `party_id`, `identification_type`, `identification_document`, `is_primary`
- Permitir múltiplos documentos por party (ex: CPF + RG + CNH + Passaporte)
- Remover campos de identificação das tabelas `person` e `organization`
- Migrar dados existentes usando função SQL fornecida

---

## Justificativa

### Por que NÃO Opção A (Identificação na tabela `party`)?

**Problemas**:
1. **Viola Normalização Apropriada (3FN)**:
   - Mistura de responsabilidades: dados da entidade base + dados de documentos especializados
   - `identification_type` só seria significativo para certos tipos de party
   - Nem todos os parties exigem as mesmas regras de identificação

2. **NÃO Segue Padrões da Indústria**:
   - SAP armazena documentos em entidades especializadas (BAPIBUS1006, PartnerIdentification)
   - Oracle Customer Hub usa tabelas de identificação separadas
   - Salesforce armazena documentos em campos customizados nos objetos Person/Organization
   - **Nenhum sistema empresarial importante armazena documentos na tabela party base**

3. **Extensibilidade Limitada**:
   - Dificulta adicionar múltiplos documentos (CPF + RG + CNH)
   - Força desnormalização para suportar múltiplos documentos
   - Requer colunas JSON ou colunas repetidas (design ruim)

4. **Problemas de Segurança de Tipos**:
   - Não pode impor regras de tipo de documento no nível de banco
   - Exemplo: Person deveria ter CPF/SSN, Organization deveria ter CNPJ/EIN
   - Requer apenas validação no nível de aplicação (arriscado)

5. **Complexidade de Busca**:
   - Vantagem alegada: "busca mais fácil em todos os parties"
   - **Realidade**: Busca unificada única é rara em aplicações reais
   - Maioria das buscas são especializadas: "encontrar pessoa por CPF" ou "encontrar organização por CNPJ"
   - Quando necessário, views de banco podem facilmente fornecer busca unificada

### Por que SIM para Opção B (Identificação nas Tabelas Especializadas)?

**Vantagens**:

#### 1. **Segue Normalização de Banco de Dados (3FN)**
- Cada tabela representa uma única entidade coesa
- Sem colunas nullable que só se aplicam a alguns subtipos
- Schema limpo e focado

#### 2. **Corresponde a Padrões da Indústria**
Sistemas empresariais separam armazenamento de documentos:

| Sistema       | Padrão                                                |
|---------------|-------------------------------------------------------|
| SAP           | Business Partner → PartnerIdentification (separado)   |
| Oracle CDM    | Party → Identification (separado)                     |
| Salesforce    | Account/Contact → Custom Fields (especializado)       |
| Microsoft D365| Party → IdentificationDocument (separado)             |

#### 3. **Segurança de Tipos e Validação**
```sql
-- Tabela person pode impor regras de CPF/SSN
ALTER TABLE person ADD CONSTRAINT check_person_id_type 
  CHECK (primary_identification_type IN ('CPF', 'SSN', 'PASSPORT', 'NATIONAL_ID'));

-- Tabela organization pode impor regras de CNPJ/EIN  
ALTER TABLE organization ADD CONSTRAINT check_org_id_type 
  CHECK (primary_identification_type IN ('CNPJ', 'EIN', 'VAT', 'BUSINESS_REG'));
```

#### 4. **Modelo de Entidade Limpo (JPA)**
```java
@Entity
@Table(name = "person")
public class Person extends Party {
    @Column(name = "primary_identification_document")
    private String primaryIdentificationDocument;
    
    @Column(name = "primary_identification_type")
    private String primaryIdentificationType;
    
    // Métodos type-safe
    public void setCpf(String cpf) { /* valida CPF */ }
    public void setSsn(String ssn) { /* valida SSN */ }
}
```

#### 5. **Evolução Fácil para Múltiplos Documentos (FASE 2)**
Começar com documentos em tabelas especializadas torna a migração da FASE 2 simples:

```sql
-- FASE 2: Criar nova tabela
CREATE TABLE party_identification (
    id UUID PRIMARY KEY,
    party_id UUID NOT NULL REFERENCES party(id),
    identification_type VARCHAR(20) NOT NULL,
    identification_document VARCHAR(50) NOT NULL,
    is_primary BOOLEAN DEFAULT false,
    issued_date DATE,
    expiry_date DATE,
    UNIQUE (party_id, identification_type, identification_document)
);

-- Migrar dados da tabela person
INSERT INTO party_identification (id, party_id, identification_type, identification_document, is_primary)
SELECT gen_random_uuid(), id, primary_identification_type, primary_identification_document, true
FROM person
WHERE primary_identification_document IS NOT NULL;

-- Agora person pode ter: CPF + RG + CNH + Passaporte
-- Organization pode ter: CNPJ + Inscrição Estadual + Inscrição Municipal
```

#### 6. **Performance de Busca**
**Alegação**: "Documentos na tabela party tornam a busca mais fácil"

**Realidade**: Buscas especializadas são mais comuns e performáticas:

```sql
-- Consulta comum: Encontrar pessoa por CPF (RÁPIDO com índice na tabela person)
SELECT p.* FROM person p WHERE p.primary_identification_document = '12345678909';

-- Consulta comum: Encontrar organização por CNPJ (RÁPIDO com índice na tabela organization)
SELECT o.* FROM organization o WHERE o.primary_identification_document = '12345678000195';

-- Consulta rara: Encontrar QUALQUER party por qualquer documento (pode usar VIEW)
CREATE VIEW party_documents AS
  SELECT id, party_number, party_type, 'PERSON' as source, 
         primary_identification_type, primary_identification_document
  FROM party p JOIN person ps ON p.id = ps.id
  UNION ALL
  SELECT id, party_number, party_type, 'ORGANIZATION' as source,
         primary_identification_type, primary_identification_document
  FROM party p JOIN organization o ON p.id = o.id;

-- Agora busca unificada é possível quando necessário:
SELECT * FROM party_documents WHERE primary_identification_document = '12345678909';
```

**Índices**:
```sql
-- Índices especializados (menores, mais rápidos)
CREATE INDEX idx_person_identification ON person (primary_identification_type, primary_identification_document);
CREATE INDEX idx_org_identification ON organization (primary_identification_type, primary_identification_document);

-- Muito melhor que:
CREATE INDEX idx_party_identification ON party (primary_identification_type, primary_identification_document);
-- (índice maior, inclui nulls para parties sem documentos, mais lento)
```

#### 7. **Flexibilidade para Diferentes Regras de Documento**
Diferentes tipos de party têm diferentes requisitos de documento:

| Tipo de Party | Documentos Obrigatórios | Documentos Opcionais       |
|---------------|-------------------------| ---------------------------|
| Person (BR)   | CPF                     | RG, CNH, Passaporte        |
| Person (EUA)  | SSN                     | CNH, Passaporte            |
| Org (BR)      | CNPJ                    | Insc. Estadual, Municipal  |
| Org (EUA)     | EIN                     | State Business ID          |

**Opção B permite**:
- Diferentes regras de validação por tipo de entidade
- Modelo de domínio type-safe
- Regras de negócio claras

**Opção A força**:
- Validação genérica na tabela base (fraca)
- Apenas validação no nível de aplicação (propenso a erros)
- Propriedade pouco clara dos campos de documento

---

## Estratégia de Implementação

### FASE 1: Simples e Normalizado (Atual)

**Schema**:
```sql
-- tabela party (entidade base)
CREATE TABLE party (
    id UUID PRIMARY KEY,
    party_number VARCHAR(50) UNIQUE NOT NULL,
    party_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    country CHAR(3) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    -- SEM campos de identificação aqui
);

-- tabela person (um documento primário)
CREATE TABLE person (
    id UUID PRIMARY KEY REFERENCES party(id),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    primary_identification_document VARCHAR(50),
    primary_identification_type VARCHAR(20),
    UNIQUE (primary_identification_type, primary_identification_document)
);

-- tabela organization (um documento primário)
CREATE TABLE organization (
    id UUID PRIMARY KEY REFERENCES party(id),
    legal_name VARCHAR(255) NOT NULL,
    primary_identification_document VARCHAR(50),
    primary_identification_type VARCHAR(20),
    UNIQUE (primary_identification_type, primary_identification_document)
);
```

**Índices**:
```sql
CREATE INDEX idx_person_identification 
  ON person (primary_identification_type, primary_identification_document);

CREATE INDEX idx_org_identification 
  ON organization (primary_identification_type, primary_identification_document);
```

**Entidade JPA (Person)**:
```java
@Entity
@Table(name = "person")
public class Person extends Party {
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    
    @Column(name = "primary_identification_document", length = 50)
    private String primaryIdentificationDocument;
    
    @Column(name = "primary_identification_type", length = 20)
    private String primaryIdentificationType;
    
    // Métodos auxiliares
    public String getIdentificationDisplay() {
        return String.format("%s: %s", primaryIdentificationType, primaryIdentificationDocument);
    }
}
```

**Benefícios**:
- ✅ Schema limpo e normalizado (3FN)
- ✅ Modelo de domínio type-safe
- ✅ Consultas especializadas rápidas
- ✅ Simples de entender e implementar
- ✅ Pronto para evolução da FASE 2

---

### FASE 2: Múltiplos Documentos (Futuro)

**Novo Schema**:
```sql
-- Nova tabela para múltiplos documentos
CREATE TABLE party_identification (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    party_id UUID NOT NULL REFERENCES party(id) ON DELETE CASCADE,
    identification_type VARCHAR(20) NOT NULL,
    identification_document VARCHAR(50) NOT NULL,
    is_primary BOOLEAN DEFAULT false,
    issued_date DATE,
    expiry_date DATE,
    issuing_authority VARCHAR(100),
    issuing_country CHAR(3),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (party_id, identification_type, identification_document)
);

CREATE INDEX idx_party_id_primary ON party_identification (party_id, is_primary);
CREATE INDEX idx_identification_lookup ON party_identification (identification_type, identification_document);
```

**Função de Migração**:
```sql
-- Função para migrar da FASE 1 para FASE 2
CREATE OR REPLACE FUNCTION migrate_identification_to_phase2() 
RETURNS void AS $$
BEGIN
    -- Migrar documentos de person
    INSERT INTO party_identification (party_id, identification_type, identification_document, is_primary)
    SELECT id, primary_identification_type, primary_identification_document, true
    FROM person
    WHERE primary_identification_document IS NOT NULL
      AND primary_identification_type IS NOT NULL;
    
    -- Migrar documentos de organization
    INSERT INTO party_identification (party_id, identification_type, identification_document, is_primary)
    SELECT id, primary_identification_type, primary_identification_document, true
    FROM organization
    WHERE primary_identification_document IS NOT NULL
      AND primary_identification_type IS NOT NULL;
    
    -- Remover colunas antigas (após verificação)
    -- ALTER TABLE person DROP COLUMN primary_identification_document;
    -- ALTER TABLE person DROP COLUMN primary_identification_type;
    -- ALTER TABLE organization DROP COLUMN primary_identification_document;
    -- ALTER TABLE organization DROP COLUMN primary_identification_type;
END;
$$ LANGUAGE plpgsql;
```

**Entidade JPA (FASE 2)**:
```java
@Entity
@Table(name = "party_identification")
public class PartyIdentification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;
    
    @Column(name = "identification_type", nullable = false)
    private String identificationType;
    
    @Column(name = "identification_document", nullable = false)
    private String identificationDocument;
    
    @Column(name = "is_primary")
    private Boolean isPrimary = false;
    
    @Column(name = "issued_date")
    private LocalDate issuedDate;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
}

// Entidade Party ganha coleção
@Entity
public class Party {
    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyIdentification> identifications = new ArrayList<>();
    
    public PartyIdentification getPrimaryIdentification() {
        return identifications.stream()
            .filter(PartyIdentification::getIsPrimary)
            .findFirst()
            .orElse(null);
    }
}
```

**Casos de Uso Habilitados**:
```java
// Pessoa com múltiplos documentos
Person person = new Person();
person.addIdentification("CPF", "12345678909", true);   // primário
person.addIdentification("RG", "123456789", false);     // secundário
person.addIdentification("CNH", "12345678900", false);  // secundário
person.addIdentification("PASSPORT", "AB123456", false); // secundário

// Organização com múltiplos registros
Organization org = new Organization();
org.addIdentification("CNPJ", "12345678000195", true);  // primário
org.addIdentification("STATE_REG", "123.456.789.012", false); // secundário
org.addIdentification("MUNICIPAL_REG", "12345678", false);    // secundário
```

---

## Comparação de Performance

### Consulta: Encontrar party por documento de identificação

**Opção A (Documento na tabela party)**:
```sql
-- Consultar todos os parties
SELECT * FROM party WHERE primary_identification_document = '12345678909';
-- Índice: party(primary_identification_document) - cobre TODOS os parties
-- Tamanho do índice: GRANDE (inclui todas as pessoas + organizações)
-- Performance: BOA (mas inclui nulls, índice maior)
```

**Opção B (Documento nas tabelas person/organization)**:
```sql
-- Consultar tipo de entidade específico
SELECT * FROM person WHERE primary_identification_document = '12345678909';
-- Índice: person(primary_identification_document) - cobre APENAS pessoas
-- Tamanho do índice: PEQUENO (apenas pessoas)
-- Performance: EXCELENTE (índice menor, sem nulls, mais seletivo)

-- Se o tipo for desconhecido, usar VIEW
SELECT * FROM party_documents WHERE primary_identification_document = '12345678909';
-- Combina índices de person + organization
-- Performance: BOA (usa ambos os índices especializados)
```

**Conclusão**: Opção B é **mais rápida para consultas comuns** (buscas específicas por tipo).

---

### Consulta: Contar parties por tipo de identificação

**Opção A**:
```sql
SELECT identification_type, COUNT(*) 
FROM party 
WHERE identification_type IS NOT NULL
GROUP BY identification_type;
-- Deve escanear toda a tabela party (pessoas + organizações + nulls)
```

**Opção B**:
```sql
SELECT 'CPF' as type, COUNT(*) FROM person WHERE primary_identification_type = 'CPF'
UNION ALL
SELECT 'CNPJ' as type, COUNT(*) FROM organization WHERE primary_identification_type = 'CNPJ';
-- Escaneia apenas tabelas relevantes com índices seletivos
-- Mais rápido para datasets grandes
```

---

## Abordando Preocupações Comuns

### Preocupação 1: "Busca em todos os parties é mais difícil"

**Resposta**: 
- Busca unificada é **rara na prática** (geralmente busca por tipo)
- Quando necessário, criar uma VIEW de banco (mostrado acima)
- View usa índices de ambas as tabelas (eficiente)

### Preocupação 2: "Mais tabelas para manter"

**Resposta**:
- Design normalizado é **prática padrão de banco de dados**
- Custo de manutenção é mínimo (migrações, índices)
- Benefícios (segurança de tipos, performance, extensibilidade) superam custos

### Preocupação 3: "Consultas JPA são mais complexas"

**Resposta**:
```java
// Opção A: Consultar tabela party (parece mais simples)
Party party = partyRepository.findByIdentificationDocument("12345678909");
// Problema: Retorna Party base, precisa fazer cast para Person/Organization (feio)
if (party instanceof Person) {
    Person person = (Person) party;
}

// Opção B: Consultar repository específico (mais limpo)
Person person = personRepository.findByIdentificationDocument("12345678909");
// Sem casting necessário, type-safe, intenção clara
```

### Preocupação 4: "Não pode adicionar documentos a Party sem conhecer o tipo"

**Resposta**:
- Isso é **uma funcionalidade, não um bug**
- Party sem tipo é sem sentido (deve ser Person ou Organization)
- Força seleção explícita de tipo de entidade (melhor UX, API mais clara)

---

## Caminho de Migração (FASE 1 → FASE 2)

### Passo 1: Verificar Qualidade de Dados da FASE 1
```sql
-- Verificar documentos duplicados
SELECT primary_identification_type, primary_identification_document, COUNT(*)
FROM (
    SELECT primary_identification_type, primary_identification_document FROM person
    UNION ALL
    SELECT primary_identification_type, primary_identification_document FROM organization
) all_docs
GROUP BY primary_identification_type, primary_identification_document
HAVING COUNT(*) > 1;
```

### Passo 2: Criar Schema da FASE 2
```sql
-- Executar database/schema-phase2.sql
-- Cria tabela party_identification + índices
```

### Passo 3: Migrar Dados
```sql
-- Executar função de migração
SELECT migrate_identification_to_phase2();

-- Verificar migração
SELECT pi.party_id, p.party_type, pi.identification_type, pi.identification_document
FROM party_identification pi
JOIN party p ON pi.party_id = p.id
WHERE pi.is_primary = true;
```

### Passo 4: Atualizar Código da Aplicação
```java
// Atualizar entidades (adicionar relacionamento OneToMany)
// Atualizar repositories (consultar tabela party_identification)
// Atualizar services (lidar com múltiplos documentos)
// Atualizar DTOs (incluir lista de identificações)
```

### Passo 5: Remover Colunas Antigas (Após Testes)
```sql
-- Apenas após FASE 2 ser totalmente testada e deployada
ALTER TABLE person DROP COLUMN primary_identification_document;
ALTER TABLE person DROP COLUMN primary_identification_type;
ALTER TABLE organization DROP COLUMN primary_identification_document;
ALTER TABLE organization DROP COLUMN primary_identification_type;
```

---

## Conclusão

**Decisão**: Armazenar documentos de identificação em tabelas especializadas (`person`, `organization`) para a FASE 1.

**Razões**:
1. ✅ Segue normalização de banco de dados (3FN)
2. ✅ Corresponde a padrões da indústria (SAP, Oracle, Salesforce)
3. ✅ Modelo de domínio type-safe
4. ✅ Melhor performance de consulta (índices especializados)
5. ✅ Evolução fácil para múltiplos documentos (FASE 2)
6. ✅ Separação clara de responsabilidades
7. ✅ Constraints no nível de banco possíveis

**Trade-offs**:
- ❌ Ligeiramente mais tabelas para gerenciar (custo mínimo)
- ❌ Busca unificada requer VIEW (fácil de criar)

**Próximos Passos**:
1. Implementar FASE 1 com documentos nas tabelas person/organization ✅
2. Construir aplicação com modelo de entidade limpo ✅
3. Monitorar padrões de uso e requisitos de documentos
4. Migrar para FASE 2 quando múltiplos documentos forem necessários

---

## Referências

- **SAP Party Model**: https://help.sap.com/docs/SAP_S4HANA_CLOUD/
- **Oracle Customer Data Hub**: https://docs.oracle.com/en/industries/health-sciences/cdm/
- **Modelo de Dados Salesforce**: https://developer.salesforce.com/docs/atlas.en-us.object_reference.meta/
- **Normalização de Banco de Dados**: https://pt.wikipedia.org/wiki/Normaliza%C3%A7%C3%A3o_de_dados
- **Martin Fowler - Analysis Patterns**: Capítulo 2 (Padrão Party)

---

**Versão do Documento**: 1.0  
**Autor**: Equipe de Arquitetura de Sistemas  
**Última Atualização**: 21/02/2024  
**Status**: Aceito e Implementado na FASE 1
