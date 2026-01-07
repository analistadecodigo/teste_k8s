# 🔧 Exemplos Práticos de Uso

## 1. Desenvolvimento Local

### Rodando a aplicação localmente
```bash
cd frontend
npm install
npm start
```
A aplicação acessará a API em `http://localhost:8080/api` (conforme definido em `environment.ts`)

---

## 2. Build de Produção

### Compilar para produção
```bash
cd frontend
npm run build
```

O build será otimizado e usará `environment.production.ts`, onde a URL será lida da variável global `window.__API_BASE_URL__`.

---

## 3. Docker Compose Local

### Arquivo: `docker-compose.yml` (exemplo)
```yaml
version: '3.8'

services:
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/finance_db
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: password
    depends_on:
      - postgres

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    environment:
      API_BASE_URL: http://backend:8080/api
    depends_on:
      - backend

  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
      POSTGRES_DB: finance_db
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### Executar
```bash
docker-compose up
# Acesse em http://localhost
```

---

## 4. Kubernetes - Desenvolvimento

### Deploy local (Minikube/Kind)
```bash
# 1. Criar namespace
kubectl create namespace finance-manager

# 2. Aplicar ConfigMap com URL de desenvolvimento
kubectl apply -f k8s/frontend/configmap.yaml

# 3. Aplicar Deployment e Service
kubectl apply -f k8s/frontend/deployment.yaml
kubectl apply -f k8s/frontend/service.yaml

# 4. Verificar status
kubectl get deployment -n finance-manager
kubectl get svc -n finance-manager

# 5. Acessar a aplicação
kubectl port-forward svc/frontend 8080:80 -n finance-manager
# Acesse em http://localhost:8080
```

---

## 5. Kubernetes - Staging

### ConfigMap customizado para Staging
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: frontend-config
  namespace: finance-manager
data:
  API_BASE_URL: https://api-staging.example.com
```

Aplicar:
```bash
kubectl apply -f k8s/frontend/configmap-staging.yaml
```

---

## 6. Kubernetes - Produção

### ConfigMap para Produção
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: frontend-config
  namespace: finance-manager
data:
  API_BASE_URL: https://api.example.com
```

### Também criar um Ingress para acesso externo
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: frontend-ingress
  namespace: finance-manager
spec:
  ingressClassName: nginx
  rules:
  - host: app.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: frontend
            port:
              number: 80
```

---

## 7. Alterar URL da API em Tempo de Execução

### Método 1: Atualizar ConfigMap
```bash
kubectl set env deployment/frontend API_BASE_URL=http://novo-backend -n finance-manager
```

Isso força um rollout do deployment com o novo valor.

### Método 2: Editar ConfigMap
```bash
kubectl edit configmap frontend-config -n finance-manager
# Alterar o valor de API_BASE_URL e salvar
# Os pods existentes precisarão ser recriados para pegar o novo valor
```

### Método 3: Recriar pods após atualizar ConfigMap
```bash
kubectl rollout restart deployment/frontend -n finance-manager
```

---

## 8. Verificar a URL Configurada

### Ver logs do container
```bash
kubectl logs deployment/frontend -n finance-manager
```

### Executar comando dentro do container
```bash
kubectl exec -it deployment/frontend -n finance-manager -- sh
# Dentro do container
cat /usr/share/nginx/html/index.html | grep API_BASE_URL
```

---

## 9. Exemplo de Teste com curl

```bash
# Verificar se o frontend está respondendo
curl -I http://localhost:80

# Ver o index.html com a URL injetada
curl http://localhost:80 | grep API_BASE_URL
```

---

## 10. Troubleshooting

### Problema: Frontend conecta mas API não responde
**Solução:** Verificar se o ConfigMap tem a URL correta
```bash
kubectl get configmap frontend-config -n finance-manager -o yaml
```

### Problema: Status do pod é "Running" mas aplicação não carrega
**Solução:** Verificar logs
```bash
kubectl logs pod/<pod-name> -n finance-manager
```

### Problema: API_BASE_URL não está sendo substituída no index.html
**Solução:** Garantir que o script de entrypoint executou corretamente
```bash
# Dentro do container
env | grep API_BASE_URL
cat /usr/share/nginx/html/index.html
```

### Problema: CORS errors
**Solução:** Garantir que a URL da API está acessível e configurada corretamente
```bash
# De dentro do container, testar conectividade
curl -I <API_BASE_URL>/health
```

---

## 11. CI/CD Pipeline Example (GitHub Actions)

```yaml
name: Build and Deploy Frontend

on:
  push:
    branches: [main]
    paths:
      - 'frontend/**'

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Build Docker Image
        run: |
          docker build -t finance-manager-frontend:${{ github.sha }} ./frontend
          docker tag finance-manager-frontend:${{ github.sha }} finance-manager-frontend:latest
      
      - name: Push to Registry
        run: |
          docker push finance-manager-frontend:${{ github.sha }}
          docker push finance-manager-frontend:latest
      
      - name: Deploy to Kubernetes
        run: |
          kubectl set image deployment/frontend \
            frontend=finance-manager-frontend:${{ github.sha }} \
            -n finance-manager
```

---

## 12. Variáveis de Ambiente Suportadas

| Variável | Obrigatória | Padrão | Ambiente |
|----------|------------|--------|----------|
| `API_BASE_URL` | Não | `http://finance-manager-backend` | K8s/Docker |

### Exemplo com múltiplas variáveis (future)
Se precisar adicionar mais variáveis no futuro:

1. Adicionar em `index.html`:
```html
<script>
  globalThis.__API_BASE_URL__ = '${API_BASE_URL}';
  globalThis.__OTHER_VAR__ = '${OTHER_VAR}';
</script>
```

2. Adicionar em `environment.production.ts`:
```typescript
export const environment = {
  production: true,
  apiBaseUrl: getEnvVar('__API_BASE_URL__', 'http://finance-manager-backend'),
  otherVar: getEnvVar('__OTHER_VAR__', 'default-value')
};

function getEnvVar(name: string, defaultValue: string): string {
  return typeof window !== 'undefined' && (window as any)[name] 
    ? (window as any)[name] 
    : defaultValue;
}
```

3. Adicionar ao ConfigMap:
```yaml
data:
  API_BASE_URL: http://finance-manager-backend
  OTHER_VAR: other-value
```

4. Atualizar `docker-entrypoint.sh` para exportar a variável.

