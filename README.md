# LunaVita - Sistema Completo Containerizado

Este diretório contém a orquestração Docker de todos os serviços LunaVita.

## 🏗️ Arquitetura

```
┌─────────────┐
│   TotemUI   │  (Frontend - Next.js - Port 3000)
└──────┬──────┘
       │
       ├──────────┐
       │          │
┌──────▼──────┐  ┌▼──────────┐  ┌─────────────┐
│  LunaCore   │  │ TotemAPI  │  │  LunaPay    │
│   (Auth)    │  │ (Clinical)│  │  (Payment)  │
│  Port 8080  │  │ Port 8081 │  │  Port 8082  │
└─────────────┘  └───────────┘  └──────┬──────┘
                                        │
                                 ┌──────▼──────┐
                                 │  PostgreSQL │
                                 │  Port 5432  │
                                 └─────────────┘
```

## 🚀 Como Usar

### 1. Primeira Execução

```powershell
# Copiar arquivo de ambiente
cp .env.example .env

# Editar .env e configurar suas variáveis (especialmente JWT_SECRET e senhas)

# Build e start de todos os serviços
docker-compose up --build
```

### 2. Execuções Subsequentes

```powershell
# Iniciar serviços
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar serviços
docker-compose down

# Parar e remover volumes (CUIDADO: apaga banco de dados)
docker-compose down -v
```

### 3. Build Individual

```powershell
# Rebuild apenas um serviço
docker-compose up --build lunacore
docker-compose up --build lunapay
docker-compose up --build totemapi
docker-compose up --build totemui
```

## 📦 Serviços

| Serviço | Porta | Descrição |
|---------|-------|-----------|
| **TotemUI** | 3000 | Interface web (Next.js) |
| **LunaCore** | 8080 | Autenticação e usuários |
| **TotemAPI** | 8081 | Pacientes e agendamentos |
| **LunaPay** | 8082 | Processamento de pagamentos |
| **PostgreSQL** | 5432 | Banco de dados LunaPay |

## 🔐 Usuários de Teste

Após iniciar os containers, use estes usuários para login:

| Email | Senha | Role |
|-------|-------|------|
| adm@lunavita.com | 123456 | ADMIN |
| recepcao@lunavita.com | 123456 | RECEPTION |
| medico@lunavita.com | 123456 | DOCTOR |

## 🩺 Health Checks

```powershell
# Verificar status de todos os containers
docker-compose ps

# Health check individual
curl http://localhost:8080/actuator/health  # LunaCore
curl http://localhost:8081/actuator/health  # TotemAPI
curl http://localhost:8082/actuator/health  # LunaPay
curl http://localhost:3000                   # TotemUI
```

## 🐛 Troubleshooting

### Container não inicia
```powershell
# Ver logs do serviço
docker-compose logs lunacore
docker-compose logs lunapay
docker-compose logs totemapi
docker-compose logs totemui
```

### Resetar banco de dados
```powershell
docker-compose down -v
docker-compose up -d postgres
docker-compose up -d
```

### Rebuild completo (limpar cache)
```powershell
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

## 🌐 URLs de Acesso

- **Frontend**: http://localhost:3000
- **LunaCore API**: http://localhost:8080
- **TotemAPI**: http://localhost:8081
- **LunaPay API**: http://localhost:8082

## 📝 Notas de Produção

Antes de deploy em produção:

1. ⚠️ **TROCAR JWT_SECRET** no `.env`
2. ⚠️ **TROCAR senhas do PostgreSQL**
3. ✅ Configurar HTTPS/TLS
4. ✅ Configurar backup do volume PostgreSQL
5. ✅ Ajustar limites de recursos (CPU/Memory)
6. ✅ Configurar logs centralizados
7. ✅ Habilitar monitoramento (Prometheus/Grafana)

## 🔧 Customização

### Ajustar recursos
Edite `docker-compose.yml` e adicione:

```yaml
services:
  lunacore:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '0.5'
          memory: 512M
```

### Adicionar variáveis de ambiente
Edite `.env` e adicione suas variáveis, depois referencie no `docker-compose.yml`.
