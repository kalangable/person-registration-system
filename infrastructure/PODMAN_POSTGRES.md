# Comandos Podman para PostgreSQL

## Criar e iniciar container PostgreSQL

```bash
# Criar container PostgreSQL 16 (última versão estável)
podman run -d \
  --name akstack-foundation-postgres \
  -e POSTGRES_DB=akstack_foundation \
  -e POSTGRES_USER=akstack \
  -e POSTGRES_PASSWORD=akstack123 \
  -p 5432:5432 \
  -v akstack-foundation-data:/var/lib/postgresql/data \
  postgres:16-alpine

# Verificar se o container está rodando
podman ps

# Ver logs do container
podman logs akstack-foundation-postgres

# Parar o container
podman stop akstack-foundation-postgres

# Iniciar o container novamente
podman start akstack-foundation-postgres

# Remover o container (dados serão preservados no volume)
podman rm akstack-foundation-postgres

# Remover o volume (CUIDADO: apaga todos os dados!)
podman volume rm akstack-foundation-data

# Conectar ao PostgreSQL via psql
podman exec -it akstack-foundation-postgres psql -U akstack -d akstack_foundation
```

## Comandos úteis dentro do psql

```sql
-- Listar todas as tabelas
\dt

-- Descrever estrutura de uma tabela
\d party
\d person
\d organization

-- Ver todas as databases
\l

-- Ver todos os schemas
\dn

-- Ver changelog do Liquibase
SELECT * FROM databasechangelog;

-- Sair do psql
\q
```

## Configuração das variáveis de ambiente (.env)

Certifique-se de que o arquivo `.env` está configurado corretamente:

```properties
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=akstack_foundation
DB_USERNAME=akstack
DB_PASSWORD=akstack123
DB_SCHEMA=public
```

## Testar conexão com o banco

```bash
# Via podman exec
podman exec -it akstack-foundation-postgres psql -U akstack -d akstack_foundation -c "SELECT version();"

# Via cliente local (se tiver psql instalado)
psql -h localhost -p 5432 -U akstack -d akstack_foundation -c "SELECT version();"
```

## Executar migrations do Liquibase manualmente

```bash
# A partir do diretório raiz do projeto
cd C:\develop\sources\person-registration-system

# Executar migrations
mvn liquibase:update -pl services/foundation

# Ver status das migrations
mvn liquibase:status -pl services/foundation

# Rollback da última migration
mvn liquibase:rollback -Dliquibase.rollbackCount=1 -pl services/foundation

# Ver SQL que será executado (sem executar)
mvn liquibase:updateSQL -pl services/foundation
```

## Notas importantes

1. **Porta 5432**: Certifique-se de que a porta 5432 não está sendo usada por outro processo
2. **Volume persistente**: Os dados são armazenados no volume `akstack-foundation-data` e persistem mesmo após remover o container
3. **Alpine**: Imagem `postgres:16-alpine` é mais leve (menor tamanho)
4. **Credenciais**: Estas são credenciais de desenvolvimento. NÃO use em produção!
5. **Spring Boot**: Ao iniciar a aplicação, o Liquibase executará as migrations automaticamente
