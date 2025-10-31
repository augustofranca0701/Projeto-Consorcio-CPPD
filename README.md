# Consorcio-CPPD

**Projeto Integrador Acadêmico – Curso de Análise e Desenvolvimento de Sistemas**

Repositório do projeto **Consórcio CPPD**, desenvolvido como trabalho integrador na faculdade. O objetivo do sistema é gerenciar grupos de consórcio, pagamentos e prêmios, com **backend em Java (Spring Boot)** e **frontend em Angular**.

---

## Tecnologias Utilizadas

**Backend:**
- Java 21
- Spring Boot 3.2.5 (MVC, JPA/Hibernate)
- PostgreSQL
- Flyway (migrações de banco)
- Lombok
- Maven (gerenciamento de dependências)
- API REST

**Frontend:**
- Angular 15+
- TypeScript
- HTML5, CSS3
- Angular Material (componentes e estilos)
- Serviços para comunicação com API REST

---

## Funcionalidades Implementadas

**Backend:**
- CRUD de usuários, grupos, pagamentos e prêmios
- DTOs para atualização e login de usuários
- Serviços e repositórios para abstração de regras de negócio
- Configuração CORS e persistência com PostgreSQL
- Migrações automáticas via Flyway

**Frontend:**
- Telas de login, registro e dashboard
- Visualização e criação de grupos
- Rastreamento de pagamentos (pagos/não pagos)
- Modais para edição de dados e upload de arquivos
- Componentes reutilizáveis (cards, headers, banners)
- Comunicação com backend via serviços Angular

---

## Estrutura do Projeto

Consorcio-CPPD/
├── api-consorcio/ # Backend Java Spring Boot
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/consorcio/api/Controller
│ │ │ ├── java/com/consorcio/api/Service
│ │ │ ├── java/com/consorcio/api/Repository
│ │ │ ├── java/com/consorcio/api/Model
│ │ │ └── resources/application.yml
│ └── pom.xml
└── consorcio-frontend/ # Frontend Angular
├── src/app/
│ ├── components/
│ ├── services/
│ └── views/
├── src/assets/
└── angular.json

---

## Banco de Dados

- Banco principal: **PostgreSQL**
- Configuração de conexão no `application.yml` do backend
- Estrutura criada automaticamente via JPA/Hibernate

---

## Como Executar

### Backend
1. Configure um banco PostgreSQL local (ou remoto) com nome `consorcio`.
2. Ajuste usuário e senha em `src/main/resources/application.yml`.
3. Execute:
```bash
mvn clean install
mvn spring-boot:run
```

### Frontend
1. Instale dependências:
```bash
yarn install
```
2. Execute o servidor de desenvolvimento:
```bash
ng serve
```
3. Acesse `http://localhost:4200/`

---

## Status

- Projeto desenvolvido durante a graduação, parcialmente finalizado
- Funcionalidades principais implementadas
z- Continuará sendo aprimorado para portfólio profissional

z---

z## Contato

zAugusto Mesquita França  
z[LinkedIn](https://www.linkedin.com/in/augustofranca)  
z[Email](mailto:augustofranca0701@gmail.com)

