# CPPD — API do Consórcio Pequeno de Pouca Duração

API REST desenvolvida em **Spring Boot + PostgreSQL** para gerenciamento de grupos de consórcio, pagamentos e sorteios de prêmios mensais.

---

## Inicialização

### 1. Pré-requisitos
- **Java 21+**
- **PostgreSQL 15+**
- **Maven** (ou usar `mvnw.cmd` incluso no projeto)

### 2. Configuração do banco

No arquivo `api-consorcio/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/consorcio
    username: postgres
   password: SUA_SENHA
```

Crie o banco localmente:

```sql
CREATE DATABASE consorcio;
```

### 3. Executar a API

Dentro da pasta `api-consorcio`, rode:

```bash
.\mvnw.cmd spring-boot:run
```

A API será iniciada em:
```
http://localhost:8080
```

---

## Endpoints da API

### Usuários (`/users`)

#### 1. Criar usuário
**POST** `/users/signup`

```json
{
  "name": "Augusto",
  "email": "augusto@email.com",
  "password": "123456",
  "cpf": "12345678900",
  "phone": "62999999999",
  "address": "Rua X",
  "complement": "Casa 2",
  "city": "Goiânia",
  "state": "GO"
}
```

**Retorno (200 OK):**
```json
{
  "id": 1,
  "name": "Augusto",
  "email": "augusto@email.com",
  "cpf": "12345678900",
  "phone": "62999999999",
  "city": "Goiânia",
  "state": "GO"
}
```

---

#### 2. Login de usuário
**POST** `/users/login`

```json
{
  "email": "augusto@email.com",
  "password": "123456"
}
```

**Retorno:**
```json
{
  "id": 1,
  "name": "Augusto",
  "email": "augusto@email.com"
}
```

---

### Grupos (`/groups`)

#### 3. Criar grupo
**POST** `/groups/{userId}/create`

Exemplo:
```
POST http://localhost:8080/groups/1/create
```

Body:
```json
{
  "name": "Consórcio Teste",
  "valorTotal": 1000,
  "valorParcelas": 200,
  "dataCriacao": "2025-11-12",
  "meses": 5,
  "dataFinal": "2026-12-12",
  "quantidadePessoas": 5,
  "privado": false
}
```

**Retorno:**
```json
{ "message": "Group created successfully!" }
```

---

#### 4. Listar todos os grupos
**GET** `/groups`

Retorna todos os grupos cadastrados.

#### 5. Buscar grupo por ID
**GET** `/groups/{id}`

---

### Pagamentos (`/payments`)

#### 6. Listar pagamentos de um usuário
**GET** `/payments/{userId}`

Exemplo:
```
GET http://localhost:8080/payments/2
```

**Retorno:**
```json
[
  {
    "id": 6,
    "dataVencimento": "2025-12-12",
    "valor": 200,
    "isPaid": true,
    "nomeGrupo": "Consórcio Teste"
  }
]
```

---

#### 7. Efetuar pagamento
**PUT** `/payments/{userId}/{paymentId}`

Exemplo:
```
PUT http://localhost:8080/payments/2/6
```

**Retorno:**
```json
{ "message": "Payment updated successfully!" }
```

---

### Prêmios (`/prizes`)

#### 8. Sortear prêmio do grupo
**POST** `/prizes/sort/{groupId}`

Exemplo:
```
POST http://localhost:8080/prizes/sort/1
```

**Retorno:**
```json
{
  "message": "The user: Augusto has been sorted to prize date 12/12/2025"
}
```

---

## Observações

- A criação de grupo automaticamente gera:
  - Entradas na tabela `prizes` (datas mensais);
  - Entradas na tabela `payments` (parcelas por usuário);
  - Associação no `user_group`.
- O sorteio distribui prêmios entre usuários que ainda não foram sorteados.

---

## Stack

- **Backend:** Spring Boot 3.2.5  
- **ORM:** Hibernate / JPA  
- **Banco:** PostgreSQL  
- **Build:** Maven  
- **Teste de rotas:** Thunder Client (VS Code)

---

## 📁 Estrutura de diretórios

```
api-consorcio/
 ├── src/
 │   ├── main/java/com/consorcio/api/
 │   │   ├── Controller/
 │   │   ├── Model/
 │   │   ├── Repository/
 │   │   ├── Service/
 │   │   └── Utils/
 │   └── resources/application.yml
 ├── pom.xml
 └── README_API.md
```

---

## Autor
**Augusto — Designer, Desenvolvedor Full Stack**  
Projeto CPPD: *Consórcio Pequeno de Pouca Duração* 🌱  
