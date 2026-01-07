# Refatoração de Variáveis de Ambiente - Finance Manager

## Resumo das Mudanças

Este documento descreve a refatoração realizada para migrar as URLs hardcoded dos serviços para usar variáveis de ambiente, permitindo configuração dinâmica via Kubernetes ConfigMap.

---

## 📁 Estrutura de Environments Criada

### Pasta: `frontend/src/environments/`

#### Arquivo: `environment.ts` (Desenvolvimento)
```typescript
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080/api'
};
```

#### Arquivo: `environment.production.ts` (Produção)
```typescript
export const environment = {
  production: true,
  apiBaseUrl: typeof window !== 'undefined' && (window as any).__API_BASE_URL__ 
    ? (window as any).__API_BASE_URL__ 
    : 'http://finance-manager-backend'
};
```

---

## 🔄 Serviços Refatorados

Todos os serviços foram atualizados para importar e usar `environment.apiBaseUrl`:

### 1. **AuthService** (`src/app/services/auth-service/auth.service.ts`)
- **Antes:** `private readonly baseUrl: string = 'http://localhost:8080/api/auth';`
- **Depois:** `private readonly baseUrl: string = \`${environment.apiBaseUrl}/auth\`;`
- **Adicionado:** `import { environment } from '../../../environments/environment';`

### 2. **ExpenseService** (`src/app/services/expense-service/expense.service.ts`)
- **Antes:** `private readonly baseUrl: string = 'http://localhost:8080/api/expenses';`
- **Depois:** `private readonly baseUrl: string = \`${environment.apiBaseUrl}/expenses\`;`
- **Adicionado:** `import { environment } from '../../../environments/environment';`

### 3. **IncomeService** (`src/app/services/income-service/income.service.ts`)
- **Antes:** `private readonly baseUrl: string = 'http://localhost:8080/api/incomes';`
- **Depois:** `private readonly baseUrl: string = \`${environment.apiBaseUrl}/incomes\`;`
- **Adicionado:** `import { environment } from '../../../environments/environment';`

### 4. **FinancialService** (`src/app/services/financial-service/financial.service.ts`)
- **Antes:** `private readonly baseUrl = 'http://localhost:8080/api/financial-summary';`
- **Depois:** `private readonly baseUrl = \`${environment.apiBaseUrl}/financial-summary\`;`
- **Adicionado:** `import { environment } from '../../../environments/environment';`

---

## 🐳 Configuração Docker

### Arquivo: `frontend/Dockerfile`
O Dockerfile foi atualizado para:
1. **Usar Nginx** como servidor web (ao invés de Node.js)
2. **Injetar variáveis de ambiente** dinamicamente através do script de entrypoint
3. **Servir a aplicação Angular otimizada** com cache adequado

**Principais mudanças:**
- Stage 2 agora usa `nginx:alpine`
- Utiliza o diretório de build do Angular (`dist/frontend/browser`)
- Copia configuração customizada do Nginx
- Instala `gettext` para usar `envsubst`
- Define entrypoint customizado

### Arquivo: `frontend/docker-entrypoint.sh`
Script que:
- Substitui `${API_BASE_URL}` no `index.html` pela variável de ambiente real
- Inicia o Nginx com daemon off

### Arquivo: `frontend/nginx.conf`
Configuração otimizada do Nginx com:
- Compressão gzip ativada
- Cache inteligente para assets estáticos (30 dias)
- Sem cache para `index.html` (para sempre buscar versão mais recente)
- SPA routing com `try_files`
- Limite de tamanho de upload configurável

---

## ☸️ Configuração Kubernetes

### Arquivo: `k8s/frontend/configmap.yaml` (Existente)
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: frontend-config
  namespace: finance-manager
data:
  API_BASE_URL: http://finance-manager-backend
```

**Nota:** Para diferentes ambientes, altere o valor de `API_BASE_URL`:
- **Desenvolvimento:** `http://localhost:8080/api`
- **Staging:** `http://backend-staging.example.com`
- **Produção:** `http://finance-manager-backend` (ou seu domínio real)

### Arquivo: `k8s/frontend/deployment.yaml` (Novo)
- Lê a variável `API_BASE_URL` do ConfigMap
- Pass a variável como env var ao container
- 2 réplicas por padrão
- Health checks configurados
- Resource limits definidos

### Arquivo: `k8s/frontend/service.yaml` (Novo)
- Tipo: LoadBalancer
- Expõe porta 80
- Integrado com o Deployment

---

## 🚀 Como Usar

### Desenvolvimento Local
```bash
# Angular detectará automaticamente que está em dev
npm start
# Usa environment.ts com API_BASE_URL = 'http://localhost:8080/api'
```

### Build de Produção
```bash
npm run build
# Usa environment.production.ts
```

### Docker Local
```bash
# Build da imagem
docker build -t finance-manager-frontend:v1.0.0 .

# Run com variável de ambiente
docker run -e API_BASE_URL=http://localhost:8080/api -p 80:80 finance-manager-frontend:v1.0.0
```

### Kubernetes
```bash
# Apply das configurações
kubectl apply -f k8s/frontend/configmap.yaml
kubectl apply -f k8s/frontend/deployment.yaml
kubectl apply -f k8s/frontend/service.yaml

# Para alterar a URL da API em tempo de execução
kubectl set env deployment/frontend API_BASE_URL=http://novo-backend -n finance-manager
```

---

## 🔑 Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `API_BASE_URL` | URL base da API | `http://finance-manager-backend` (prod) ou `http://localhost:8080/api` (dev) |

---

## ✅ Benefícios da Refatoração

1. ✓ **Configuração dinâmica:** Alterar URL da API sem rebuild
2. ✓ **Múltiplos ambientes:** Dev, Staging, Produção com mesma imagem
3. ✓ **Kubernetes-friendly:** Integrado com ConfigMap
4. ✓ **Seguro:** Sem URLs hardcoded no código
5. ✓ **Otimizado:** Nginx ao invés de Node.js em produção
6. ✓ **Escalável:** Suporta health checks e múltiplas réplicas
7. ✓ **Cache inteligente:** Assets estáticos com cache longo

---

## 📝 Próximos Passos (Opcional)

1. Atualizar CI/CD para fazer build da imagem Docker
2. Configurar Ingress para o frontend (ao invés de LoadBalancer)
3. Adicionar SSL/TLS com cert-manager
4. Implementar RBAC e ServiceAccount customizado
5. Adicionar PersistentVolume se necessário

