# Projeto Consórcio — Documentação Oficial
Sistema completo para gerenciamento de grupos de consórcio, incluindo API (Spring Boot),
frontend (Angular) e infraestrutura Docker.
Este README unificado substitui os arquivos README_API.md e README_FRONT.md.

---
# 📚 Índice
1. Visão Geral do Projeto  
2. Arquitetura Geral  
3. Tecnologias Utilizadas  
4. Estrutura de Pastas  
5. Configuração de Ambiente  
6. Variáveis de Ambiente (.env)  
7. Backend (API - Spring Boot)  
8. Frontend (Angular + NGINX)  
9. Docker (API + Front + DB)  
10. Scripts de Dev e Produção  
11. Swagger (Documentação automática dos endpoints)  
12. Como rodar tudo com um comando  
13. Troubleshooting (erros comuns)  
14. Recomendações de produção  
15. Contribuindo / Pull Requests  
16. Licença  

---
# 1. Visão Geral do Projeto
Este sistema simula o funcionamento de um consórcio, oferecendo:
- Cadastro e login de usuários  
- Criação de grupos de consórcio  
- Acompanhamento de pagamentos  
- Emissão de boletos  
- Simulação de contemplação  
- Tela administrativa do usuário  
- UI responsiva  

---
# 2. Arquitetura Geral
A arquitetura segue o padrão moderno:

```
Frontend (Angular + NGINX)
        |
API Gateway do Nginx
        |
Backend (Spring Boot)
        |
PostgreSQL (DB)
```

Todos os serviços rodam dentro de containers isolados, comunicando-se pela network interna
do Docker.

---
# 3. Tecnologias Utilizadas
## Backend
- Java 21  
- Spring Boot 3  
- Spring Web  
- Spring Data JPA  
- PostgreSQL  
- Flyway (opcional)  
- Docker  
- Maven  

## Frontend
- Angular 17  
- TypeScript  
- NGINX para produção  
- Docker  

## Infraestrutura
- Docker  
- Docker Compose  
- Rede bridge dedicada  
- Volumes para persistência  

---
# 4. Estrutura de Pastas
```
Projeto-Consorcio-CPPD
│
├── api-consorcio        # Backend (Spring)
├── consorcio-frontend   # Frontend (Angular + NGINX)
└── docker-compose.yml   # Orquestração
```

---
# 5. Configuração de Ambiente
Antes de rodar o projeto, copie o `.env.example` do backend:

```
cp api-consorcio/env.example .env
```

Edite os valores conforme desejado.

---
# 6. Variáveis de Ambiente (.env)
```
# Banco de Dados
DB_HOST=db
DB_PORT=5432
DB_NAME=consorcio
DB_USER=postgres
DB_PASSWORD=guardachuva

# Spring
SPRING_PROFILES_ACTIVE=dev

# Frontend
VITE_API_URL=http://localhost:8080
```

---
# 7. Backend (API - Spring Boot)
## Estrutura
```
api-consorcio/
├── src/main/java/com/consorcio/api
│   ├── Controller/
│   ├── Service/
│   ├── Repository/
│   ├── Model/
│   └── ApiApplication.java
└── src/main/resources
    ├── application-dev.yml
    └── application-prod.yml
```

## Como rodar localmente (sem Docker)
```
cd api-consorcio
./mvnw spring-boot:run
```

---
# 8. Frontend (Angular + NGINX)
## Como rodar localmente (sem Docker)
```
cd consorcio-frontend
npm install
ng serve --configuration development
```

## Produção (Angular build + NGINX)
O Dockerfile.prod cuida de tudo.

---
# 9. Docker (API + Front + DB)
O arquivo docker-compose.yml inicia tudo:

```
docker compose up --build
```

Serviços:
- http://localhost:8080 → API  
- http://localhost:4200 → Frontend  

---
# 10. Scripts de Dev e Produção
## Backend
```
api-consorcio/run-dev.ps1
api-consorcio/run-prod.ps1
```

## Frontend
```
consorcio-frontend/run-dev.ps1
consorcio-frontend/run-prod.ps1
```

---
# 11. Swagger (Documentação automática)
O Swagger será adicionado via dependência:

### Dependência no pom.xml
```
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.4.0</version>
</dependency>
```

### Acesso
Após iniciar a API:
```
http://localhost:8080/swagger-ui.html
```

---
# 12. Como rodar tudo com um comando
```
docker compose up --build
```

---
# 13. Troubleshooting
## Porta 5432 ocupada
→ seu PostgreSQL do Windows está rodando  
Solução:
- Pare o serviço do Windows (postgresql-x64-18)
- Reinicie o Docker

## “Connection refused” na API
→ DB não está pronto  
→ docker compose up deve resolver, ele usa depends_on

## Angular não acessa a API
→ API_Host errado no environment.ts
→ no docker deve ser:
```
api:8080
```
---
# 14. Recomendações de Produção
✔ logs centralizados  
✔ senha do banco em secrets, nunca versionada  
✔ healthcheck no docker  
✔ build multi-stage (já implementado)  
✔ volumes persistentes  
✔ reverse proxy com HTTPS (NGINX)  
✔ backups do banco  

---
# 15. Contribuindo
1. Crie uma branch  
2. Faça commits claros  
3. Abra PR descritivo  

---
# 16. Licença
MIT License — livre para uso pessoal e comercial.
