-- =====================================================================
-- Script para popular tabelas com dados FAKE
-- =====================================================================
-- IMPORTANTE: Todos os dados aqui são fictícios e gerados aleatoriamente
-- Não representam pessoas ou empresas reais
-- =====================================================================

-- =====================================================================
-- PESSOAS FÍSICAS (6 registros)
-- =====================================================================

-- Pessoa 1: John Smith (USA - SSN)
INSERT INTO party (id, party_number, party_type, email, phone, is_active, is_deleted, created_at, updated_at)
VALUES (1, '1000001', 'PERSON', 'john.smith@email.com', '+1-555-0101', true, false, NOW(), NOW());

INSERT INTO person (id, first_name, middle_name, last_name, full_name, date_of_birth, gender, primary_identification_type, primary_identification_document)
VALUES (1, 'John', 'Michael', 'Smith', 'John Michael Smith', '1985-03-15', 'MALE', 'SSN', '123-45-6789');

-- Pessoa 2: Maria Silva (Brasil - CPF)
INSERT INTO party (id, party_number, party_type, email, phone, is_active, is_deleted, created_at, updated_at)
VALUES (2, '1000002', 'PERSON', 'maria.silva@email.com', '+55-11-98765-4321', true, false, NOW(), NOW());

INSERT INTO person (id, first_name, middle_name, last_name, full_name, date_of_birth, gender, primary_identification_type, primary_identification_document)
VALUES (2, 'Maria', 'Aparecida', 'Silva', 'Maria Aparecida Silva', '1990-07-22', 'FEMALE', 'CPF', '123.456.789-01');

-- Pessoa 3: Carlos Santos (Brasil - CPF)
INSERT INTO party (id, party_number, party_type, email, phone, is_active, is_deleted, created_at, updated_at)
VALUES (3, '1000003', 'PERSON', 'carlos.santos@email.com', '+55-21-91234-5678', true, false, NOW(), NOW());

INSERT INTO person (id, first_name, middle_name, last_name, full_name, date_of_birth, gender, primary_identification_type, primary_identification_document)
VALUES (3, 'Carlos', 'Eduardo', 'Santos', 'Carlos Eduardo Santos', '1988-11-30', 'MALE', 'CPF', '987.654.321-09');

-- Pessoa 4: Sarah Johnson (USA - SSN)
INSERT INTO party (id, party_number, party_type, email, phone, is_active, is_deleted, created_at, updated_at)
VALUES (4, '1000004', 'PERSON', 'sarah.johnson@email.com', '+1-555-0202', true, false, NOW(), NOW());

INSERT INTO person (id, first_name, middle_name, last_name, full_name, date_of_birth, gender, primary_identification_type, primary_identification_document)
VALUES (4, 'Sarah', 'Anne', 'Johnson', 'Sarah Anne Johnson', '1992-05-18', 'FEMALE', 'SSN', '987-65-4321');

-- Pessoa 5: Ana Oliveira (Brasil - CPF)
INSERT INTO party (id, party_number, party_type, email, phone, is_active, is_deleted, created_at, updated_at)
VALUES (5, '1000005', 'PERSON', 'ana.oliveira@email.com', '+55-31-99876-5432', true, false, NOW(), NOW());

INSERT INTO person (id, first_name, middle_name, last_name, full_name, date_of_birth, gender, primary_identification_type, primary_identification_document)
VALUES (5, 'Ana', 'Paula', 'Oliveira', 'Ana Paula Oliveira', '1995-09-08', 'FEMALE', 'CPF', '456.789.123-45');

-- Pessoa 6: Robert Williams (USA - SSN)
INSERT INTO party (id, party_number, party_type, email, phone, is_active, is_deleted, created_at, updated_at)
VALUES (6, '1000006', 'PERSON', 'robert.williams@email.com', '+1-555-0303', true, false, NOW(), NOW());

INSERT INTO person (id, first_name, middle_name, last_name, full_name, date_of_birth, gender, primary_identification_type, primary_identification_document)
VALUES (6, 'Robert', 'James', 'Williams', 'Robert James Williams', '1983-12-25', 'MALE', 'SSN', '555-12-3456');

-- =====================================================================
-- ORGANIZAÇÕES / EMPRESAS (7 registros)
-- =====================================================================

-- Empresa 1: Tech Solutions Brasil (CNPJ)
INSERT INTO party (id, party_number, party_type, email, phone, is_active, is_deleted, created_at, updated_at)
VALUES (7, '1000007', 'ORGANIZATION', 'contato@techsolutions.com.br', '+55-11-3000-1000', true, false, NOW(), NOW());

INSERT INTO organization (id, legal_name, trade_name, brand_name, founding_date, primary_identification_type, primary_identification_document)
VALUES (7, 'Tech Solutions Brasil Ltda', 'Tech Solutions', 'TechSol', '2015-06-10', 'CNPJ', '12.345.678/0001-90');

-- Empresa 2: Innovation Corp (USA - EIN)
INSERT INTO party (id, party_number, party_type, email, phone, is_active, is_deleted, created_at, updated_at)
VALUES (8, '1000008', 'ORGANIZATION', 'info@innovationcorp.com', '+1-555-1000', true, false, NOW(), NOW());

INSERT INTO organization (id, legal_name, trade_name, brand_name, founding_date, primary_identification_type, primary_identification_document)
VALUES (8, 'Innovation Corporation Inc', 'Innovation Corp', 'InnovCorp', '2010-03-22', 'EIN', '12-3456789');

-- Empresa 3: Comercio Digital Ltda (CNPJ)
INSERT INTO party (id, party_number, party_type, email, phone, is_active, is_deleted, created_at, updated_at)
VALUES (9, '1000009', 'ORGANIZATION', 'vendas@comerciodigital.com.br', '+55-21-2000-3000', true, false, NOW(), NOW());

INSERT INTO organization (id, legal_name, trade_name, brand_name, founding_date, primary_identification_type, primary_identification_document)
VALUES (9, 'Comércio Digital Ltda', 'Comércio Digital', 'DigiShop', '2018-01-15', 'CNPJ', '98.765.432/0001-10');

-- Empresa 4: Global Services Inc (USA - EIN)
INSERT INTO party (id, party_number, party_type, email, phone, is_active, is_deleted, created_at, updated_at)
VALUES (10, '1000010', 'ORGANIZATION', 'contact@globalservices.com', '+1-555-2000', true, false, NOW(), NOW());

INSERT INTO organization (id, legal_name, trade_name, brand_name, founding_date, primary_identification_type, primary_identification_document)
VALUES (10, 'Global Services International Inc', 'Global Services', 'GlobalServ', '2005-11-08', 'EIN', '98-7654321');

-- Empresa 5: Logistica Express Ltda (CNPJ)
INSERT INTO party (id, party_number, party_type, email, phone, is_active, is_deleted, created_at, updated_at)
VALUES (11, '1000011', 'ORGANIZATION', 'sac@logisticaexpress.com.br', '+55-31-4000-5000', true, false, NOW(), NOW());

INSERT INTO organization (id, legal_name, trade_name, brand_name, founding_date, primary_identification_type, primary_identification_document)
VALUES (11, 'Logística Express Brasil Ltda', 'Logística Express', 'LogExpress', '2012-04-20', 'CNPJ', '45.678.901/0001-23');

-- Empresa 6: Alimentos Naturais Ltda (CNPJ)
INSERT INTO party (id, party_number, party_type, email, phone, is_active, is_deleted, created_at, updated_at)
VALUES (12, '1000012', 'ORGANIZATION', 'contato@alimentosnaturais.com.br', '+55-41-5000-6000', true, false, NOW(), NOW());

INSERT INTO organization (id, legal_name, trade_name, brand_name, founding_date, primary_identification_type, primary_identification_document)
VALUES (12, 'Alimentos Naturais do Brasil Ltda', 'Alimentos Naturais', 'NaturalFood', '2019-08-05', 'CNPJ', '78.901.234/0001-56');

-- Empresa 7: Consultoria Empresarial Ltda (CNPJ)
INSERT INTO party (id, party_number, party_type, email, phone, is_active, is_deleted, created_at, updated_at)
VALUES (13, '1000013', 'ORGANIZATION', 'info@consultoriaempresarial.com.br', '+55-51-6000-7000', true, false, NOW(), NOW());

INSERT INTO organization (id, legal_name, trade_name, brand_name, founding_date, primary_identification_type, primary_identification_document)
VALUES (13, 'Consultoria Empresarial Brasil Ltda', 'CE Brasil', 'CEBrasil', '2016-02-28', 'CNPJ', '34.567.890/0001-78');

-- =====================================================================
-- Ajustar sequence para próximo ID disponível
-- =====================================================================
SELECT setval('party_id_seq', (SELECT MAX(id) FROM party));
SELECT setval('party_number_seq', (SELECT MAX(CAST(party_number AS INTEGER)) FROM party));

-- =====================================================================
-- Verificar dados inseridos
-- =====================================================================
-- SELECT COUNT(*) as total_parties FROM party;
-- SELECT COUNT(*) as total_persons FROM person;
-- SELECT COUNT(*) as total_organizations FROM organization;
-- 
-- SELECT p.party_number, p.party_type, p.email, per.full_name 
-- FROM party p 
-- JOIN person per ON p.id = per.id 
-- ORDER BY p.id;
-- 
-- SELECT p.party_number, p.party_type, p.email, org.legal_name 
-- FROM party p 
-- JOIN organization org ON p.id = org.id 
-- ORDER BY p.id;
