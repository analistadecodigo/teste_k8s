# ⚡ Quick Start - Comece em 5 minutos

## 1️⃣ Verificar Mudanças

Para ver o que foi refatorado:

```bash
# Ver os serviços modificados
cat frontend/src/app/services/auth-service/auth.service.ts | grep "environment"

# Ver o arquivo de environments
cat frontend/src/environments/environment.ts
cat frontend/src/environments/environment.production.ts

# Ver a injeção no HTML
cat frontend/src/index.html | grep "API_BASE_URL"
```

---

## 2️⃣ Testar Localmente

### Pré-requisitos
- Node.js 20+
- npm ou yarn
- Backend rodando em `http://localhost:8080`

### Executar

```bash
cd frontend
npm install
npm start
```

✅ Acesse em `http://localhost:4200`

A aplicação automaticamente usará `http://localhost:8080/api` para requisições.

---

## 3️⃣ Build para Produção

```bash
cd frontend
npm run build
```

Output: `dist/frontend/browser/`

---

## 4️⃣ Docker Local

### Build
```bash
docker build -t finance-manager-frontend:v1.0.0 .
```

### Run
```bash
docker run \
  -e API_BASE_URL=http://localhost:8080/api \
  -p 8080:80 \
  finance-manager-frontend:v1.0.0
```

✅ Acesse em `http://localhost:8080`

---

## 5️⃣ Kubernetes

### Prerequisites
- kubectl configurado
- Cluster Kubernetes (Minikube, Kind, EKS, etc)
- Backend já deployado

### Deploy

```bash
# Criar namespace
kubectl create namespace finance-manager

# Aplicar configs
kubectl apply -f k8s/frontend/configmap.yaml
kubectl apply -f k8s/frontend/deployment.yaml
kubectl apply -f k8s/frontend/service.yaml

# Verificar
kubectl get all -n finance-manager

# Acessar
kubectl port-forward svc/frontend 8080:80 -n finance-manager
```

✅ Acesse em `http://localhost:8080`

---

## 🔧 Alterar URL da API

### Docker
```bash
docker run \
  -e API_BASE_URL=http://seu-backend:8080/api \
  -p 80:80 \
  finance-manager-frontend:v1.0.0
```

### Kubernetes
```bash
# Opção 1: Set env
kubectl set env deployment/frontend \
  API_BASE_URL=http://novo-backend:8080/api \
  -n finance-manager

# Opção 2: Editar ConfigMap
kubectl edit configmap frontend-config -n finance-manager
# Alterar API_BASE_URL e salvar
# Depois fazer rollout
kubectl rollout restart deployment/frontend -n finance-manager
```

---

## 📁 Estrutura de Arquivos Criados

```
✅ Criados:
frontend/src/environments/
├── environment.ts
└── environment.production.ts

frontend/
├── docker-entrypoint.sh
└── nginx.conf

k8s/frontend/
├── deployment.yaml
└── service.yaml

📄 Documentação:
├── REFACTORING_GUIDE.md
├── REFACTORING_CHECKLIST.md
├── USAGE_EXAMPLES.md
├── ANGULAR_ENVIRONMENTS_CONFIG.md
└── README_REFACTORING.md
```

---

## 🎯 O Que Mudou nos Serviços

### Antes
```typescript
export class AuthService {
  private readonly baseUrl: string = 'http://localhost:8080/api/auth';
}
```

### Depois
```typescript
import { environment } from '../../../environments/environment';

export class AuthService {
  private readonly baseUrl: string = `${environment.apiBaseUrl}/auth`;
}
```

---

## 🔍 Verificar se Está Funcionando

### No Navegador
```javascript
// Abrir DevTools (F12) e digitar:
globalThis.__API_BASE_URL__
// Deve retornar a URL configurada
```

### Com curl
```bash
curl http://localhost:8080/index.html | grep API_BASE_URL
# Deve mostrar algo como:
# globalThis.__API_BASE_URL__ = 'http://finance-manager-backend';
```

### No Kubernetes
```bash
kubectl logs deployment/frontend -n finance-manager | grep -i api
```

---

## 📝 Comandos Úteis

```bash
# Desenvolvimento
npm start                    # Servir localmente com auto-reload

# Build
npm run build               # Build para produção

# Docker
docker build -t finance-manager-frontend:v1.0.0 .
docker run -e API_BASE_URL=... -p 80:80 finance-manager-frontend:v1.0.0

# Kubernetes
kubectl apply -f k8s/frontend/
kubectl get pods -n finance-manager
kubectl logs deployment/frontend -n finance-manager
kubectl port-forward svc/frontend 8080:80 -n finance-manager
kubectl set env deployment/frontend API_BASE_URL=... -n finance-manager
```

---

## ⚠️ Erros Comuns

| Erro | Solução |
|------|---------|
| `Cannot find module '@angular/core'` | Rodar `npm install` na pasta `frontend` |
| `API is unreachable` | Verificar se `API_BASE_URL` está correto |
| `CORS error` | Backend precisa permitir requisições do frontend |
| `index.html em branco` | Ver logs do Nginx: `docker logs <container-id>` |

---

## 📚 Documentação Completa

- **REFACTORING_GUIDE.md** - Guia detalhado
- **USAGE_EXAMPLES.md** - Exemplos de uso
- **ANGULAR_ENVIRONMENTS_CONFIG.md** - Config Angular

---

## ✅ Pronto para Produção!

A aplicação agora está:
- ✅ Configurável via ambiente
- ✅ Pronta para Kubernetes
- ✅ Otimizada com Nginx
- ✅ Multi-ambiente (dev, staging, prod)
- ✅ Sem URLs hardcoded

**Próximo passo:** Fazer deploy no seu cluster Kubernetes! 🚀

