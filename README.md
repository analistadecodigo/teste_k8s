# Finance Manager

Este é um projeto de gerenciamento financeiro que permite aos usuários controlar suas receitas e despesas. O projeto é dividido em duas partes principais: o backend (API) e o frontend (aplicação web).

## Tecnologias Utilizadas

### Backend

O backend é construído com Java e Spring Boot, seguindo uma arquitetura hexagonal para garantir modularidade e manutenibilidade.

- **Java 17**: Linguagem de programação principal.
- **Spring Boot**: Framework para facilitar o desenvolvimento de aplicações Java.
- **Spring Security**: Para autenticação e autorização.
- **Lombok**: Biblioteca para reduzir o código boilerplate.
- **Bean Validation**: Para validação de dados.
- **PostgreSQL**: Banco de dados relacional.
- **Spring Web**: Para construção de APIs RESTful.
- **Spring Data JPA**: Para acesso a dados com JPA.
- **Arquitetura Hexagonal**: Padrão de arquitetura para desacoplar a lógica de negócio da infraestrutura.

### Frontend

O frontend é uma aplicação web moderna desenvolvida com Angular.

- **Angular 17**: Framework para construção de interfaces de usuário.
- **TypeScript**: Superset de JavaScript que adiciona tipagem estática.
- **Angular Material**: Biblioteca de componentes UI baseada no Material Design.
- **RxJS**: Biblioteca para programação reativa.

## Como Rodar o Projeto

### Backend

1. Certifique-se de ter o Java 17 e o PostgreSQL instalados.
2. Configure as credenciais do banco de dados no arquivo `application.properties` ou `application.yml` do projeto backend.
3. Navegue até o diretório `backend` e execute a aplicação Spring Boot.

```bash
cd ../backend
./mvnw spring-boot:run
```

### Frontend

1. Certifique-se de ter o Node.js (versão 18 ou superior) e o npm (ou yarn) instalados.
2. Certifique-se de ter o Angular CLI versão 17 instalado globalmente:
   ```bash
   npm install -g @angular/cli@17
   ```
3. Navegue até o diretório `frontend`.
4. Instale as dependências.

```bash
npm install
```

5. Inicie o servidor de desenvolvimento.

```bash
npm start
```

A aplicação estará disponível em `http://localhost:4200`.
