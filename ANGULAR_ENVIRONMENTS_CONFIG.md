# ⚙️ Configuração do Angular para Environments

## Como adicionar suporte a environments no `angular.json`

Se você quiser ter mais controle sobre qual arquivo de environment usar em cada build, adicione a seguinte configuração ao seu `angular.json`:

### Localização
Editar o arquivo `frontend/angular.json`, na seção `architect.build.options`:

### Versão Angular 17 - Configuração Recomendada

```json
{
  "$schema": "./node_modules/@angular/cli/lib/config/schema.json",
  "version": 1,
  "newProjectRoot": "projects",
  "projects": {
    "frontend": {
      "projectType": "application",
      "root": "",
      "sourceRoot": "src",
      "prefix": "app",
      "architect": {
        "build": {
          "builder": "@angular-devkit/build-angular:application",
          "options": {
            "outputPath": "dist/frontend",
            "index": "src/index.html",
            "browser": "src/main.ts",
            "polyfills": ["zone.js"],
            "tsConfig": "tsconfig.app.json",
            "assets": [
              "src/favicon.ico",
              "src/assets"
            ],
            "styles": [
              "@angular/material/prebuilt-themes/purple-green.css",
              "src/styles.css",
              "src/custom-theme.scss"
            ],
            "scripts": [],
            "server": "src/main.server.ts",
            "prerender": true,
            "ssr": {
              "entry": "server.ts"
            }
          },
          "configurations": {
            "production": {
              "fileReplacements": [
                {
                  "replace": "src/environments/environment.ts",
                  "with": "src/environments/environment.production.ts"
                }
              ],
              "budgets": [
                {
                  "type": "initial",
                  "maximumWarning": "500kb",
                  "maximumError": "1mb"
                },
                {
                  "type": "anyComponentStyle",
                  "maximumWarning": "2kb",
                  "maximumError": "4kb"
                }
              ],
              "outputHashing": "all",
              "optimization": true,
              "buildOptimizer": true,
              "sourceMap": false,
              "namedChunks": false,
              "aot": true,
              "extractLicenses": true
            },
            "development": {
              "optimization": false,
              "extractLicenses": false,
              "sourceMap": true,
              "namedChunks": true
            },
            "staging": {
              "fileReplacements": [
                {
                  "replace": "src/environments/environment.ts",
                  "with": "src/environments/environment.staging.ts"
                }
              ],
              "budgets": [
                {
                  "type": "initial",
                  "maximumWarning": "500kb",
                  "maximumError": "1mb"
                },
                {
                  "type": "anyComponentStyle",
                  "maximumWarning": "2kb",
                  "maximumError": "4kb"
                }
              ],
              "outputHashing": "all"
            }
          },
          "defaultConfiguration": "production"
        },
        "serve": {
          "builder": "@angular-devkit/build-angular:dev-server",
          "configurations": {
            "production": {
              "buildTarget": "frontend:build:production"
            },
            "development": {
              "buildTarget": "frontend:build:development"
            },
            "staging": {
              "buildTarget": "frontend:build:staging"
            }
          },
          "defaultConfiguration": "development"
        }
      }
    }
  }
}
```

---

## Exemplo: Criar Environment para Staging

Se você quiser ter um ambiente de staging com URL diferente:

### 1. Criar arquivo `frontend/src/environments/environment.staging.ts`

```typescript
export const environment = {
  production: true,
  apiBaseUrl: 'https://api-staging.example.com'
};
```

### 2. Atualizar `angular.json` com a configuração acima

### 3. Usar em um build

```bash
# Build para staging
npm run build -- --configuration staging

# Ou servir localmente como staging
npm run ng serve -- --configuration staging
```

---

## Comandos de Build com Environments

```bash
# Build para desenvolvimento (padrão)
npm run build

# Build para produção
npm run build -- --configuration production

# Build para staging
npm run build -- --configuration staging

# Servir em desenvolvimento
npm start

# Servir como produção
npm run ng serve -- --configuration production

# Servir como staging
npm run ng serve -- --configuration staging
```

---

## Como Funciona

1. **Em Desenvolvimento (`development`):**
   - Usa `src/environments/environment.ts`
   - `apiBaseUrl: 'http://localhost:8080/api'`
   - Build desotimizado, source maps habilitados

2. **Em Produção (`production`):**
   - Usa `src/environments/environment.production.ts`
   - `apiBaseUrl` vem da variável global `window.__API_BASE_URL__`
   - Build otimizado, sem source maps

3. **Em Staging (`staging`):**
   - Usa `src/environments/environment.staging.ts`
   - `apiBaseUrl: 'https://api-staging.example.com'` (hardcoded)
   - Build otimizado, sem source maps

---

## ⚠️ Notas Importantes

### Angular 17 vs Versões Anteriores

No Angular 17, o sistema de build mudou. Os "environments" clássicos foram descontinuados, mas você ainda pode usar `fileReplacements` para ter comportamento similar.

**Alternativa Recomendada (Angular 17+):**
Ao invés de `fileReplacements`, você pode importar diretamente:

```typescript
// Em qualquer serviço
import { environment } from '../environments/environment';

// O Angular automaticamente escolhe a versão correta baseado no build
```

Isso funciona porque o Angular CLI resolve `environment.ts` para `environment.production.ts` automaticamente em builds de produção.

---

## Variáveis de Ambiente em Runtime

Para máxima flexibilidade em Kubernetes/Docker, recomendamos manter a abordagem atual onde a URL é injetada em tempo de execução via `index.html`.

Isso permite:
- ✅ Mesma imagem Docker para todos os ambientes
- ✅ Alterar URL sem rebuild
- ✅ Integração perfeita com ConfigMap do Kubernetes
- ✅ Mais controle em CI/CD

