# 🌐 Configuração de URLs - LunaTotem Frontend

## 📍 Onde ficam as configurações?

```
/TotemUI/
   ├─ .env.local        ← Desenvolvimento (localhost)
   ├─ .env.production   ← Produção (domínios reais)
   └─ src/
```

## 🎯 URLs Configuradas

### Desenvolvimento (`.env.local`)
```env
NEXT_PUBLIC_LUNACORE_URL=http://localhost:8080
NEXT_PUBLIC_LUNATOTEM_API_URL=http://localhost:8081
NEXT_PUBLIC_LUNAPAY_URL=http://localhost:8082
```

### Produção (`.env.production`)
```env
NEXT_PUBLIC_LUNACORE_URL=https://core.minhaluna.com
NEXT_PUBLIC_LUNATOTEM_API_URL=https://api-totem.minhaluna.com
NEXT_PUBLIC_LUNAPAY_URL=https://pay.minhaluna.com
```

## 🧠 Por que no FRONT e não no BACK?

| Aspecto | Explicação |
|---------|------------|
| **Frontend precisa saber** | O cliente (navegador) precisa enviar requisições para diferentes backends |
| **Múltiplos backends** | Totem API, LunaCore e LunaPay são serviços separados |
| **Roteamento no cliente** | Next.js precisa saber para onde enviar cada tipo de requisição |
| **Backend só responde** | Os backends não precisam dessas URLs - eles apenas validam tokens |

## 🚀 Como usar no código

### Exemplo básico
```typescript
const CORE_URL = process.env.NEXT_PUBLIC_LUNACORE_URL;
const TOTEM_API_URL = process.env.NEXT_PUBLIC_LUNATOTEM_API_URL;
const PAY_URL = process.env.NEXT_PUBLIC_LUNAPAY_URL;
```

### Exemplo de autenticação
```typescript
// Login no LunaCore
const response = await fetch(`${CORE_URL}/auth/login`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email, password })
});
const { token } = await response.json();
```

### Exemplo de busca de pacientes
```typescript
// Buscar pacientes no LunaTotem API
const response = await fetch(`${TOTEM_API_URL}/patients`, {
  headers: { 
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});
const patients = await response.json();
```

### Exemplo de pagamento
```typescript
// Processar pagamento no LunaPay
const response = await fetch(`${PAY_URL}/payments`, {
  method: 'POST',
  headers: { 
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({ amount, method: 'PIX' })
});
const payment = await response.json();
```

## 📋 Tabela de responsabilidades

| Projeto | Precisa das URLs? | Arquivo | Motivo |
|---------|------------------|---------|--------|
| **LunaTotem FRONT** | ✅ SIM | `.env.local` | Precisa saber para onde enviar requisições |
| **LunaTotem API** | ❌ NÃO | - | Só responde e valida tokens |
| **LunaCore** | ❌ NÃO | - | Só responde e emite tokens |
| **LunaPay API** | ❌ NÃO | - | Só responde requisições de pagamento |

## 🔄 Fluxo de requisições

```
┌─────────────────────────────────────────────────────────────┐
│                    LunaTotem Frontend                        │
│                      (Next.js App)                           │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ .env.local define as URLs
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌──────────────┐      ┌──────────────┐     ┌──────────────┐
│  LunaCore    │      │ LunaTotem API│     │  LunaPay API │
│ :8080        │      │ :8081        │     │ :8082        │
├──────────────┤      ├──────────────┤     ├──────────────┤
│ • Login      │      │ • Pacientes  │     │ • Pagamentos │
│ • Registro   │      │ • Agendas    │     │ • PIX        │
│ • JWT Tokens │      │ • Médicos    │     │ • Asaas      │
└──────────────┘      └──────────────┘     └──────────────┘
```

## ⚙️ Configuração por ambiente

### Desenvolvimento Local
1. Use `.env.local`
2. Rode os 3 backends em portas diferentes:
   - LunaCore: `http://localhost:8080`
   - LunaTotem API: `http://localhost:8081`
   - LunaPay: `http://localhost:8082`

### Produção
1. Use `.env.production`
2. Configure domínios reais com HTTPS
3. Certifique-se de que todos os backends estão acessíveis

## ✅ Checklist de configuração

- [ ] Criar `.env.local` com URLs localhost
- [ ] Criar `.env.production` com URLs de produção
- [ ] Atualizar código para usar as variáveis de ambiente
- [ ] Testar em desenvolvimento
- [ ] Substituir URLs hardcoded por variáveis
- [ ] Configurar CORS nos backends para aceitar o frontend
- [ ] Validar em produção

## 🚨 Erros comuns

### Erro: "Cannot read env variable"
**Causa**: Variável não tem o prefixo `NEXT_PUBLIC_`

**Solução**: 
```env
# ❌ Errado
LUNACORE_URL=http://localhost:8080

# ✅ Correto
NEXT_PUBLIC_LUNACORE_URL=http://localhost:8080
```

### Erro: CORS blocked
**Causa**: Backend não aceita requisições do frontend

**Solução**: Configure CORS no backend:
```java
@CrossOrigin(origins = "http://localhost:3000")
```

### Erro: 404 Not Found
**Causa**: URL incorreta ou backend não está rodando

**Solução**: Verifique se todos os backends estão rodando nas portas corretas

## 📚 Referências

- [Next.js Environment Variables](https://nextjs.org/docs/basic-features/environment-variables)
- [Documentação JWT do projeto](../TotemAPI/JWT_AUTHENTICATION_README.md)
- [Arquitetura Multi-tenant](../TotemAPI/JWT_ARCHITECTURE.md)
